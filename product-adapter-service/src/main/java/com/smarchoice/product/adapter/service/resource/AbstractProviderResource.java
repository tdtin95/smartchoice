package com.smarchoice.product.adapter.service.resource;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public abstract class AbstractProviderResource<T> implements ProviderResource<T> {

    protected abstract String getServerUrl();

    @Override
    public List<T> findProduct(MultiValueMap<String, String> criterion) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getServerUrl()).queryParams(criterion);
        HttpEntity<T> requestEntity = new HttpEntity<>(headers);
        HttpEntity<List<T>> response =
                new RestTemplate().exchange(
                        builder.toUriString(),
                        HttpMethod.GET,
                        requestEntity,
                        new ParameterizedTypeReference<>() {
                        });
        return response.getBody();
    }
}
