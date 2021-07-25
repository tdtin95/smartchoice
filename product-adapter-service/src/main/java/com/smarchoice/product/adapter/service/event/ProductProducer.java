package com.smarchoice.product.adapter.service.event;

import com.smarchoice.product.adapter.service.dto.Product;
import com.smarchoice.product.adapter.service.dto.ProductGroup;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;

@Component
public class ProductProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductProducer.class);
    private final KafkaTemplate<String, ProductGroup> kafkaTemplate;

    @Value("${message.queue.product.topic}")
    private String messageQueueTopic;

    @Autowired
    public ProductProducer(KafkaTemplate<String, ProductGroup> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToMessageQueue(List<Product> products) {
        if (CollectionUtils.isNotEmpty(products)) {
            ProductGroup productGroup = ProductGroup.newBuilder().setProducts(products).build();
            ListenableFuture<SendResult<String, ProductGroup>> future = kafkaTemplate.send(messageQueueTopic, productGroup);

            future.addCallback(new ListenableFutureCallback<>() {
                @Override
                public void onSuccess(SendResult<String, ProductGroup> result) {
                    LOGGER.info("Sent products with offset=[" + result.getRecordMetadata().offset() + "]");
                }

                @Override
                public void onFailure(Throwable ex) {
                    LOGGER.info("Unable to send products=["
                            + products + "] due to : " + ex.getMessage());
                }
            });
        }
    }
}
