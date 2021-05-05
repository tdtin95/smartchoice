package com.smarchoice.product.adapter.service.dto;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
