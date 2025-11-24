package com.saletech;

import java.util.*;

/**
 * InMemoryProductRepository
 *
 * This class is a temporary, in-memory implementation of the
 * ProductRepository interface. It stores product data inside a
 * simple ArrayList during program execution.
 *
 * In Module 5, this class will be replaced (or supplemented) by a
 * database-backed repository that performs real SQL operations.
 * For now, it allows the program to run without requiring a database.
 */
public class InMemoryProductRepository implements ProductRepository {

    // Internal list that holds all Product objects
    private final List<Product> products = new ArrayList<>();


    /**
     * Returns a copy of all products in the repository.
     * A new list is returned to prevent outside code from directly
     * modifying the internal product list (encapsulation).
     *
     * @return List of all products
     */
    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products);
    }


    /**
     * Finds a product by its ID using Java Streams.
     *
     * @param id Product ID to search for
     * @return Optional containing the product if found, otherwise empty
     */
    @Override
    public Optional<Product> findById(int id) {
        return products.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }


    /**
     * Searches for products whose name or manufacturer contains
     * the provided term (case-insensitive).
     *
     * @param term Search term typed by the user
     * @return List of all matching products
     */
    @Override
    public List<Product> searchByNameOrManufacturer(String term) {
        String t = term.toLowerCase();
        List<Product> result = new ArrayList<>();

        // Manual loop for clarity and compatibility with older Java styles
        for (Product p : products) {
            if (p.getName().toLowerCase().contains(t)
                    || p.getManufacturer().toLowerCase().contains(t)) {
                result.add(p);
            }
        }

        return result;
    }


    /**
     * Adds a new product to the repository.
     * Currently no duplicate checking is performed since IDs are
     * uniquely assigned inside Product objects.
     *
     * @param product Product to store
     */
    @Override
    public void save(Product product) {
        products.add(product);
    }


    /**
     * Updates the stock level of an existing product.
     * If the product exists, its quantity is updated; otherwise nothing happens.
     *
     * @param id      Product ID to modify
     * @param newQty  New inventory quantity
     */
    @Override
    public void updateQuantity(int id, int newQty) {
        findById(id).ifPresent(p -> p.setQuantityInStock(newQty));
    }
}
