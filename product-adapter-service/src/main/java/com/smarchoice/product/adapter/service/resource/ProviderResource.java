package com.smarchoice.product.adapter.service.resource;

import java.util.List;
import org.springframework.util.MultiValueMap;

/**
 * Resource to access to external provider api
 * @param <T> DTO object that return from parsing response from provider api
 */
public interface ProviderResource<T> {
    List<T> findProduct(MultiValueMap<String, String> criterion);
}
