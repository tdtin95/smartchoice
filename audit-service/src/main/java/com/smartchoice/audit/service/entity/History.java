package com.smartchoice.audit.service.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class History {
    @Id
    private String id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private LocalDateTime actionOn;

    @Getter
    @Setter
    private String productName;
}
