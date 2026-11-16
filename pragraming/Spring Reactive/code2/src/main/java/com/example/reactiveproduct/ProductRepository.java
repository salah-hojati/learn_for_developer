package com.example.reactiveproduct;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
}