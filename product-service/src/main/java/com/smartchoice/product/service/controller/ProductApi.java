package com.smartchoice.product.service.controller;

import com.smartchoice.product.service.dto.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ProductApi.PRODUCT_PATH)
public interface ProductApi {

    String PRODUCT_PATH = "/product-information";

    @GetMapping
    ResponseEntity<List<Product>> getProductInformation(@RequestHeader("username") String username, @RequestParam MultiValueMap<String, String> allRequestParams);
}
