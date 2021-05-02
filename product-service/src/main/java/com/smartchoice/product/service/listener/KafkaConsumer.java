package com.smartchoice.product.service.listener;

import com.smartchoice.product.service.dto.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@KafkaListener(topics = "product-message-queue")
public class KafkaConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
    @KafkaHandler
    void listen(String message) {
        LOGGER.info("KafkaHandler[String] {}", message);
    }

    @KafkaHandler(isDefault = true)
    void listenDefault(List<Product> products) {
        LOGGER.info("Receive product form Kafka, size {}", products.size());
    }
}
