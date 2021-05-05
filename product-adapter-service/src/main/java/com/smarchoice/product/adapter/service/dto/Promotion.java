package com.smarchoice.product.adapter.service.dto;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Promotion implements Serializable {
    @Getter
    @Setter
    private String details;
    @Getter
    @Setter
    private Date validFrom;
    @Getter
    @Setter
    private Date validTill;
}
