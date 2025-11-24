package com.saletech;

import java.util.*;

public class InMemoryProductRepository implements ProductRepository {

    private final List<Product> products = new ArrayList<>();

    @Override
    public List<Product> findAll() { return new ArrayList<>(products); }

    @Override
    public Optional<Product> findById(int id) {
        return products.stream().filter(p -> p.getId() == id).findFirst();
    }

    @Override
    public List<Product> searchByNameOrManufacturer(String term) {
        String t = term.toLowerCase();
        List<Product> result = new ArrayList<>();

        for (Product p : products) {
            if (p.getName().toLowerCase().contains(t)
                    || p.getManufacturer().toLowerCase().contains(t)) {
                result.add(p);
            }
        }
        return result;
    }

    @Override
    public void save(Product product) {
        products.add(product);
    }

    @Override
    public void updateQuantity(int id, int newQty) {
        findById(id).ifPresent(p -> p.setQuantityInStock(newQty));
    }
}
