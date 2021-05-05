/*
 * Copyright by Axon Ivy (Lucerne), all rights reserved.
 */
package com.smarchoice.product.adapter.service.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.smarchoice.product.adapter.service.ProductTestUtil;
import com.smarchoice.product.adapter.service.dto.Product;
import com.smarchoice.product.adapter.service.resource.Provider;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@RunWith(SpringRunner.class)
@DirtiesContext
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:7072", "port=7072"})
public class ProductProducerTest {

    @Autowired
    private ProductProducer producer;

    @Value("${message.queue.product.topic}")
    private String topic;


    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    private BlockingQueue<ConsumerRecord<String, List<Product>>> records;
    private KafkaMessageListenerContainer<String, String> container;

    @MockBean
    private KafkaTemplate<String, List<Product>> template;

    @Before
    public void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps(topic, "false", embeddedKafkaBroker));
        ObjectMapper om = new ObjectMapper();
        JavaType type = om.getTypeFactory()
            .constructParametricType(List.class, Product.class);

        DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(configs,
            new StringDeserializer(),
            new JsonDeserializer<>(type, om, false));
        ContainerProperties containerProperties = new ContainerProperties(topic);
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, List<Product>>) records::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @After
    public void tearDown() {
        container.stop();
    }

    @Test
    public void sendToMessageQueue_shouldSendListOfProduct_toKafka() {
        ListenableFuture<SendResult<String, List<Product>>> responseFuture = mock(ListenableFuture.class);
        when(template.send(Mockito.anyString(), any())).thenReturn(responseFuture);
        produceMessage();
        Mockito.verify(template).send(anyString(), any());
    }

    @Test
    public void sendToMessageQueue_shouldLogError_when_cannotSendMessage() {

        ListenableFuture<SendResult<String, List<Product>>> responseFuture = mock(ListenableFuture.class);
        Throwable throwable = mock(Throwable.class);
        given(throwable.getMessage()).willReturn("error-mess");
        when(template.send(Mockito.anyString(), Mockito.any())).thenReturn(responseFuture);
        doAnswer(invocationOnMock -> {
            ListenableFutureCallback listenableFutureCallback = invocationOnMock.getArgument(0);
            listenableFutureCallback.onFailure(throwable);
            return null;
        }).when(responseFuture).addCallback(any(ListenableFutureCallback.class));
        produceMessage();
        Mockito.verify(throwable).getMessage();
    }

    private void produceMessage() {
        Product product = ProductTestUtil.createProduct(Provider.SHOPEE);
        producer.sendToMessageQueue(List.of(product));
    }
}
