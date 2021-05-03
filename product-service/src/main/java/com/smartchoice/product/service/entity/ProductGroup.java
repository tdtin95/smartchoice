package com.smartchoice.product.service.entity;

import com.smartchoice.product.service.dto.Product;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

@Data
@RedisHash("productGroup")
@NoArgsConstructor
@AllArgsConstructor
public class ProductGroup {

    @Getter
    @Setter
    @Indexed
    private String productName;

    @Getter
    @Setter
    private List<Product> products;
}
