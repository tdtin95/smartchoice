package com.smarchoice.product.adapter.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import com.smarchoice.product.adapter.service.resource.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String image;

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
