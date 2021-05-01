package com.smarchoice.product.adapter.service.controller;

import com.smarchoice.product.adapter.service.dto.Product;
import com.smarchoice.product.adapter.service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController implements ProductApi {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Get all products from provider
     *
     * @param allRequestParams list of request param from client
     * @return fetched product from external providers
     */
    @Override
    public List<Product> search(@RequestParam MultiValueMap<String, String> allRequestParams) {
        return productService.getProducts(allRequestParams);
    }

}
