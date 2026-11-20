package com.example.reactiveproduct;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Flux<Product> findAll() {
        return repository.findAll();
    }

    public Mono<Product> findById(Long id) {
        return repository.findById(id);
    }

    public Mono<Product> save(Product product) {
        return repository.save(product);
    }

    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }
}