package com.saletech;

import java.util.List;
import java.util.Optional;

/**
 * CustomerRepository
 *
 * Abstraction for customer persistence.
 */
public interface CustomerRepository {

    Optional<Customer> findById(int id);

    Optional<Customer> findByEmail(String email);

    List<Customer> findAll();

    /**
     * Creates the customer if it does not exist.
     * If a customer with the same email exists, returns the existing record.
     */
    Customer createOrGetByEmail(String name, String email);
}
