package com.smarchoice.product.adapter.service.resource.provider.shopee;

import com.smarchoice.product.adapter.service.dto.Product;
import com.smarchoice.product.adapter.service.resource.AbstractProviderResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

@Component(ShopeeResource.QUALIFIER_NAME)
@ApplicationScope
public class ShopeeResource extends AbstractProviderResource<Product> {

    public static final String QUALIFIER_NAME = "SHOPEE";

    @Value("${external.provider.shopee.service.url}")
    private String serverUrl;

    @Override
    protected String getServerUrl() {
        return serverUrl;
    }
}
