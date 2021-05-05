/*
 * Copyright by Axon Ivy (Lucerne), all rights reserved.
 */
package com.smarchoice.product.adapter.service;

import com.smarchoice.product.adapter.service.dto.Product;
import com.smarchoice.product.adapter.service.dto.Promotion;
import com.smarchoice.product.adapter.service.resource.Provider;

import java.util.Date;
import java.util.UUID;

public class ProductTestUtil {
    public static Product createProduct(Provider provider) {
        return Product.builder().productName(UUID.randomUUID().toString())
                .price((long) Math.random()).provider(provider)
                .discountRate((int) Math.random())
                .promotion(Promotion.builder().details("details")
                        .validFrom(new Date())
                        .validTill(new Date()).build())
                .build();
    }
}
