package com.smartchoice.product.service.service;

import com.smartchoice.product.service.dto.Product;
import com.smartchoice.product.service.entity.ProductGroup;
import com.smartchoice.product.service.repository.Repository;
import com.smartchoice.product.service.resource.ProductAdapterServiceResource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Service
public class ProductInformationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductInformationService.class);
    public static final String PRODUCT_NAME = "productName";
    private final ProductAdapterServiceResource productAdapterServiceResource;
    private final Repository<ProductGroup> repository;

    @Autowired
    public ProductInformationService(ProductAdapterServiceResource productAdapterServiceResource, Repository<ProductGroup> repository) {
        this.productAdapterServiceResource = productAdapterServiceResource;
        this.repository = repository;
    }

    public List<Product> getProductInformation(MultiValueMap<String, String> queryParams) {
        String productName = queryParams.getFirst(PRODUCT_NAME);

        if (StringUtils.isBlank(productName)) {
            return List.of();
        }

        if (repository.existsById(productName)) {
            ProductGroup productGroup = repository.findById(productName);
            LOGGER.info("GET FROM CACHE {} {}", productName, productGroup.getProducts().size());
            return productGroup.getProducts();
        }

        List<Product> products = productAdapterServiceResource.findProduct(queryParams);
        repository.save(new ProductGroup(productName, products));
        return products;

    }

}
