package com.smartchoice.product.service.repository;

public interface Repository<T> {

    void save(T productGroup);

    boolean existsById(String productName);

    T findById(String productName);
}
