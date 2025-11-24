package com.saletech;

public class CartItem {

    private final Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }

    public void increment(int amount) {
        this.quantity += amount;
    }

    public double getLineTotal() {
        return product.getPrice() * quantity;
    }
}
