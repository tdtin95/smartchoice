package com.smarchoice.product.adapter.service.service;

import com.smarchoice.product.adapter.service.dto.Product;
import com.smarchoice.product.adapter.service.dto.Provider;
import com.smarchoice.product.adapter.service.event.ProductProducer;
import com.smarchoice.product.adapter.service.exception.IncompleteException;
import com.smarchoice.product.adapter.service.repository.ProductRepository;
import com.smarchoice.product.adapter.service.resource.ProviderResource;
import com.smarchoice.product.adapter.service.resource.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Service
public class ProductService {

    public static final String PRODUCT_NAME = "productName";
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private ResourceFactory resourceFactory;
    private ProductRepository repository;
    private ProductProducer productProducer;


    /**
     * Get products from 3rd providers
     *
     * @param queryParams request query params
     * @return products that consumed from providers
     */
    public List<Product> getProducts(MultiValueMap<String, String> queryParams) {
        String productName = queryParams.getFirst(PRODUCT_NAME);

        List<Callable<List<Product>>> tasks = new ArrayList<>();

        for (ProviderResource<? extends Product> resource : resourceFactory.getResources()) {
            tasks.add(createTask(productName, resource.getProvider(), resource, queryParams));
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
                } catch (Exception e) {
                    LOGGER.error("Cannot not get result", e);
                    throw new IncompleteException(e.getMessage(), e);
                }

            }
            productProducer.sendToMessageQueue(products);
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
            if (repository.isExist(productName, provider)) {
                return repository.search(productName, provider);
            }
            List<Product> products = resource.findProduct(queryParams);

            if (!products.isEmpty()) {
                repository.save(productName, provider, products);
            }
            return products;
        };
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
    public void setProductProducer(ProductProducer productProducer) {
        this.productProducer = productProducer;
    }
}
