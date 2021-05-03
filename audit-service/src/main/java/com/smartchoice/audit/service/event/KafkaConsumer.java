package com.smartchoice.audit.service.event;

import com.smartchoice.audit.service.entity.History;
import com.smartchoice.audit.service.repository.HistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "customer-history")
public class KafkaConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);


    private final HistoryRepository repository;

    @Autowired
    public KafkaConsumer(HistoryRepository repository) {
        this.repository = repository;
    }

    @KafkaHandler(isDefault = true)
    void listenDefault(History history) {
        LOGGER.info("Receive search event");
        repository.save(history);
    }
}
