package com.smarchoice.product.adapter.service.resource;

public enum Provider {
    SHOPEE("shopee"),
    LAZADA("lazada"),
    TIKI("tiki");

    private final String name;

    Provider(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
