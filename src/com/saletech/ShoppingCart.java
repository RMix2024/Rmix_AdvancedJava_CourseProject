package com.saletech;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private final List<CartItem> items = new ArrayList<>();

    public void addItem(Product p, int qty) {
        for (CartItem item : items) {
            if (item.getProduct().getId() == p.getId()) {
                item.increment(qty);
                return;
            }
        }
        items.add(new CartItem(p, qty));
    }

    public List<CartItem> getItems() { return items; }

    public double getTotal() {
        return items.stream().mapToDouble(CartItem::getLineTotal).sum();
    }

    public void printCart() {
        if (items.isEmpty()) {
            System.out.println("Cart empty.");
            return;
        }
        for (CartItem item : items) {
            System.out.printf("%s x%d = %.2f\n",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getLineTotal());
        }
    }

    public void clear() {
        items.clear();
    }
}
