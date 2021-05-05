package com.smarchoice.product.adapter.service.repository;

import java.util.List;
import com.smarchoice.product.adapter.service.dto.Product;
import com.smarchoice.product.adapter.service.resource.Provider;

public interface ProductRepository {

    /**
     * Check if product group does exist
     * @param productName product name
     * @param provider 3rd party provider {@link Provider}
     * @return if product group existed
     */
    boolean isExist(String productName, Provider provider);

    /**
     * Get list of product stored in redis
     * @param productName product name (e.g : pencil)
     * @param provider 3rd party provider {@link Provider}
     * @return list of product belongs to a provider
     */
    List<Product> search(String productName, Provider provider);

    /**
     * update a list of product for providers (tiki, lazada, shopee) based on product name
     *
     * @param productName product name (e.g : pencil)
     * @param provider 3rd party provider {@link Provider}
     * @param products list of product that to be saved
     */
    void save(String productName, Provider provider, List<Product> products);

    /**
     * delete a list of product for providers (tiki, lazada, shopee) based on product name
     * @param provider 3rd party provider {@link Provider}
     * @param productName product name (e.g : pencil)
     */
    void delete(String productName, Provider provider);


}
