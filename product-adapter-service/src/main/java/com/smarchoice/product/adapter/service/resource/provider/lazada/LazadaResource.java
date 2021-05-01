package com.smarchoice.product.adapter.service.resource.provider.lazada;

import com.smarchoice.product.adapter.service.dto.Product;
import com.smarchoice.product.adapter.service.resource.AbstractProviderResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

@Component(LazadaResource.QUALIFIER_NAME)
@ApplicationScope
public class LazadaResource extends AbstractProviderResource<Product> {

    public static final String QUALIFIER_NAME = "LAZADA";

    @Value("${external.provider.lazada.service.url}")
    private String serverUrl;

    @Override
    protected String getServerUrl() {
        return serverUrl;
    }
}
