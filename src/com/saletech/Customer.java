package com.saletech;

/**
 * Customer
 *
 * Represents a single customer within the SaleTech system. 
 * Each customer has a unique ID, a name, and an email address.
 *
 * In this version of the program, customer data is stored only
 * in memory. In Module 5, this class will map to a database table
 * and be stored permanently using SQL and repository classes.
 */
public class Customer {

    // Static counter used to auto-generate unique customer IDs
    private static int nextId = 1;

    // Unique identifier for each customer
    private final int id;

    // Customer's full name
    private final String name;

    // Customer's email address (used for searching)
    private final String email;

    /**
     * Constructor used to create a new customer record.
     * Each new customer automatically receives the next available ID.
     *
     * @param name   Customer's full name
     * @param email  Customer's email address
     */
    public Customer(String name, String email) {
        this.id = nextId++;   // Assign and increment auto-ID
        this.name = name;
        this.email = email;
    }

    // ----- Getter Methods -----

    /** @return Unique customer ID */
    public int getId() { return id; }

    /** @return Customer's name */
    public String getName() { return name; }

    /** @return Customer's email */
    public String getEmail() { return email; }

    /**
     * Returns a readable string representation of the customer,
     * typically used when displaying lists of customers.
     */
    @Override
    public String toString() {
        return "Customer[id=" + id + ", name=" + name + ", email=" + email + "]";
    }
}
