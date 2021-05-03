package com.smartchoice.product.service.entity;

import com.smartchoice.product.service.dto.Product;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.List;

@Data
@RedisHash(value = "productGroup")
@NoArgsConstructor
@AllArgsConstructor
public class ProductGroup implements Serializable {

    @Getter
    @Setter
    @Indexed
    @Id
    private String productName;

    @Getter
    @Setter
    private List<Product> products;
}
