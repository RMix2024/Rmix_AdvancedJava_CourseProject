package com.saletech;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    List<Product> findAll();

    Optional<Product> findById(int id);

    List<Product> searchByNameOrManufacturer(String term);

    void save(Product product);

    void updateQuantity(int id, int newQuantity);
}
