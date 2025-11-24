package com.saletech;

public class SaleLine {
    private final Product product;
    private final int quantity;
    private final double unitPrice;

    public SaleLine(Product product, int quantity, double unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public double getLineTotal() {
        return unitPrice * quantity;
    }
}
