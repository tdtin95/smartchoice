package com.smarchoice.product.adapter.service.config;

import com.smarchoice.product.adapter.service.resource.Provider;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    //For assignment we just init partition of topic as one
    //In real production, Kafka server should be set up by operation admin
    //base on the need of system
    @Bean
    public NewTopic shopeeTopic() {
        return new NewTopic(Provider.SHOPEE.getName(), 1, (short) 1);
    }

    @Bean
    public NewTopic lazadaTopic() {
        return new NewTopic(Provider.LAZADA.getName(), 1, (short) 1);
    }

    @Bean
    public NewTopic tikiTopic() {
        return new NewTopic(Provider.TIKI.getName(), 1, (short) 1);
    }
}