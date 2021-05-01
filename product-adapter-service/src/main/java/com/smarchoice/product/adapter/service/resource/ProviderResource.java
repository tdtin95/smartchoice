package com.smarchoice.product.adapter.service.resource;

import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

/**
 * Resource to access to external provider api
 * @param <T> DTO object that return from parsing response from provider api
 */
public interface ProviderResource<T> {
    List<T> findProduct(MultiValueMap<String, String> criterion);
}
