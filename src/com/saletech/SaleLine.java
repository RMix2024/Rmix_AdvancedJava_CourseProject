package com.saletech;

/**
 * SaleLine
 *
 * Represents a single line item inside a Sale transaction.
 * This maps cleanly to a database row in a sale_lines table.
 */
public class SaleLine {

    private final Product product;
    private final int quantity;
    private final double unitPrice;

    public SaleLine(Product product, int quantity, double unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Product getProduct() { return product; }

    public int getQuantity() { return quantity; }

    public double getUnitPrice() { return unitPrice; }

    public double getLineTotal() {
        return unitPrice * quantity;
    }
}
