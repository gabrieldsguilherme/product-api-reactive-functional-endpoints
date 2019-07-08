package com.wiredbraincoffee.productapifunctional.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.wiredbraincoffee.productapifunctional.model.Product;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {

}
