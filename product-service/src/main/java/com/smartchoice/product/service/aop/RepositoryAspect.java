package com.smartchoice.product.service.aop;

import com.smartchoice.product.service.entity.ProductGroup;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RepositoryAspect {

    private RedisTemplate template;
    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryAspect.class);

    public RepositoryAspect(@Qualifier("redisTemplate") RedisTemplate template) {
        this.template = template;
    }


    @Before("execution(* com.smartchoice.product.service.repository.ProductGroupRepository.save(..))")
    @Async
    public void setExpiredForCache(JoinPoint joinPoint) {
//        Object[] methodArguments = joinPoint.getArgs();
//        ProductGroup entity = (ProductGroup) methodArguments[0];
//        LOGGER.info("Cache product group {}", entity.getProductName());
//        template.expire(entity.getProductName(), 10, TimeUnit.SECONDS);

    }
}
