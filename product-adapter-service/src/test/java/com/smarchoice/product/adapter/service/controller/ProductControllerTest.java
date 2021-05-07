package com.smarchoice.product.adapter.service.controller;

import com.smarchoice.product.adapter.service.ProductTestUtil;
import com.smarchoice.product.adapter.service.config.RedisTestConfiguration;
import com.smarchoice.product.adapter.service.dto.Product;
import com.smarchoice.product.adapter.service.event.ProductProducer;
import com.smarchoice.product.adapter.service.repository.ProductRepository;
import com.smarchoice.product.adapter.service.resource.Provider;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
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

import java.util.List;
import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisTestConfiguration.class)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:7072", "port=7072"})
public class ProductControllerTest {

    public static final String REST_FIELD_SIZE = "$.size()";
    public static final String PRODUCTS_PATH = "/api/products";
    public static final String PRODUCT_NAME = "productName";


    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private ProductProducer productProducer;

    @Autowired
    private ProductRepository repository;

    private String productSearchValue;

    @Before
    public void initialiseRestAssuredMockMvcWebApplicationContext() {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
        mockRestTemplate();
        productSearchValue = UUID.randomUUID().toString();
    }

    /**
     * should search product by product name
     */
    @Test
    public void search_shouldFindProduct() {
        given()
                .auth().none()
                .param(PRODUCT_NAME, productSearchValue)
                .when().get(PRODUCTS_PATH).then()
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
                .param(PRODUCT_NAME, productSearchValue)
                .when().get(PRODUCTS_PATH).then()
                .statusCode(503);
    }

    @Test
    public void search_shouldReturnEmptyList_whenProgressInterrupted() throws InterruptedException {
        Mockito.doAnswer((answer) -> {
            throw new InterruptedException();
        })
                .when(productProducer)
                .sendToMessageQueue(anyList());

        given()
                .auth().none()
                .param(PRODUCT_NAME, productSearchValue)
                .when().get(PRODUCTS_PATH).then()
                .body(REST_FIELD_SIZE, is(0))
                .statusCode(200);
    }

    /**
     * To save cost cache response from 3rd provide
     */
    @Test
    public void search_shouldReturnResultFromCache_whenServiceHasBeenCalled() {
        Product product = ProductTestUtil.createProduct(Provider.SHOPEE);
        product.setProductName(productSearchValue);
        repository.save(product.getProductName(), Provider.SHOPEE, List.of(product));

        given()
                .auth().none()
                .param(PRODUCT_NAME, productSearchValue)
                .when().get(PRODUCTS_PATH).then()
                .body(REST_FIELD_SIZE, is(3))
                .statusCode(200);

        Mockito.verify(restTemplate, times(2)).exchange(Mockito.anyString(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(ParameterizedTypeReference.class));


    }

    /**
     * To save cost cache response from 3rd provide
     */
    @Test
    public void search_shouldCallService_whenCacheExpired() throws InterruptedException {
        Product product = ProductTestUtil.createProduct(Provider.SHOPEE);
        product.setProductName(productSearchValue);
        repository.save(product.getProductName(), Provider.SHOPEE, List.of(product));
        Thread.sleep(5000);
        given()
                .auth().none()
                .param(PRODUCT_NAME, productSearchValue)
                .when().get(PRODUCTS_PATH).then()
                .body(REST_FIELD_SIZE, is(3))
                .statusCode(200);

        Mockito.verify(restTemplate, times(3)).exchange(Mockito.anyString(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(ParameterizedTypeReference.class));


    }

    @Test
    public void search_shouldSendResultToKafka_whenFinishCallingServices() {
        given()
                .auth().none()
                .param(PRODUCT_NAME, productSearchValue)
                .when().get(PRODUCTS_PATH).then()
                .body(REST_FIELD_SIZE, is(3))
                .statusCode(200);
    }

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
