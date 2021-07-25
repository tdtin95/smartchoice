package com.smarchoice.product.adapter.service.repository;

import com.smarchoice.product.adapter.service.ProductTestUtil;
import com.smarchoice.product.adapter.service.config.RedisTestConfiguration;
import com.smarchoice.product.adapter.service.dto.Product;
import com.smarchoice.product.adapter.service.dto.Provider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisTestConfiguration.class)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:7072", "port=7072"})
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @Test
    public void save_shouldSaveProducts_byProvider() {
        Product product = ProductTestUtil.createProduct(Provider.SHOPEE);
        repository.save(product.getProductName(), Provider.SHOPEE, List.of(product));
        assertTrue(repository.isExist(product.getProductName(), Provider.SHOPEE));
    }

    @Test
    public void save_shouldThrowException_when_productNameAndProviderAreNull() {
        Product product = ProductTestUtil.createProduct(Provider.SHOPEE);
        assertThrows(NullPointerException.class, () ->
                repository.save(null, null, List.of(product)));
    }

    @Test
    public void save_shouldOverrideExistRecord() {
        Product product = ProductTestUtil.createProduct(Provider.SHOPEE);
        repository.save(product.getProductName(), Provider.SHOPEE, List.of(product));
        Product newProduct = ProductTestUtil.createProduct(Provider.SHOPEE);
        repository.save(product.getProductName(), Provider.SHOPEE, List.of(newProduct));

        final List<Product> products = repository.search(product.getProductName(), Provider.SHOPEE);
        assertEquals(1, products.size());
        assertEquals(newProduct.getProductName(), products.get(0).getProductName());
    }

    @Test
    public void isExist_shouldClearTheCache_when_recordExpires() throws InterruptedException {
        Product product = ProductTestUtil.createProduct(Provider.SHOPEE);
        repository.save(product.getProductName(), Provider.SHOPEE, List.of(product));
        Thread.sleep(6000);
        assertFalse(repository.isExist(product.getProductName(), Provider.SHOPEE));
    }

    @Test
    public void search_shouldSeparateProduct_whenHasTheSameName_but_differentProvider() {
        Product shopeeProduct = ProductTestUtil.createProduct(Provider.SHOPEE);
        repository.save(shopeeProduct.getProductName(), Provider.SHOPEE, List.of(shopeeProduct));

        Product lazadaProduct = ProductTestUtil.createProduct(Provider.LAZADA);
        lazadaProduct.setProductName(shopeeProduct.getProductName());
        repository.save(shopeeProduct.getProductName(), Provider.LAZADA, List.of(shopeeProduct));

        assertEquals(shopeeProduct.getProductName(), repository.search(shopeeProduct.getProductName(), Provider.SHOPEE).get(0).getProductName());
        assertEquals(lazadaProduct.getProductName(), repository.search(lazadaProduct.getProductName(), Provider.LAZADA).get(0).getProductName());
    }

    @Test
    public void search_shouldReturnEmptyList_whenProductIsNOtExist() {
        assertTrue(repository.search("none-exist", Provider.SHOPEE).isEmpty());
    }

    @Test
    public void search_shouldSearchExactByProductNameAndProvider() {
        Product shopeeProduct = ProductTestUtil.createProduct(Provider.SHOPEE);
        repository.save(shopeeProduct.getProductName(), Provider.SHOPEE, List.of(shopeeProduct));
        assertTrue(repository.search(shopeeProduct.getProductName(), Provider.TIKI).isEmpty());
    }

    @Test
    public void delete_shouldDeleteRecord() {
        Product shopeeProduct = ProductTestUtil.createProduct(Provider.SHOPEE);
        final String productName = shopeeProduct.getProductName();
        repository.save(productName, Provider.SHOPEE, List.of(shopeeProduct));
        repository.delete(productName, Provider.SHOPEE);
        assertFalse(repository.isExist(productName, Provider.SHOPEE));
    }


}
