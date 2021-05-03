package com.smarchoice.product.adapter.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ProductAdapterServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductAdapterServiceApplication.class, args);
    }

}
