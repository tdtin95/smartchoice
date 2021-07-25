package com.smarchoice.product.adapter.service.resource.provider.shopee;

import com.smarchoice.product.adapter.service.dto.Provider;
import com.smarchoice.product.adapter.service.resource.AbstractProviderResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.ApplicationScope;

@Component(ShopeeResource.QUALIFIER_NAME)
@ApplicationScope
public class ShopeeResource extends AbstractProviderResource {

    public static final String QUALIFIER_NAME = "SHOPEE";
    private final RestTemplate restTemplate;
    @Value("${external.provider.shopee.service.url}")
    private String serverUrl;

    @Autowired
    public ShopeeResource(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }

    @Override
    protected String getServerUrl() {
        return serverUrl;
    }

    @Override
    public Provider getProvider() {
        return Provider.SHOPEE;
    }
}
