package com.saletech;

/**
 * CartItem
 *
 * Represents a single entry inside the ShoppingCart.
 * Each CartItem links a Product with a quantity, allowing
 * the cart to track how many units of each product the
 * customer intends to purchase.
 *
 * This class is used only during the current shopping session.
 * After checkout, each CartItem is converted into a SaleLine
 * and stored inside a Sale record.
 */
public class CartItem {

    // The product being purchased
    private final Product product;

    // Number of units of this product in the cart
    private int quantity;

    /**
     * Constructs a new CartItem.
     *
     * @param product  The product being added to the cart
     * @param quantity Initial quantity selected
     */
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    /** @return Product associated with this cart item */
    public Product getProduct() { 
        return product; 
    }

    /** @return Quantity of this product in the cart */
    public int getQuantity() { 
        return quantity; 
    }

    /**
     * Increases the quantity of this cart item.
     * Used when the user adds the same product again.
     *
     * @param amount Number of units to add
     */
    public void increment(int amount) {
        this.quantity += amount;
    }

    /**
     * Computes the total price for this item in the cart:
     * (price per unit) * (quantity)
     *
     * @return Total dollar amount for this cart line
     */
    public double getLineTotal() {
        return product.getPrice() * quantity;
    }
}
