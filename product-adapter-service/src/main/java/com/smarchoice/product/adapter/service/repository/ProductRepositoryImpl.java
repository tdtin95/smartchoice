package com.smarchoice.product.adapter.service.repository;

import com.smarchoice.product.adapter.service.dto.Product;
import com.smarchoice.product.adapter.service.dto.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
@SuppressWarnings("rawtypes")
public class ProductRepositoryImpl implements ProductRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductRepositoryImpl.class);
    private final RedisTemplate template;
    @Value("${cache.product.group.lifespan}")
    private long cacheLifeSpan;

    public ProductRepositoryImpl(@Qualifier("redisTemplate") RedisTemplate template) {
        this.template = template;
    }

    @Override
    public boolean isExist(String productName, Provider provider) {
        return template.hasKey(buildProductGroupIdentifier(productName, provider));
    }

    @Override
    public List<Product> search(String productName, Provider provider) {
        return template.opsForList().range(buildProductGroupIdentifier(productName, provider), 0, -1);
    }

    @Override
    public void save(String productName, Provider provider, List<Product> products) {
        String identifier = buildProductGroupIdentifier(productName, provider);
        LOGGER.info("update {}", identifier);
        delete(productName, provider);
        template.opsForList().rightPushAll(identifier, products);
        template.expire(identifier, cacheLifeSpan, TimeUnit.SECONDS);
    }

    @Override
    public void delete(String productName, Provider provider) {
        template.delete(buildProductGroupIdentifier(productName, provider));
    }

    /**
     * Build product group cache identifier that is the combination of product name and
     * provider that product belongs to
     *
     * @param productName product name
     * @param provider    3rd party provider {@link Provider}
     * @return product group identifier
     */
    private String buildProductGroupIdentifier(String productName, Provider provider) {
        return productName + "-" + provider;
    }
}
