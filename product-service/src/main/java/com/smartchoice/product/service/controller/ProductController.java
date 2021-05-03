package com.smartchoice.product.service.controller;

import com.smartchoice.product.service.dto.Product;
import com.smartchoice.product.service.service.ProductInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController implements ProductApi {

    private ProductInformationService productInformationService;

    @Autowired
    public ProductController(ProductInformationService productInformationService) {
        this.productInformationService = productInformationService;
    }


    /**
     * Get all products from provider
     *
     * @param allRequestParams list of request param from client
     * @return fetched product from external providers
     */
    @Override
    public ResponseEntity<List<Product>> getProductInformation(MultiValueMap<String, String> allRequestParams) {
        return new ResponseEntity<>(productInformationService.getProductInformation(allRequestParams), HttpStatus.OK);
    }
}
