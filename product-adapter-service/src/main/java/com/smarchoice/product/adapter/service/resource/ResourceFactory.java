package com.smarchoice.product.adapter.service.resource;

import com.smarchoice.product.adapter.service.dto.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;

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
