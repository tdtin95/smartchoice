package com.smartchoice.product.service.resource;

import com.smartchoice.product.service.dto.Product;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory.AUTHORIZATION_HEADER;

@Component
public class ProductAdapterServiceResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductAdapterServiceResource.class);

    @Value("${product.adapter.service.url}")
    private String serverUrl;

    private RestTemplate restTemplate;

    @Autowired
    private final WebClient.Builder webClientBuilder;


    @Autowired
    public ProductAdapterServiceResource(RestTemplate restTemplate, WebClient.Builder webClientBuilder) {
        this.restTemplate = restTemplate;
        this.webClientBuilder = webClientBuilder;
    }

    //    @HystrixCommand(
//            fallbackMethod = "handleConnectTimeOut",
//            threadPoolKey = "productServiceCallProductAdapter",
//            threadPoolProperties = {
//                    @HystrixProperty(name = "coreSize", value = "30"),
//                    @HystrixProperty(name = "maxQueueSize", value = "10"),
//            },
//            commandProperties = {
//                    @HystrixProperty(
//                            name = "execution.isolation.thread.timeoutInMilliseconds",
//                            value = "10000"),
//                    @HystrixProperty(
//                            name = "circuitBreaker.requestVolumeThreshold",
//                            value = "10"),
//                    @HystrixProperty(
//                            name = "circuitBreaker.errorThresholdPercentage",
//                            value = "75"),
//                    @HystrixProperty(
//                            name = "circuitBreaker.sleepWindowInMilliseconds",
//                            value = "7000"),
//                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds",
//                            value = "15000"),
//                    @HystrixProperty(
//                            name = "metrics.rollingStats.numBuckets",
//                            value = "5")}
//    )
    public List<Product> findProduct(MultiValueMap<String, String> criterion) {
        WebClient webClient = webClientBuilder
                .baseUrl(serverUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(AUTHORIZATION_HEADER, "Bearer " + getToken())
                .build();


        Mono<List<Product>> productFlux = webClient.get().uri(uri -> uri.queryParams(criterion).build()).retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Product>>() {
                }).onErrorResume(error ->
                {
                    LOGGER.error(error.getMessage(), error);
                    return Mono.just(new ArrayList<>());
                });
        // .onErrorReturn(List.of());

        return productFlux.block();

    }

    public List<Product> handleConnectTimeOut(MultiValueMap<String, String> criterion) {
        LOGGER.error("Call product-adapter-service reached timeout");
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE, "product-adapter-service is temporary down");
    }

    private String getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null && !KeycloakAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return "";
        }
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) authentication;
        KeycloakSecurityContext context = token.getAccount().getKeycloakSecurityContext();
        return context.getTokenString();
    }
}
