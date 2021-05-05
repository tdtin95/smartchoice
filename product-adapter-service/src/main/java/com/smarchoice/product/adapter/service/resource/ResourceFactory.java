package com.smarchoice.product.adapter.service.resource;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import com.smarchoice.product.adapter.service.dto.Product;
import com.smarchoice.product.adapter.service.resource.provider.lazada.LazadaResource;
import com.smarchoice.product.adapter.service.resource.provider.shopee.ShopeeResource;
import com.smarchoice.product.adapter.service.resource.provider.tiki.TikiResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

@Component
@ApplicationScope
public class ResourceFactory {

    private Map<Provider, ProviderResource> resources;

    private ProviderResource<Product> shopeeResource;
    private ProviderResource<Product> tikiResource;
    private ProviderResource<Product> lazadaResource;

    @PostConstruct
    public void initResource() {
        resources = new HashMap<>();
        register(Provider.SHOPEE, shopeeResource);
        register(Provider.TIKI, tikiResource);
        register(Provider.LAZADA, lazadaResource);
    }

    /**
     * Register provider in order to get products
     * @param provider {@link Provider}
     * @param resource provider resource to call external api
     */
    public void register(Provider provider, ProviderResource<?> resource) {
        resources.put(provider, resource);
    }

    public Map<Provider, ProviderResource> getResources() {
        return resources;
    }

    @Autowired
    public void setShopeeResource(@Qualifier(ShopeeResource.QUALIFIER_NAME) ProviderResource<Product> shopeeResource) {
        this.shopeeResource = shopeeResource;
    }

    @Autowired
    public void setTikiResource(@Qualifier(TikiResource.QUALIFIER_NAME) ProviderResource<Product> tikiResource) {
        this.tikiResource = tikiResource;
    }

    @Autowired
    public void setLazadaResource(@Qualifier(LazadaResource.QUALIFIER_NAME) ProviderResource<Product> lazadaResource) {
        this.lazadaResource = lazadaResource;
    }
}
