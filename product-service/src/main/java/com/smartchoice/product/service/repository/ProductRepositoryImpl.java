package com.smartchoice.product.service.repository;

import com.smartchoice.product.service.dto.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductRepositoryImpl.class);
    private RedisTemplate template;

    public ProductRepositoryImpl(@Qualifier("redisTemplate") RedisTemplate template) {
        this.template = template;
    }

    @Override
    public boolean isProductGroupExist(String productName) {
        return template.hasKey(productName);
    }

    @Override
    public List<Product> getProductGroup(String productName) {
        return List.copyOf(template.opsForSet().members(productName));
    }

    @Override
    public void saveProductGroup(String productName, List<Product> products) {
        template.opsForSet().add(productName, products);
        template.expire(productName, 30, TimeUnit.SECONDS);
    }

    @Override
    public void updateProductGroup(String productName, List<Product> products) {
        deleteProductGroup(productName);
        saveProductGroup(productName, products);
        LOGGER.info("Update cache {}", getProductGroup(productName).size());
        template.expire(productName, 30, TimeUnit.SECONDS);
    }

    @Override
    public void deleteProductGroup(String productName) {
        template.delete(productName);
    }
}

