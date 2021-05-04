package com.smartchoice.product.service.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class History {


    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private Date actionOn;

    @Getter
    @Setter
    private String productName;
}
