package com.smartchoice.product.service.listener;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartchoice.product.service.dto.History;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@DirtiesContext
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:7071", "port=7071"})
public class HistoryProducerTest {

    @Autowired
    private HistoryProducer producer;

    @Value("${message.queue.history.topic}")
    private String topic;


    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    private BlockingQueue<ConsumerRecord<String, History>> records;
    private KafkaMessageListenerContainer<String, String> container;

    @MockBean
    private KafkaTemplate<String, History> template;

    @Before
    public void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps(topic, "false", embeddedKafkaBroker));
        ObjectMapper om = new ObjectMapper();
        JavaType type = om.getTypeFactory()
                .constructType(History.class);

        DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(configs,
                new StringDeserializer(),
                new JsonDeserializer<>(type, om, false));
        ContainerProperties containerProperties = new ContainerProperties(topic);
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, History>) records::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }


    @After
    public void tearDown() {
        container.stop();
    }

    @Test
    public void storeHistory_shouldSendHistory_toKafka() {
        ListenableFuture<SendResult<String, History>> responseFuture = mock(ListenableFuture.class);
        when(template.send(Mockito.anyString(), any())).thenReturn(responseFuture);
        produceMessage();
        Mockito.verify(template).send(anyString(), any());
    }

    @Test
    public void storeHistory_shouldLogError_when_cannotSendMessage() {

        ListenableFuture<SendResult<String, History>> responseFuture = mock(ListenableFuture.class);
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
        producer.storeHistory(History.builder().actionOn(new Date())
                .productName(UUID.randomUUID().toString())
                .username("username")
                .id(UUID.randomUUID().toString())
                .build());
    }
}
