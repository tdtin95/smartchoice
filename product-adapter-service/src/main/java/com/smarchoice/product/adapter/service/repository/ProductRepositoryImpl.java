package com.smarchoice.product.adapter.service.repository;

import com.smarchoice.product.adapter.service.dto.Product;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class ProductRepositoryImpl implements ProductRepository {


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
        return template.opsForList().range(productName, 0, -1);

    }

    @Override
    public void saveProductGroup(String productName, List<Product> products) {
        template.opsForList().rightPushAll(productName, products);
        template.expire(productName, 60, TimeUnit.SECONDS);
    }

    @Override
    public void updateProductGroup(String productName, List<Product> products) {
        deleteProductGroup(productName);
        saveProductGroup(productName, products);
        template.expire(productName, 60, TimeUnit.SECONDS);
    }

    @Override
    public void deleteProductGroup(String productName) {
        template.delete(productName);
    }
}
