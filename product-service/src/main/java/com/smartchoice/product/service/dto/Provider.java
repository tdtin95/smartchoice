package com.smartchoice.product.service.dto;

public enum Provider {
    SHOPEE("shopee"),
    LAZADA("lazada"),
    TIKI("tiki");

    private String name;

    Provider(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
