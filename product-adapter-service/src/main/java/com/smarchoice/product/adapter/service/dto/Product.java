package com.smarchoice.product.adapter.service.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Getter
    @Setter
    private String productName;
    @Getter
    @Setter
    private long price;
    @Getter
    @Setter
    private int discountRate;
    @Getter
    @Setter
    private Promotion promotion;
}
