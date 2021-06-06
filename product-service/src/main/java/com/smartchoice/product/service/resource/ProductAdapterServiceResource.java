package com.smartchoice.product.service.resource;

import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.smartchoice.product.service.dto.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.ApplicationScope;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
@ApplicationScope
public class ProductAdapterServiceResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductAdapterServiceResource.class);

    @Value("${product.adapter.service.url}")
    private String serverUrl;

    private RestTemplate restTemplate;

    @Autowired
    public ProductAdapterServiceResource(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @HystrixCommand(
            fallbackMethod = "handleConnectTimeOut",
            threadPoolKey = "productServiceCallProductAdapter",
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "30"),
                    @HystrixProperty(name = "maxQueueSize", value = "10"),
            },
            commandProperties = {
                    @HystrixProperty(
                    name = "execution.isolation.thread.timeoutInMilliseconds",
                    value = "10000"),
                    @HystrixProperty(
                            name = "circuitBreaker.requestVolumeThreshold",
                            value = "10"),
                    @HystrixProperty(
                            name = "circuitBreaker.errorThresholdPercentage",
                            value = "75"),
                    @HystrixProperty(
                            name = "circuitBreaker.sleepWindowInMilliseconds",
                            value = "7000"),
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds",
                            value = "15000"),
                    @HystrixProperty(
                            name = "metrics.rollingStats.numBuckets",
                            value = "5")}
    )
    public List<Product> findProduct(MultiValueMap<String, String> criterion) {
        HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(10000);
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

    public List<Product> handleConnectTimeOut(MultiValueMap<String, String> criterion) {
        LOGGER.error("Call product-adapter-service reached timeout");
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE, "product-adapter-service is temporary down");
    }

}
