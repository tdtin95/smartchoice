package com.smarchoice.product.adapter.service.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import com.smarchoice.product.adapter.service.ProductTestUtil;
import com.smarchoice.product.adapter.service.config.RedisTestConfiguration;
import com.smarchoice.product.adapter.service.dto.Product;
import com.smarchoice.product.adapter.service.resource.Provider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisTestConfiguration.class)
@DirtiesContext
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @Test
    public void save_shouldSaveProducts_byProvider() {
        Product product = ProductTestUtil.createProduct(Provider.SHOPEE);
        repository.save(product.getProductName(), Provider.SHOPEE, List.of(product));
        assertTrue(repository.isExist(product.getProductName(), Provider.SHOPEE));
    }


}
