package com.smartchoice.product.service.repository;

import com.smartchoice.product.service.entity.ProductGroup;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@org.springframework.stereotype.Repository
public class ProductGroupRepository implements Repository<ProductGroup> {

    @Value("${product.cache.ttl}")
    private long cacheTimeToLive;
    private RedisTemplate template;

    public ProductGroupRepository(@Qualifier("redisTemplate") RedisTemplate template) {
        this.template = template;
    }


    @Override
    public void save(ProductGroup productGroup) {
        template.opsForValue().set(productGroup.getProductName(), productGroup, cacheTimeToLive, TimeUnit.SECONDS);
    }

    @Override
    public boolean existsById(String productName) {
        return template.opsForValue().get(productName) != null;
    }

    @Override
    public ProductGroup findById(String productName) {
        return (ProductGroup) template.opsForValue().get(productName);
    }
}

