package com.smartchoice.product.service.service;

import com.smartchoice.product.service.dto.Product;
import com.smartchoice.product.service.repository.ProductRepository;
import com.smartchoice.product.service.resource.ProductAdapterServiceResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Service
public class ProductInformationService {

    private ProductRepository productRepository;
    private ProductAdapterServiceResource productAdapterServiceResource;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductInformationService.class);

    @Autowired
    public ProductInformationService(ProductRepository productRepository, ProductAdapterServiceResource productAdapterServiceResource) {
        this.productRepository = productRepository;
        this.productAdapterServiceResource = productAdapterServiceResource;
    }

    public List<Product> getProductInformation(MultiValueMap<String, String> queryParams) {
        String productName = queryParams.getFirst("productName");

        LOGGER.info("---- {}", productName);
        //TODO throw exception if missing productName
        if (productRepository.isProductGroupExist(productName)) {
            LOGGER.info("GET FROM CACHE {} {}",productName , productRepository.getProductGroup(productName).size());
            return productRepository.getProductGroup(productName);
        }

        List<Product> products = productAdapterServiceResource.findProduct(queryParams);
        productRepository.saveProductGroup(productName, products);
        return products;

    }
}
