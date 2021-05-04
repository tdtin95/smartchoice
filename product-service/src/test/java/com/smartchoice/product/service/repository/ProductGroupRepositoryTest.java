package com.smartchoice.product.service.repository;

import com.smartchoice.product.service.config.RedisTestConfiguration;
import com.smartchoice.product.service.dto.Product;
import com.smartchoice.product.service.dto.Provider;
import com.smartchoice.product.service.entity.ProductGroup;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisTestConfiguration.class)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:7071", "port=7071"})
public class ProductGroupRepositoryTest {

    @Autowired
    private Repository<ProductGroup> repository;

    @Test
    public void save_shouldSaveProductGroupToRedis() {
        Product product = createProduct();
        ProductGroup productGroup = new ProductGroup(product.getProductName(), List.of(product));
        repository.save(productGroup);
        Assertions.assertTrue(repository.existsById(product.getProductName()));
    }

    @Test
    public void save_shouldClearCache_when_itExpires() throws InterruptedException {
        Product product = createProduct();
        ProductGroup productGroup = new ProductGroup(product.getProductName(), List.of(product));
        repository.save(productGroup);
        Thread.sleep(5000);
        Assertions.assertFalse(repository.existsById(product.getProductName()));
    }

    @Test
    public void save_shouldThrowException_when_productName_isEmpty() {
        Product product = new Product();
        ProductGroup productGroup = new ProductGroup(product.getProductName(), List.of(product));
        Assertions.assertThrows(IllegalArgumentException.class, () -> repository.save(productGroup));
    }

    @Test
    public void existById_shouldReturnFalse_whenProductGroupDoesNotExist() {
        Assertions.assertFalse(repository.existsById("non-exist"));
    }

    @Test
    public void findById_shouldReturnEntity_whenItExistsInCache() {
        Product product = createProduct();
        ProductGroup productGroup = new ProductGroup(product.getProductName(), List.of(product));
        repository.save(productGroup);
        Assertions.assertNotNull(repository.findById(product.getProductName()));
    }

    @Test
    public void findById_shouldReturnNull_whenProductDoesNotExistInCache() {
        Assertions.assertNull(repository.findById("something"));
    }

    private Product createProduct() {
        return Product.builder().productName(UUID.randomUUID().toString())
                .price(57).provider(Provider.SHOPEE)
                .discountRate(15).build();
    }

}
