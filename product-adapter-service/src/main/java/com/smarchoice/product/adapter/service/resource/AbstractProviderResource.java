package com.smarchoice.product.adapter.service.resource;

import com.smarchoice.product.adapter.service.dto.Product;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public abstract class AbstractProviderResource implements ProviderResource<Product> {

    protected abstract String getServerUrl();
    protected abstract Provider getProvider();

    @Override
    public List<Product> findProduct(MultiValueMap<String, String> criterion) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getServerUrl()).queryParams(criterion);
        HttpEntity<Product> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<List<Product>> response =
                new RestTemplate().exchange(
                        builder.toUriString(),
                        HttpMethod.GET,
                        requestEntity,
                        new ParameterizedTypeReference<>() {
                        });
        List<Product> products = response.getBody();
        products.forEach(item -> item.setProvider(getProvider()));
        return products;
    }

}
