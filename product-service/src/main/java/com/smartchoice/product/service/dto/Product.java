package com.smartchoice.product.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private byte[] images;

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