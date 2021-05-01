package com.smarchoice.product.adapter.service.resource.provider.tiki;

import com.smarchoice.product.adapter.service.dto.Product;
import com.smarchoice.product.adapter.service.resource.AbstractProviderResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

@Component(TikiResource.QUALIFIER_NAME)
@ApplicationScope
public class TikiResource extends AbstractProviderResource<Product> {

    public static final String QUALIFIER_NAME = "TIKI";

    @Value("${external.provider.tiki.service.url}")
    private String serverUrl;

    @Override
    protected String getServerUrl() {
        return serverUrl;
    }
}
