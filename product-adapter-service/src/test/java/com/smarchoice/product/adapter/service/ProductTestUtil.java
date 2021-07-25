/*
 * Copyright by Axon Ivy (Lucerne), all rights reserved.
 */
package com.smarchoice.product.adapter.service;

import java.time.LocalDate;
import java.util.UUID;
import com.smarchoice.product.adapter.service.dto.Product;
import com.smarchoice.product.adapter.service.dto.Promotion;
import com.smarchoice.product.adapter.service.dto.Provider;

public class ProductTestUtil {
    public static Product createProduct(Provider provider) {
        return Product.newBuilder().setProductName(UUID.randomUUID().toString())
                .setPrice((long) Math.random())
                .setProvider(provider)
                .setDiscountRate((int) Math.random())
                .setPromotion(Promotion.newBuilder().setDetails("details")
                        .setValidFrom(LocalDate.now())
                        .setValidTill(LocalDate.now()).build())
                .build();
    }
}
