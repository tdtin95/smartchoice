package com.smartchoice.product.service.listener;

import com.smartchoice.product.service.dto.History;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
public class HistoryProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryProducer.class);
    private KafkaTemplate<String, History> template;
    @Value(value = "${message.queue.kafka.topic}")
    private String historyTopicName;

    @Autowired
    public HistoryProducer(KafkaTemplate<String, History> template) {
        this.template = template;
    }

    public void storeHistory(History history) {
        if (history != null) {
            ListenableFuture<SendResult<String, History>> future = template.send(historyTopicName, history);

            future.addCallback(new ListenableFutureCallback<>() {
                @Override
                public void onSuccess(SendResult<String, History> result) {
                    LOGGER.info("Sent history with offset=[" + result.getRecordMetadata().offset() + "]");
                }

                @Override
                public void onFailure(Throwable ex) {
                    LOGGER.info("Unable to send history=["
                            + history + "] due to : " + ex.getMessage());
                }
            });
        }
    }

}
