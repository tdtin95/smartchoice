package com.smartchoice.product.service.resource;

import com.smartchoice.product.service.dto.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.ApplicationScope;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
@ApplicationScope
public class ProductAdapterServiceResource {

    @Value("${product.adapter.service.url}")
    private String serverUrl;

    private RestTemplate restTemplate;

    @Autowired
    public ProductAdapterServiceResource(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Product> findProduct(MultiValueMap<String, String> criterion) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverUrl).queryParams(criterion);
        HttpEntity<Product> requestEntity = new HttpEntity<>(headers);

        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        restTemplate.setUriTemplateHandler(defaultUriBuilderFactory);

        ResponseEntity<List<Product>> response =
                restTemplate.exchange(
                        builder.toUriString(),
                        HttpMethod.GET,
                        requestEntity,
                        new ParameterizedTypeReference<>() {
                        });
        return response.getBody();
    }

}
