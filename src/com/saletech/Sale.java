package com.saletech;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sale
 *
 * Represents a completed sales transaction.
 * In the finished version, a Sale maps to a database row and its SaleLines
 * map to related rows.
 */
public class Sale {

    // Database primary key (0 means not yet saved)
    private int id;

    private final Customer customer;
    private final List<SaleLine> lines = new ArrayList<>();

    public Sale(Customer customer) {
        this(0, customer);
    }

    public Sale(int id, Customer customer) {
        this.id = id;
        this.customer = customer;
    }

    public int getId() {
        return id;
    }

    // Used by repositories after INSERT
    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void addLine(Product product, int quantity, double price) {
        lines.add(new SaleLine(product, quantity, price));
    }

    public List<SaleLine> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public double getTotal() {
        return lines.stream()
                .mapToDouble(SaleLine::getLineTotal)
                .sum();
    }
}
