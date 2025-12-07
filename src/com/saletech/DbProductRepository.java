package com.saletech;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DbProductRepository
 *
 * Database backed implementation of ProductRepository.
 * Uses JDBC to talk to the "products" table.
 */
public class DbProductRepository implements ProductRepository {

    private static final Logger LOGGER =
            Logger.getLogger(DbProductRepository.class.getName());

    // Update these three values to match your database
    private static final String URL =
            "jdbc:mysql://localhost:3306/saletech";   // database name from the SQL script
    private static final String USER = "root";        // your DB user
    private static final String PASSWORD = "";        // your DB password

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String manufacturer = rs.getString("manufacturer");
        double price = rs.getDouble("price");
        int qty = rs.getInt("quantity_in_stock");

        return new Product(id, name, manufacturer, price, qty);
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, name, manufacturer, price, quantity_in_stock FROM products";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all products from database", e);
        }

        return products;
    }

    @Override
    public Optional<Product> findById(int id) {
        String sql = "SELECT id, name, manufacturer, price, quantity_in_stock " +
                     "FROM products WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToProduct(rs));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching product by id " + id, e);
        }

        return Optional.empty();
    }

    @Override
    public List<Product> searchByNameOrManufacturer(String term) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, name, manufacturer, price, quantity_in_stock " +
                     "FROM products " +
                     "WHERE LOWER(name) LIKE ? OR LOWER(manufacturer) LIKE ?";

        String pattern = "%" + term.toLowerCase() + "%";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pattern);
            stmt.setString(2, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRowToProduct(rs));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    "Error searching products by term '" + term + "'", e);
        }

        return products;
    }

    @Override
    public void save(Product product) {
        String sql = "INSERT INTO products " +
                     "(id, name, manufacturer, price, quantity_in_stock) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, product.getId());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getManufacturer());
            stmt.setDouble(4, product.getPrice());
            stmt.setInt(5, product.getQuantityInStock());

            stmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    "Error saving product with id " + product.getId(), e);
        }
    }

    @Override
    public void updateQuantity(int id, int newQuantity) {
        String sql = "UPDATE products SET quantity_in_stock = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newQuantity);
            stmt.setInt(2, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    "Error updating quantity for product id " + id, e);
        }
    }
}
