package com.smartchoice.product.service.resource;

import com.netflix.discovery.EurekaClient;
import com.smartchoice.product.service.dto.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.ApplicationScope;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
@ApplicationScope
public class ProductAdapterServiceResource {

    @Value("${product.adapter.service.url}")
    private String serverUrl;

//    @Autowired
//    public ProductAdapterServiceResource(@Qualifier("eurekaClient") EurekaClient eurekaClient) {
//        this.eurekaClient = eurekaClient;
//    }

//    private EurekaClient eurekaClient;

    public List<Product> findProduct(MultiValueMap<String, String> criterion) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverUrl).queryParams(criterion);
        HttpEntity<Product> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<List<Product>> response =
                new RestTemplate().exchange(
                        builder.toUriString(),
                        HttpMethod.GET,
                        requestEntity,
                        new ParameterizedTypeReference<>() {
                        });
        return response.getBody();
    }

}
