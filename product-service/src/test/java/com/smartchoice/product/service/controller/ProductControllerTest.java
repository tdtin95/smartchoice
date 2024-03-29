package com.smartchoice.product.service.controller;

import static org.hamcrest.CoreMatchers.is;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.Date;
import java.util.List;
import com.smartchoice.product.service.config.RedisTestConfiguration;
import com.smartchoice.product.service.dto.Product;
import com.smartchoice.product.service.dto.Promotion;
import com.smartchoice.product.service.dto.Provider;
import com.smartchoice.product.service.entity.ProductGroup;
import com.smartchoice.product.service.repository.Repository;
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
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:7071", "port=7071"})
public class ProductControllerTest {
    public static final String REST_FIELD_SIZE = "$.size()";
    public static final String PRODUCT_NAME = "productName";
    public static final String PRODUCT_INFORMATION_PATH = "/api/product-information";
    private static final String USERNAME = "username";
    private static final String TEST_USERNAME = "tdtin";
    @MockBean
    RestTemplate restTemplate;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private Repository<ProductGroup> repository;

    @Before
    public void initialiseRestAssuredMockMvcWebApplicationContext() {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
        mockRestTemplate();
    }

    /**
     * should search product by product name
     */
    @Test
    public void getProductInformation_shouldFindProduct() {
        RestAssuredMockMvc.given()
            .auth().none()
            .header(USERNAME, TEST_USERNAME)
            .param(PRODUCT_NAME, "pen")
            .get(PRODUCT_INFORMATION_PATH)
            .then()
            .body(REST_FIELD_SIZE, is(3))
            .statusCode(200);
    }

    @Test
    public void getProductInformation_shouldReturnBadRequest_when_headerIsMissed() {
        RestAssuredMockMvc.given()
            .auth().none()
            .get(PRODUCT_INFORMATION_PATH)
            .then()
            .statusCode(400);
    }

    @Test
    public void getProductInformation_shouldReturnEmptyList_when_paramIsEmpty() {
        RestAssuredMockMvc.given()
            .auth().none()
            .header(USERNAME, TEST_USERNAME)
            .get(PRODUCT_INFORMATION_PATH)
            .then()
            .body(REST_FIELD_SIZE, is(0));
    }

    @Test
    public void getProductInformation_should_returnResultFromCache() {
        Product product = createProduct("brush", Provider.LAZADA);
        ProductGroup productGroup = new ProductGroup(product.getProductName(),
            List.of(product));
        repository.save(productGroup);
        RestAssuredMockMvc.given()
            .auth().none()
            .header(USERNAME, TEST_USERNAME)
            .param(PRODUCT_NAME, "brush")
            .get(PRODUCT_INFORMATION_PATH)
            .then()
            .body(REST_FIELD_SIZE, is(1))
            .statusCode(200);
    }

    @Test
    public void getProductInformation_shouldCallProductAdapterService_when_cacheIsExpired() throws InterruptedException {
        Product product = createProduct("pencil", Provider.SHOPEE);
        ProductGroup productGroup = new ProductGroup(product.getProductName(),
            List.of(product));
        repository.save(productGroup);
        Thread.sleep(5000);
        RestAssuredMockMvc.given()
            .auth().none()
            .header(USERNAME, TEST_USERNAME)
            .param(PRODUCT_NAME, "pencil")
            .get(PRODUCT_INFORMATION_PATH)
            .then()
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
            List.of(createProduct("shopee-product", Provider.SHOPEE)
                , createProduct("lazada-product", Provider.LAZADA)
                , createProduct("tiki-product", Provider.TIKI)
            ));
    }

    private Product createProduct(String name, Provider provider) {
        return Product.builder()
            .productName(name)
            .price(57)
            .provider(provider)
            .discountRate(15)
            .image("image-data")
            .promotion(Promotion.builder()
                .details("detail")
                .validFrom(new Date())
                .validTill(new Date()).build()).build();
    }
}
