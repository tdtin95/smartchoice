package com.smartchoice.product.service.listener;

import com.smartchoice.product.service.dto.Product;
import com.smartchoice.product.service.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@KafkaListener(topics = "product-message-queue")
public class KafkaConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    private ProductRepository productRepository;

    @Autowired
    public KafkaConsumer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @KafkaHandler(isDefault = true)
    void listenDefault(List<Product> products) {
        LOGGER.info("Receive product form Kafka, size {}", products.size());
        if (products.size() > 0) {
            String productName = products.get(0).getProductName();
            productRepository.updateProductGroup(productName, products);
            LOGGER.info("After get {} from kafka {} ", productName, productRepository.getProductGroup(productName).size());

        }
    }
}
