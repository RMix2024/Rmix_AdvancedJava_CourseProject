package com.saletech;

import java.util.ArrayList;
import java.util.List;

public class Sale {

    private static int nextId = 1;

    private final int id;
    private final Customer customer;
    private final List<SaleLine> lines = new ArrayList<>();

    public Sale(Customer customer) {
        this.id = nextId++;
        this.customer = customer;
    }

    public int getId() { return id; }

    public void addLine(Product product, int quantity, double price) {
        lines.add(new SaleLine(product, quantity, price));
    }

    public double getTotal() {
        return lines.stream().mapToDouble(SaleLine::getLineTotal).sum();
    }
}
