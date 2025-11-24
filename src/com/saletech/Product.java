package com.saletech;

/**
 * Product
 *
 * Represents a single product within the SaleTech inventory system.
 * Each product has an ID, name, manufacturer, price, and a current
 * quantity in stock. This class acts as a basic data model and will
 * later be stored in a database once the project reaches Module 5.
 */
public class Product {

    // Unique identifier for the product (set once)
    private final int id;

    // Human-readable product name (ex: "Wireless Mouse")
    private final String name;

    // Company or brand that manufactures the item
    private final String manufacturer;

    // Price per individual item
    private final double price;

    // Number of units available in inventory
    private int quantityInStock;

    /**
     * Constructor used to create a new product object.
     *
     * @param id                Unique numeric ID for this product
     * @param name              Name or title of the product
     * @param manufacturer      Brand or manufacturer name
     * @param price             Cost per item
     * @param quantityInStock   Starting inventory count
     */
    public Product(int id, String name, String manufacturer,
                   double price, int quantityInStock) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.price = price;
        this.quantityInStock = quantityInStock;
    }

    // ----- Getter Methods -----

    /** @return Product ID */
    public int getId() { return id; }

    /** @return Product name */
    public String getName() { return name; }

    /** @return Manufacturer name */
    public String getManufacturer() { return manufacturer; }

    /** @return Product price */
    public double getPrice() { return price; }

    /** @return Current stock quantity */
    public int getQuantityInStock() { return quantityInStock; }

    // ----- Setter Methods -----

    /**
     * Updates the stock level for this product.
     *
     * @param quantity New inventory amount
     */
    public void setQuantityInStock(int quantity) {
        this.quantityInStock = quantity;
    }

    /**
     * Provides a clean string representation of the product, used when
     * printing inventory lists or search results.
     */
    @Override
    public String toString() {
        return String.format(
            "Product[id=%d, name=%s, maker=%s, price=%.2f, stock=%d]",
            id, name, manufacturer, price, quantityInStock
        );
    }
}
