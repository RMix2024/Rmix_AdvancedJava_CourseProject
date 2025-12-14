package com.saletech;

/**
 * Customer
 *
 * Represents a customer within the SaleTech system.
 * In the finished version, customer records are persisted in the database.
 */
public class Customer {

    // Database primary key (0 means not yet saved)
    private int id;

    private final String name;
    private final String email;

    /**
     * Create a new customer that has not yet been saved to the database.
     */
    public Customer(String name, String email) {
        this(0, name, email);
    }

    /**
     * Create a customer with a known database id.
     */
    public Customer(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() { return id; }

    // Used by repositories after INSERT
    void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public String getEmail() { return email; }

    @Override
    public String toString() {
        return "Customer[id=" + id + ", name=" + name + ", email=" + email + "]";
    }
}
