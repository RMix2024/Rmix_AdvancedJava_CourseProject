package com.saletech;

import java.util.List;
import java.util.Optional;

/**
 * ProductRepository
 *
 * Defines the contract for any class that manages Product data.
 * This interface allows the rest of the application to remain
 * independent of how products are stored (in memory, database, etc.).
 *
 * In early modules, the program uses the in-memory implementation:
 *   InMemoryProductRepository
 *
 * In Module 5, a database-backed version (e.g., ProductRepositoryDB)
 * will replace or supplement the in-memory version, without requiring
 * changes to the rest of the application.
 */
public interface ProductRepository {

    /**
     * Retrieves a list of all products currently stored.
     *
     * @return List of Product objects
     */
    List<Product> findAll();

    /**
     * Finds a product by its unique ID.
     * Uses Optional to safely represent "found" or "not found."
     *
     * @param id The product ID
     * @return Optional containing the product if found
     */
    Optional<Product> findById(int id);

    /**
     * Searches products whose name or manufacturer matches the search term.
     * The implementation handles case-insensitive matching.
     *
     * @param term Search text entered by the user
     * @return List of matching products
     */
    List<Product> searchByNameOrManufacturer(String term);

    /**
     * Stores a new product in the repository.
     */
    void save(Product product);

    /**
     * Updates inventory quantity for a product.
     *
     * @param id          Target product ID
     * @param newQuantity New inventory level
     */
    void updateQuantity(int id, int newQuantity);
}
