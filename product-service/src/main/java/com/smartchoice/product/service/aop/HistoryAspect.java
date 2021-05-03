package com.smartchoice.product.service.aop;

import com.smartchoice.product.service.dto.History;
import com.smartchoice.product.service.listener.HistoryProducer;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import static com.smartchoice.product.service.service.ProductInformationService.PRODUCT_NAME;

@Aspect
@Component
public class HistoryAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryAspect.class);
    private HistoryProducer historyProducer;

    @Autowired
    public HistoryAspect(HistoryProducer historyProducer) {
        this.historyProducer = historyProducer;
    }


    @Before("execution(* com.smartchoice.product.service.service.ProductInformationService.getProductInformation(..))")
    @Async
    public void storeSearchHistory(JoinPoint joinPoint) {
        Object[] methodArguments = joinPoint.getArgs();
        MultiValueMap<String, String> queryParams = (MultiValueMap<String, String>) methodArguments[0];
        String productName = queryParams.getFirst(PRODUCT_NAME);

        if (StringUtils.isNotBlank(productName)) {
            History history = History.builder().actionOn(new Date())
                    .productName(productName)
                    .username("username")
                    .id(UUID.randomUUID().toString())
                    .build();
            historyProducer.storeHistory(history);
        }

    }
}
