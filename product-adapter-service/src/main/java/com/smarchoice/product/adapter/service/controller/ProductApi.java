package com.smarchoice.product.adapter.service.controller;

import com.smarchoice.product.adapter.service.dto.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ProductApi.PRODUCT_PATH)
public interface ProductApi {

    String PRODUCT_PATH = "/products";

    @GetMapping
    ResponseEntity<List<Product>> search(@RequestParam MultiValueMap<String, String> allRequestParams);
}
