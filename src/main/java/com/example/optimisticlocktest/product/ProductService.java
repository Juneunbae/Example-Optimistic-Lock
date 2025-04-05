package com.example.optimisticlocktest.product;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public void updateProductPrice(Long id, double price) {
        try {
            Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

            product.setPrice(price);

            productRepository.save(product);
        } catch (ObjectOptimisticLockingFailureException e) {
            System.err.println("낙관적 락 충돌 발생!!");
        }
    }
}