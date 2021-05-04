package com.smartchoice.product.service.listener;

import com.smartchoice.product.service.config.RedisTestConfiguration;
import com.smartchoice.product.service.dto.Product;
import com.smartchoice.product.service.dto.Provider;
import com.smartchoice.product.service.entity.ProductGroup;
import com.smartchoice.product.service.repository.Repository;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@DirtiesContext
@SpringBootTest(classes = RedisTestConfiguration.class)
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:7071", "port=7071"})
public class ProductConsumerTest {


    @Autowired
    KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    @Value("${message.queue.product.topic}")
    private String topic;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired
    private Repository<ProductGroup> repository;

    @Before
    public void setUp() {
        for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer,
                    embeddedKafkaBroker.getPartitionsPerTopic());
        }
    }

    @Test
    public void listen_shouldCacheProducts_whenReceiveMessageFromKafka() throws InterruptedException {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        Producer<String, List<Product>> producer = new DefaultKafkaProducerFactory<>(
                configs, new StringSerializer(), new JsonSerializer()).createProducer();


        Product product = Product.builder().productName("coat")
                .price(100).provider(Provider.SHOPEE)
                .discountRate(1).build();
        producer.send(new ProducerRecord<>(topic, product.getProductName(), List.of(product)));
        producer.flush();
        Thread.sleep(100);
        Assertions.assertTrue(repository.existsById(product.getProductName()));

    }

    @Test
    public void listen_shouldNotCacheEmptyProducts() throws InterruptedException {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        Producer<String, List<Product>> producer = new DefaultKafkaProducerFactory<>(
                configs, new StringSerializer(), new JsonSerializer()).createProducer();
        String id = "product-id";
        producer.send(new ProducerRecord<>(topic, id, List.of()));
        producer.flush();
        Thread.sleep(100);
        Assertions.assertFalse(repository.existsById(id));

    }
}
