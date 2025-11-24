package com.saletech;

/**
 * SaleLine
 *
 * Represents a single line item inside a Sale transaction.
 * Each SaleLine stores:
 *  - The product that was purchased
 *  - The quantity bought
 *  - The unit price at the time of checkout
 *
 * SaleLines are created inside the Sale class whenever the user
 * checks out their cart. Later on (Module 5), this class will map
 * to a database table that links each entry back to its Sale record.
 */
public class SaleLine {

    // Product purchased in this line item
    private final Product product;

    // Number of units purchased
    private final int quantity;

    // Price per unit at the moment of sale
    private final double unitPrice;

    /**
     * Constructs a new sale line item.
     *
     * @param product    The product being purchased
     * @param quantity   Number of units bought
     * @param unitPrice  Cost per unit at checkout time
     */
    public SaleLine(Product product, int quantity, double unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /**
     * Calculates the total cost of this line item by multiplying
     * quantity by unit price.
     *
     * @return Total cost of this sale line
     */
    public double getLineTotal() {
        return unitPrice * quantity;
    }
}
