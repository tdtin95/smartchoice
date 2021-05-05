package com.smarchoice.product.adapter.service.config;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

@TestConfiguration
public class RedisTestConfiguration {

    private RedisServer redisServer;

    public RedisTestConfiguration(final RedisProperties redisProperties) {
        this.redisServer = new RedisServer(redisProperties.getRedisPort());
    }

    @PostConstruct
    public void postConstruct() {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
    }
}
