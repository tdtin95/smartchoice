package com.smarchoice.product.adapter.service.resource.provider.lazada;

import com.smarchoice.product.adapter.service.resource.AbstractProviderResource;
import com.smarchoice.product.adapter.service.resource.Provider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

@Component(LazadaResource.QUALIFIER_NAME)
@ApplicationScope
public class LazadaResource extends AbstractProviderResource {

    public static final String QUALIFIER_NAME = "LAZADA";

    @Value("${external.provider.lazada.service.url}")
    private String serverUrl;

    @Override
    protected String getServerUrl() {
        return serverUrl;
    }

    @Override
    protected Provider getProvider() {
        return Provider.LAZADA;
    }
}
