package com.smarchoice.product.adapter.service.repository;

import com.smarchoice.product.adapter.service.dto.Product;

import java.util.List;

public interface ProductRepository {

    /**
     * Check if product group does exist
     * @param productName product name
     * @return if product group existed
     */
    boolean isProductGroupExist(String productName);

    /**
     * Get list of product stored in redis
     * @param productName product name (e.g : pencil)
     * @return
     */
    List<Product> getProductGroup(String productName);

    /**
     * Save a list of product for providers (tiki, lazada, shopee) based on product name
     *
     * @param productName product name (e.g : pencil)
     * @param products list of product that to be saved
     */
    void saveProductGroup(String productName, List<Product> products);


    /**
     * update a list of product for providers (tiki, lazada, shopee) based on product name
     *
     * @param productName product name (e.g : pencil)
     * @param products list of product that to be saved
     */
    void updateProductGroup(String productName, List<Product> products);

    /**
     * delete a list of product for providers (tiki, lazada, shopee) based on product name
     *
     * @param productName product name (e.g : pencil)
     */
    void deleteProductGroup(String productName);


}
