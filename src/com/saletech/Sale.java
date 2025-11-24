package com.saletech;

import java.util.ArrayList;
import java.util.List;

/**
 * Sale
 *
 * Represents a completed sales transaction in the SaleTech system.
 * A Sale object is created during checkout and is linked to a specific
 * Customer. Each sale contains one or more SaleLine objects, where
 * each SaleLine represents a purchased product along with its quantity
 * and unit price at the time of purchase.
 *
 * In future modules (Module 5), this class will map directly to a
 * database table, and its SaleLine entries will map to a related table.
 */
public class Sale {

    // Static counter used to generate unique sale IDs
    private static int nextId = 1;

    // Unique identifier for this sale
    private final int id;

    // The customer associated with this sale
    private final Customer customer;

    // List of individual purchased items (product + quantity + price)
    private final List<SaleLine> lines = new ArrayList<>();

    /**
     * Creates a new sale for the specified customer.
     * Automatically assigns the next available sale ID.
     *
     * @param customer The customer completing this purchase
     */
    public Sale(Customer customer) {
        this.id = nextId++;     // Auto-increment sale ID
        this.customer = customer;
    }

    /** 
     * @return The unique ID of this sale
     */
    public int getId() { 
        return id; 
    }

    /**
     * Adds a new line item to the sale.
     * Each line represents an individual product purchase.
     *
     * @param product  The product being purchased
     * @param quantity Quantity purchased
     * @param price    Price per item at the moment of sale
     */
    public void addLine(Product product, int quantity, double price) {
        lines.add(new SaleLine(product, quantity, price));
    }

    /**
     * Calculates the total price of the sale by summing all line totals.
     * (SaleLine::getLineTotal multiplies unit price * quantity)
     *
     * @return Total cost of the sale
     */
    public double getTotal() {
        return lines.stream()
                .mapToDouble(SaleLine::getLineTotal)
                .sum();
    }
}
