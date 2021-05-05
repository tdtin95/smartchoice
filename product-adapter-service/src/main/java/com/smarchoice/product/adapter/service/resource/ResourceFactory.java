package com.smarchoice.product.adapter.service.resource;

import java.util.HashMap;
import java.util.List;
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

    private final List<ProviderResource<? extends Product>> resources;

    @Autowired
    public ResourceFactory(List<ProviderResource<? extends Product>> resources) {
        this.resources = resources;
    }

    public List<ProviderResource<? extends Product>> getResources() {
        return resources;
    }
}
