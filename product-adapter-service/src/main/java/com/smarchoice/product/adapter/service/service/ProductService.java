package com.smarchoice.product.adapter.service.service;

import com.smarchoice.product.adapter.service.dto.Product;
import com.smarchoice.product.adapter.service.exception.IncompleteException;
import com.smarchoice.product.adapter.service.repository.ProductRepository;
import com.smarchoice.product.adapter.service.resource.Provider;
import com.smarchoice.product.adapter.service.resource.ProviderResource;
import com.smarchoice.product.adapter.service.resource.ResourceFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


@Service
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private ResourceFactory resourceFactory;
    private ProductRepository repository;
    private KafkaTemplate<String, List<Product>> kafkaTemplate;

    @Value("${message.queue.product.topic}")
    private String messageQueueTopic;

    /**
     * Get products from 3rd providers
     *
     * @param queryParams request query params
     * @return products that consumed from providers
     */
    public List<Product> getProducts(MultiValueMap<String, String> queryParams) {
        String productName = queryParams.getFirst("productName");

        List<Callable<List<Product>>> tasks = new ArrayList<>();

        for (Map.Entry<Provider, ProviderResource> resource : resourceFactory.getResources().entrySet()) {
            tasks.add(createTask(productName, resource.getKey(), resource.getValue(), queryParams));
        }

        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            List<Future<List<Product>>> futures = executor.invokeAll(tasks);
            List<Product> products = new ArrayList<>();
            for (Future<List<Product>> future : futures) {
                try {
                    List<Product> consumedProducts = future.get();
                    if (consumedProducts != null) {
                        products.addAll(consumedProducts);
                    }
                } catch (ExecutionException e) {
                    LOGGER.error("Cannot not get result", e);
                    throw new IncompleteException(e.getMessage(), e);
                }

            }
            sendToMessageQueue(products);
            return products;
        } catch (InterruptedException e) {
            LOGGER.error("Process interrupted", e);
            return new ArrayList<>();
        } finally {
            executor.shutdown();
        }
    }

    /**
     * Create task to consume product for 3rd provider
     *
     * @param productName product name
     * @param provider    {@link Provider}
     * @param resource    {@link ProviderResource}
     * @param queryParams query params from request
     * @return task
     */
    private Callable<List<Product>> createTask(String productName,
                                               Provider provider,
                                               ProviderResource resource,
                                               MultiValueMap<String, String> queryParams) {
        return () -> {
            if (repository.isProductGroupExist(productName, provider)) {
                return repository.getProductGroup(productName, provider);
            }
            List<Product> products = resource.findProduct(queryParams);
            repository.saveProductGroup(productName, provider, products);
            return products;
        };
    }

    private void sendToMessageQueue(List<Product> products) {
        if (CollectionUtils.isNotEmpty(products)) {
            ListenableFuture<SendResult<String, List<Product>>> future =
                    kafkaTemplate.send(messageQueueTopic, products);

            future.addCallback(new ListenableFutureCallback<>() {

                @Override
                public void onSuccess(SendResult<String, List<Product>> result) {
                    LOGGER.info("Sent products with offset=[" + result.getRecordMetadata().offset() + "]");
                }

                @Override
                public void onFailure(Throwable ex) {
                    LOGGER.info("Unable to send products=["
                            + products + "] due to : " + ex.getMessage());
                }
            });
        }

    }

    @Autowired
    public void setResourceFactory(ResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    @Autowired
    public void setRepository(ProductRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setKafkaTemplate(KafkaTemplate<String, List<Product>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
}
