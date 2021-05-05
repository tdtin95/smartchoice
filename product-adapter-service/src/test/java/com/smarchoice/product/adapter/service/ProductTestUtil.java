/*
 * Copyright by Axon Ivy (Lucerne), all rights reserved.
 */
package com.smarchoice.product.adapter.service;

import java.util.UUID;
import com.smarchoice.product.adapter.service.dto.Product;
import com.smarchoice.product.adapter.service.resource.Provider;

public class ProductTestUtil {
    public static Product createProduct(Provider provider) {
        return Product.builder().productName(UUID.randomUUID().toString())
            .price((long) Math.random()).provider(provider)
            .discountRate((int) Math.random()).build();
    }
}
