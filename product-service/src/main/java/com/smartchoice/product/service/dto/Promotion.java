package com.smartchoice.product.service.dto;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Promotion {
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
