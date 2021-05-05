package com.smarchoice.product.adapter.service.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyList;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.List;
import com.smarchoice.product.adapter.service.ProductTestUtil;
import com.smarchoice.product.adapter.service.config.RedisTestConfiguration;
import com.smarchoice.product.adapter.service.dto.Product;
import com.smarchoice.product.adapter.service.event.ProductProducer;
import com.smarchoice.product.adapter.service.resource.Provider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisTestConfiguration.class)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:7072", "port=7072"})
public class ProductControllerTest {

    public static final String REST_FIELD_SIZE = "$.size()";

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;

//    @MockBean
//    private ProductProducer productProducer;

    @Before
    public void initialiseRestAssuredMockMvcWebApplicationContext() {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
        mockRestTemplate();

    }

    /**
     * should search product by product name
     */
    @Test
    public void search_shouldFindProduct() {
        given()
            .auth().none()
            .param("productName", "product-name")
            .when().get("/products").then()
            .body(REST_FIELD_SIZE, is(3))
            .statusCode(200);
    }

    @Test
    public void search_shouldReturn503_whenAtLeaseOneProvider_isUnreachable() {

        Mockito.when(restTemplate.exchange(Mockito.anyString(),
            Mockito.any(),
            Mockito.any(),
            Mockito.any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("unreachable"));

        given()
            .auth().none()
            .param("productName", "product-name")
            .when().get("/products").then()
            .statusCode(503);
    }

//    @Test
//    public void search_shouldReturnEmptyList_whenProgressInterrupted() throws InterruptedException {
//        Mockito.doThrow(new InterruptedException())
//            .when(productProducer)
//            .sendToMessageQueue(anyList());
//
//        given()
//            .auth().none()
//            .param("productName", "product-name")
//            .when().get("/products").then()
//            .body(REST_FIELD_SIZE, is(0))
//            .statusCode(200);
//    }

    private void mockRestTemplate() {
        ResponseEntity<List<Product>> response = Mockito.mock(ResponseEntity.class);
        Mockito.when(restTemplate.exchange(Mockito.anyString(),
            Mockito.any(),
            Mockito.any(),
            Mockito.any(ParameterizedTypeReference.class)
        )).thenReturn(response);
        Mockito.when(response.getBody()).thenReturn(
            List.of(ProductTestUtil.createProduct(Provider.SHOPEE)));
    }
}
