package com.example.optimisticlocktest.product;

import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ProductServiceTest {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testOptimisticLocking() throws InterruptedException {
        Product product = new Product();

        product.setName("Product 1");
        product.setPrice(100.0);

        productRepository.save(product);

        Thread thread1 = new Thread(() -> {
            productService.updateProductPrice(product.getId(), 200.0);
        });

        Thread thread2 = new Thread(() -> {
            assertThrows(OptimisticLockException.class, () -> {
                productService.updateProductPrice(product.getId(), 300.0);
            });
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }
}