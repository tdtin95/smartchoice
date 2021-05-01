package com.smarchoice.product.adapter.service.controller;

import com.smarchoice.product.adapter.service.resource.ProviderResource;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;

@SpringBootTest
public class ProductControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProviderResource resource;


    @BeforeEach
    public void initialiseRestAssuredMockMvcWebApplicationContext() {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);

    }

    /**
     * should search product by product name
     */
    @Test
    void search_shouldFindProduct() {
        RestAssuredMockMvc.given().auth().none().param("productName", "pen")
                .when().get("/products").then().statusCode(200);

    }
}
