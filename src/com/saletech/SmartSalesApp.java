package com.saletech;

import java.util.*;

/**
 * SmartSalesApp
 *
 * This class is the main entry point for the SaleTech Innovations
 * sales application. It provides a console-based user interface
 * and handles user-driven actions such as searching products,
 * adding items to a cart, checking out, and managing customers.
 *
 * Later modules will connect this logic to a database and expand
 * the system into a full inventory/order solution.
 */
public class SmartSalesApp {

    // Scanner used for all user input from the console
    private static final Scanner SCANNER = new Scanner(System.in);

    // Repository pattern for products (currently in-memory)
    private static final ProductRepository productRepository =
            new InMemoryProductRepository();

    // Stores customer records (later modules will store these in a DB)
    private static final List<Customer> customers = new ArrayList<>();

    // Stores completed sales transactions
    private static final List<Sale> sales = new ArrayList<>();

    // Shopping cart instance shared during a user session
    private static final ShoppingCart cart = new ShoppingCart();

    /**
     * Application entry point.
     * Loads sample inventory, then displays the main menu loop.
     */
    public static void main(String[] args) {
        seedSampleData();   // Populate test data for demonstration
        boolean running = true;

        // Main application loop
        while (running) {
            printMainMenu();
            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1 -> handleSearchProducts();
                case 2 -> handleDisplayInventory();
                case 3 -> handleAddToCart();
                case 4 -> handleViewCart();
                case 5 -> handleCheckout();
                case 6 -> handleCustomerMenu();
                case 0 -> {
                    System.out.println("Exiting Smart Sales Application. Goodbye.");
                    running = false;  // End program loop
                }
                default -> System.out.println("Invalid option.");
            }
            System.out.println();
        }
    }

    /**
     * Prints the main application menu.
     */
    private static void printMainMenu() {
        System.out.println("======================================");
        System.out.println(" SaleTech Innovations - Smart Sales");
        System.out.println("======================================");
        System.out.println("1. Search products");
        System.out.println("2. Display inventory");
        System.out.println("3. Add product to cart");
        System.out.println("4. View cart");
        System.out.println("5. Checkout");
        System.out.println("6. Customer management");
        System.out.println("0. Exit");
    }

    /**
     * Allows user to search inventory by product name or manufacturer.
     */
    private static void handleSearchProducts() {
        System.out.print("Enter search term: ");
        String term = SCANNER.nextLine();

        // Repository performs case-insensitive search
        List<Product> results = productRepository.searchByNameOrManufacturer(term);

        if (results.isEmpty()) {
            System.out.println("No products found.");
        } else {
            results.forEach(System.out::println);
        }
    }

    /**
     * Displays all products currently available in the inventory.
     */
    private static void handleDisplayInventory() {
        productRepository.findAll().forEach(System.out::println);
    }

    /**
     * Allows the user to select an item from the inventory and add
     * it to their cart if the quantity requested is available.
     */
    private static void handleAddToCart() {
        handleDisplayInventory();
        int id = readInt("Enter product id: ");

        Optional<Product> p = productRepository.findById(id);
        if (p.isEmpty()) {
            System.out.println("Product not found.");
            return;
        }

        int qty = readInt("Quantity: ");

        // Basic validation for inventory control
        if (qty <= 0 || qty > p.get().getQuantityInStock()) {
            System.out.println("Invalid quantity.");
            return;
        }

        cart.addItem(p.get(), qty);
        System.out.println("Item added.");
    }

    /**
     * Displays all items currently in the shopping cart.
     */
    private static void handleViewCart() {
        cart.printCart();
        System.out.printf("Total: $%.2f\n", cart.getTotal());
    }

    /**
     * Converts current cart contents into a completed sale.
     * Updates product inventory levels and clears the cart.
     */
    private static void handleCheckout() {
        if (cart.getItems().isEmpty()) {
            System.out.println("Cart empty.");
            return;
        }

        // User must be connected to a customer record
        Customer customer = selectOrCreateCustomer();
        if (customer == null) return;

        // Create a new sale transaction
        Sale sale = new Sale(customer);

        // Convert cart items into sale lines and adjust stock
        for (CartItem item : cart.getItems()) {
            sale.addLine(item.getProduct(), item.getQuantity(), item.getProduct().getPrice());

            // Update inventory count
            productRepository.updateQuantity(
                    item.getProduct().getId(),
                    item.getProduct().getQuantityInStock() - item.getQuantity()
            );
        }

        sales.add(sale);  // Record sale
        cart.clear();     // Empty cart after purchase

        System.out.println("Checkout complete.");
        System.out.println("Sale id: " + sale.getId());
        System.out.printf("Sale Total: %.2f\n", sale.getTotal());
    }

    /**
     * Displays menu options for customer management:
     * - Create new customer
     * - Find customer by email
     * - List existing customers
     */
    private static void handleCustomerMenu() {
        System.out.println("1. Create customer");
        System.out.println("2. Find by email");
        System.out.println("3. List all");
        System.out.println("0. Back");

        int choice = readInt("Choose: ");

        switch (choice) {
            case 1 -> createCustomer();
            case 2 -> findCustomerByEmail();
            case 3 -> customers.forEach(System.out::println);
        }
    }

    /**
     * Creates a new customer object from user input.
     */
    private static void createCustomer() {
        System.out.print("Name: ");
        String name = SCANNER.nextLine();

        System.out.print("Email: ");
        String email = SCANNER.nextLine();

        Customer c = new Customer(name, email);
        customers.add(c);

        System.out.println("Customer created with id: " + c.getId());
    }

    /**
     * Searches the customer list by email address.
     */
    private static void findCustomerByEmail() {
        System.out.print("Email: ");
        String email = SCANNER.nextLine();

        customers.stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .ifPresentOrElse(
                        System.out::println,
                        () -> System.out.println("Not found.")
                );
    }

    /**
     * Allows user to either select an existing customer
     * or create a new one during checkout.
     */
    private static Customer selectOrCreateCustomer() {
        System.out.println("1. Existing customer");
        System.out.println("2. Create new");
        System.out.println("0. Cancel");

        int choice = readInt("Choose: ");

        return switch (choice) {
            case 1 -> {
                customers.forEach(System.out::println);
                int id = readInt("Customer id: ");

                // Return matching customer or null
                yield customers.stream()
                        .filter(c -> c.getId() == id)
                        .findFirst()
                        .orElse(null);
            }
            case 2 -> {
                createCustomer();
                yield customers.get(customers.size() - 1);  // return newly created
            }
            default -> null;
        };
    }

    /**
     * Utility method that repeatedly prompts the user until a valid
     * integer is entered. Helps prevent input mismatch errors.
     */
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);

            try {
                return Integer.parseInt(SCANNER.nextLine());
            } catch (Exception e) {
                System.out.println("Enter a valid number.");
            }
        }
    }

    /**
     * Seeds the application with a small test inventory.
     * This replaces database interaction during early modules.
     */
    private static void seedSampleData() {
        productRepository.save(new Product(1, "USB Cable", "TechCo", 9.99, 50));
        productRepository.save(new Product(2, "Wireless Mouse", "ClickTech", 24.99, 30));
        productRepository.save(new Product(3, "Laptop Stand", "ErgoWare", 39.99, 20));
        productRepository.save(new Product(4, "4K Monitor", "ViewMax", 249.99, 10));
    }
}
