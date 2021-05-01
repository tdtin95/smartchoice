package com.smarchoice.product.adapter.service.resource;

import com.smarchoice.product.adapter.service.dto.Product;
import com.smarchoice.product.adapter.service.resource.provider.lazada.LazadaResource;
import com.smarchoice.product.adapter.service.resource.provider.shopee.ShopeeResource;
import com.smarchoice.product.adapter.service.resource.provider.tiki.TikiResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@ApplicationScope
public class ResourceFactory {

    private List<ProviderResource> resources;

    private ProviderResource<Product> shopeeResource;
    private ProviderResource<Product> tikiResource;
    private ProviderResource<Product> lazadaResource;

    @PostConstruct
    public void initResource() {
        resources = new ArrayList<>();
        register(shopeeResource);
        register(tikiResource);
        register(lazadaResource);
    }

    /**
     * Register provider in order to get products
     *
     * @param resource provider resource to call external api
     */
    public void register(ProviderResource<?> resource) {
        resources.add(resource);
    }

    public List<ProviderResource> getResources() {
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
