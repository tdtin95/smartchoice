package com.smarchoice.product.adapter.service.resource.provider.tiki;

import com.smarchoice.product.adapter.service.resource.AbstractProviderResource;
import com.smarchoice.product.adapter.service.resource.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.ApplicationScope;

@Component(TikiResource.QUALIFIER_NAME)
@ApplicationScope
public class TikiResource extends AbstractProviderResource {

    public static final String QUALIFIER_NAME = "TIKI";
    private final RestTemplate restTemplate;
    @Value("${external.provider.tiki.service.url}")
    private String serverUrl;

    @Autowired
    public TikiResource(RestTemplate restTemplate) {
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
        return Provider.TIKI;
    }


}
