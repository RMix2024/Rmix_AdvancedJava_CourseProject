package com.saletech;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.*;



/**
 * SmartSalesApp
 *
 * Console UI for SaleTech Innovations Smart Sales.
 * Finished product version: products, customers, and sales are persisted in the database.
 */
public class SmartSalesApp {

    private static final Logger LOGGER =
            Logger.getLogger(SmartSalesApp.class.getName());

    static {
        try {
            LogManager.getLogManager().reset();

            LOGGER.setLevel(Level.ALL);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(consoleHandler);

            FileHandler fileHandler = new FileHandler("smartsalesapp.log", true);
            fileHandler.setLevel(Level.FINE);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);

            LOGGER.config("SmartSalesApp logger configured successfully.");

        } catch (IOException e) {
            System.err.println("Failed to set up logging for SmartSalesApp.");
            e.printStackTrace();
        }
    }

    private static final Scanner SCANNER = new Scanner(System.in);

    private static final ProductRepository productRepository = new DbProductRepository();
    private static final CustomerRepository customerRepository = new DbCustomerRepository();
    private static final SaleRepository saleRepository = new DbSaleRepository();

    private static final ShoppingCart cart = new ShoppingCart();

    public static void main(String[] args) {
        if (!testDatabase()) {
            System.out.println("Database connection failed. Please verify your DB settings.");
            return;
        }

        boolean running = true;
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
                case 7 -> handleReportsMenu();
                case 0 -> {
                    System.out.println("Exiting Smart Sales Application. Goodbye.");
                    running = false;
                }
                default -> System.out.println("Invalid option.");
            }
            System.out.println();
        }
    }

    private static boolean testDatabase() {
        try {
            int count = productRepository.findAll().size();
            LOGGER.info("Database connected. Products loaded: " + count);
            return true;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Database test failed.", ex);
            return false;
        }
    }

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
        System.out.println("7. Reports");
        System.out.println("0. Exit");
    }

    private static void handleSearchProducts() {
        System.out.print("Enter search term: ");
        String term = SCANNER.nextLine().trim();

        if (term.isEmpty()) {
            System.out.println("Search term cannot be empty.");
            return;
        }

        try {
            List<Product> results = productRepository.searchByNameOrManufacturer(term);
            if (results.isEmpty()) {
                System.out.println("No products found.");
            } else {
                results.forEach(System.out::println);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Search failed for term: " + term, ex);
            System.out.println("Search failed. Please try again.");
        }
    }

    private static void handleDisplayInventory() {
        try {
            productRepository.findAll().forEach(System.out::println);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to display inventory.", ex);
            System.out.println("Unable to load inventory right now.");
        }
    }

    private static void handleAddToCart() {
        handleDisplayInventory();
        int id = readInt("Enter product id: ");

        Optional<Product> p;
        try {
            p = productRepository.findById(id);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "DB error while finding product id: " + id, ex);
            System.out.println("Unable to look up product right now.");
            return;
        }

        if (p.isEmpty()) {
            System.out.println("Product not found.");
            return;
        }

        Product product = p.get();
        int qty = readInt("Quantity: ");

        if (qty <= 0) {
            System.out.println("Invalid quantity.");
            return;
        }

        if (qty > product.getQuantityInStock()) {
            System.out.println("Invalid quantity.");
            System.out.println("Available: " + product.getQuantityInStock());
            return;
        }

        cart.addItem(product, qty);
        System.out.println("Item added.");
        LOGGER.info("Added to cart: " + qty + " x " + product.getName() + " (id " + product.getId() + ")");
    }

    private static void handleViewCart() {
        cart.printCart();
        System.out.printf("Total: $%.2f%n", cart.getTotal());
    }

    private static void handleCheckout() {
        if (cart.getItems().isEmpty()) {
            System.out.println("Cart empty.");
            return;
        }

        Customer customer = selectOrCreateCustomerDb();
        if (customer == null || customer.getId() <= 0) {
            System.out.println("Checkout cancelled.");
            return;
        }

        Sale sale = new Sale(customer);

        // Validate and apply inventory updates
        for (CartItem item : cart.getItems()) {
            int productId = item.getProduct().getId();
            int qtyRequested = item.getQuantity();

            Optional<Product> fresh = productRepository.findById(productId);
            if (fresh.isEmpty()) {
                System.out.println("Checkout failed. Product missing: " + productId);
                return;
            }

            Product p = fresh.get();
            int available = p.getQuantityInStock();

            if (qtyRequested > available) {
                System.out.println("Checkout failed. Not enough stock for: " + p.getName());
                System.out.println("Requested: " + qtyRequested + " Available: " + available);
                return;
            }
        }

        // Add lines and update inventory
        for (CartItem item : cart.getItems()) {
            int productId = item.getProduct().getId();
            int qty = item.getQuantity();

            Optional<Product> fresh = productRepository.findById(productId);
            if (fresh.isEmpty()) {
                System.out.println("Checkout failed. Product missing: " + productId);
                return;
            }

            Product p = fresh.get();

            sale.addLine(p, qty, p.getPrice());

            productRepository.updateQuantity(productId, p.getQuantityInStock() - qty);
        }

        // Persist the sale to DB
        Sale saved = saleRepository.save(sale);

        cart.clear();

        System.out.println("Checkout complete.");
        System.out.println("Sale id: " + saved.getId());
        System.out.printf("Sale Total: %.2f%n", saved.getTotal());

        LOGGER.info("Checkout completed for sale id " + saved.getId() + " total " + saved.getTotal());
    }

    private static void handleCustomerMenu() {
        System.out.println("1. Create customer");
        System.out.println("2. Find by email");
        System.out.println("3. List all");
        System.out.println("0. Back");

        int choice = readInt("Choose: ");

        switch (choice) {
            case 1 -> createCustomerDb();
            case 2 -> findCustomerByEmailDb();
            case 3 -> listCustomersDb();
            case 0 -> { }
            default -> System.out.println("Invalid option.");
        }
    }

    private static void createCustomerDb() {
        System.out.print("Name: ");
        String name = SCANNER.nextLine().trim();

        System.out.print("Email: ");
        String email = SCANNER.nextLine().trim();

        if (name.isEmpty() || email.isEmpty()) {
            System.out.println("Name and email are required.");
            return;
        }

        Customer c = customerRepository.createOrGetByEmail(name, email);
        if (c.getId() > 0) {
            System.out.println("Customer saved with id: " + c.getId());
        } else {
            System.out.println("Customer could not be saved. Please try again.");
        }
    }

    private static void findCustomerByEmailDb() {
        System.out.print("Email: ");
        String email = SCANNER.nextLine().trim();

        if (email.isEmpty()) {
            System.out.println("Email is required.");
            return;
        }

        Optional<Customer> c = customerRepository.findByEmail(email);
        if (c.isPresent()) {
            System.out.println(c.get());
        } else {
            System.out.println("Not found.");
        }
    }

    private static void listCustomersDb() {
        List<Customer> customers = customerRepository.findAll();
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }
        customers.forEach(System.out::println);
    }

    private static Customer selectOrCreateCustomerDb() {
        System.out.println("1. Existing customer");
        System.out.println("2. Create new");
        System.out.println("0. Cancel");

        int choice = readInt("Choose: ");

        return switch (choice) {
            case 1 -> {
                List<Customer> customers = customerRepository.findAll();
                if (customers.isEmpty()) {
                    System.out.println("No customers available. Create one first.");
                    yield null;
                }
                customers.forEach(System.out::println);
                int id = readInt("Customer id: ");
                yield customerRepository.findById(id).orElse(null);
            }
            case 2 -> {
                System.out.print("Name: ");
                String name = SCANNER.nextLine().trim();
                System.out.print("Email: ");
                String email = SCANNER.nextLine().trim();

                if (name.isEmpty() || email.isEmpty()) {
                    System.out.println("Name and email are required.");
                    yield null;
                }

                yield customerRepository.createOrGetByEmail(name, email);
            }
            default -> null;
        };
    }

    private static void handleReportsMenu() {
        System.out.println("======================================");
        System.out.println(" Reports");
        System.out.println("======================================");
        System.out.println("1. Inventory Report (Database)");
        System.out.println("2. Low Stock Report (Database)");
        System.out.println("3. Recent Sales Report (Database)");
        System.out.println("0. Back");

        int choice = readInt("Choose: ");

        switch (choice) {
            case 1 -> printInventoryReportDb();
            case 2 -> printLowStockReportDb();
            case 3 -> printRecentSalesReportDb();
            case 0 -> { }
            default -> System.out.println("Invalid option.");
        }
    }

    private static void printInventoryReportDb() {
        System.out.println("INVENTORY REPORT (DATABASE)");
        System.out.println("--------------------------------------");

        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            System.out.println("No products found in inventory.");
            return;
        }

        int totalSkus = products.size();
        int totalUnits = products.stream().mapToInt(Product::getQuantityInStock).sum();

        products.forEach(System.out::println);

        System.out.println("--------------------------------------");
        System.out.println("Total SKUs: " + totalSkus);
        System.out.println("Total Units in Stock: " + totalUnits);
    }

    private static void printLowStockReportDb() {
        int threshold = readInt("Low stock threshold: ");
        if (threshold < 0) {
            System.out.println("Threshold must be 0 or greater.");
            return;
        }

        System.out.println("LOW STOCK REPORT (DATABASE)");
        System.out.println("Threshold: " + threshold);
        System.out.println("--------------------------------------");

        List<Product> products = productRepository.findAll();
        boolean any = false;

        for (Product p : products) {
            if (p.getQuantityInStock() <= threshold) {
                System.out.println(p);
                any = true;
            }
        }

        if (!any) {
            System.out.println("No low stock items found.");
        }
    }

    private static void printRecentSalesReportDb() {
        int limit = readInt("How many recent sales: ");
        if (limit <= 0) {
            System.out.println("Enter a number greater than 0.");
            return;
        }

        System.out.println("RECENT SALES REPORT (DATABASE)");
        System.out.println("--------------------------------------");

        List<SaleSummary> sales = saleRepository.findRecentSummaries(limit);
        if (sales.isEmpty()) {
            System.out.println("No sales found.");
            return;
        }

        double grandTotal = 0.0;
        for (SaleSummary s : sales) {
            System.out.println(s);
            grandTotal += s.getTotal();
        }

        System.out.println("--------------------------------------");
        System.out.println("Sales Count: " + sales.size());
        System.out.println("Grand Total: " + String.format("%.2f", grandTotal));
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine();

            try {
                return Integer.parseInt(line.trim());
            } catch (Exception e) {
                System.out.println("Enter a valid number.");
                LOGGER.log(Level.WARNING, "Invalid numeric input: \"" + line + "\"", e);
                          }
        }
    }
}
