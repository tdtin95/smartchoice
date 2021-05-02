package com.smarchoice.product.adapter.service.service;

import com.smarchoice.product.adapter.service.dto.Product;
import com.smarchoice.product.adapter.service.repository.ProductRepository;
import com.smarchoice.product.adapter.service.resource.Provider;
import com.smarchoice.product.adapter.service.resource.ProviderResource;
import com.smarchoice.product.adapter.service.resource.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


@Service
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private ResourceFactory resourceFactory;

    private ProductRepository repository;


    private KafkaTemplate<String, String> kafkaTemplate;

    //TODO need to be refactor
    public List<Product> getProducts(MultiValueMap<String, String> queryParams) {
        String productName = queryParams.getFirst("productName");
        // repository.deleteProductGroup(productName);
        if (repository.isProductGroupExist(productName)) {
            return repository.getProductGroup(productName);
        }

        sendMessage("Tdtin");


        List<Callable<List<Product>>> callableTasks = new ArrayList<>();
        for (ProviderResource resource : resourceFactory.getResources()) {
            Callable<List<Product>> task = () -> {
                List<Product> product = resource.findProduct(queryParams);
                //sendMessage();

                return product;
            };
            callableTasks.add(task);
        }

        ExecutorService executor = Executors.newFixedThreadPool(10);
        try {
            List<Future<List<Product>>> futures = executor.invokeAll(callableTasks);
            List<Product> products = new ArrayList<>();
            for (Future<List<Product>> future : futures) {
                try {
                    products.addAll(future.get());
                } catch (ExecutionException e) {
                   LOGGER.error("Cannot not get result");
                }

            }
            repository.saveProductGroup(productName, products);
            return products;
        } catch (InterruptedException e) {
            return new ArrayList<>();
        } finally {
            executor.shutdown();
        }
    }

    public void sendMessage(String message) {
        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(Provider.SHOPEE.getName(), message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                LOGGER.info("Sent message=[" + message +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                LOGGER.info("Unable to send message=["
                        + message + "] due to : " + ex.getMessage());
            }
        });
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
    public void setKafkaTemplate(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
}
