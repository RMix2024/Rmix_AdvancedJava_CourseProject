package com.saletech;

import java.util.ArrayList;
import java.util.List;

/**
 * ShoppingCart
 *
 * Represents a temporary collection of items the user intends to purchase.
 * The cart only exists during the user's current session and is cleared
 * after checkout. Each entry in the cart is stored as a CartItem, which
 * tracks both the product and the quantity being purchased.
 */
public class ShoppingCart {

    // List holding all items currently in the shopping cart
    private final List<CartItem> items = new ArrayList<>();

    /**
     * Adds a product to the shopping cart. If the product is already
     * present in the cart, its quantity is increased instead of creating
     * a duplicate entry.
     *
     * @param p   Product being added to the cart
     * @param qty Quantity of the product to add
     */
    public void addItem(Product p, int qty) {
        // Check if item already exists in the cart
        for (CartItem item : items) {
            if (item.getProduct().getId() == p.getId()) {
                // Increase quantity instead of adding a second entry
                item.increment(qty);
                return;
            }
        }
        // Otherwise add as a new CartItem
        items.add(new CartItem(p, qty));
    }

    /**
     * Returns all cart items. Used during checkout and cart display.
     *
     * @return List of CartItem objects
     */
    public List<CartItem> getItems() { return items; }

    /**
     * Calculates the total cost of the cart by summing each
     * CartItem's line total (price * quantity).
     *
     * @return Combined dollar total of the shopping cart
     */
    public double getTotal() {
        return items.stream()
                .mapToDouble(CartItem::getLineTotal)
                .sum();
    }

    /**
     * Prints the contents of the shopping cart in a readable format.
     * Displays product name, quantity, and total price per item.
     */
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

    /**
     * Clears the shopping cart, usually after checkout has completed.
     */
    public void clear() {
        items.clear();
    }
}
