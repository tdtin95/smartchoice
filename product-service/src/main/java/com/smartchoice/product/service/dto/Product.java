package com.smartchoice.product.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product implements Serializable {

    @Getter
    @Setter
    private String productName;

    @Getter
    @Setter
    private Provider provider;

    @Getter
    @Setter
    private long price;

    @Getter
    @Setter
    private String image;


    @Getter
    @Setter
    private int discountRate;
    @Getter
    @Setter
    private Promotion promotion;
}