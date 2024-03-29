package com.smartchoice.product.service.event;

import com.smartchoice.product.service.dto.Product;
import com.smartchoice.product.service.entity.ProductGroup;
import com.smartchoice.product.service.repository.ProductGroupRepository;
import com.smartchoice.product.service.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@KafkaListener(topics = "${message.queue.product.topic}")
public class ProductConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductConsumer.class);


    private final Repository<ProductGroup> repository;

    @Autowired
    public ProductConsumer(ProductGroupRepository repository) {
        this.repository = repository;
    }

    @KafkaHandler(isDefault = true)
    void listen(List<Product> products) {
        LOGGER.info("Receive product form Kafka, size {}", products.size());
        if (products.size() > 0) {
            String productName = products.get(0).getProductName();
            repository.save(new ProductGroup(productName, products));
        }
    }
}
