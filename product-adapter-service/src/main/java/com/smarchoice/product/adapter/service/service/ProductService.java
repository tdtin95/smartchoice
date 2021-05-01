package com.smarchoice.product.adapter.service.service;

import com.smarchoice.product.adapter.service.dto.Product;
import com.smarchoice.product.adapter.service.resource.ProviderResource;
import com.smarchoice.product.adapter.service.resource.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class ProductService {
    private ResourceFactory resourceFactory;

    //TODO need to be refactor
    public List<Product> getProducts(MultiValueMap<String, String> queryParams) {
        List<Callable<List<Product>>> callableTasks = new ArrayList<>();
        for (ProviderResource resource : resourceFactory.getResources()) {
            Callable<List<Product>> task = () -> resource.findProduct(queryParams);
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
                    System.out.println("Cannot get result");
                }

            }
            return products;
        } catch (InterruptedException e) {
            return new ArrayList<>();
        } finally {
            executor.shutdown();
        }
    }

    @Autowired
    public void setResourceFactory(ResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }
}
