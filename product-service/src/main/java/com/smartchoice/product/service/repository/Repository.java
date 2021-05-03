package com.smartchoice.product.service.repository;

import com.smartchoice.product.service.entity.ProductGroup;
import org.springframework.data.repository.CrudRepository;

public interface Repository extends CrudRepository<ProductGroup, String> {
}

