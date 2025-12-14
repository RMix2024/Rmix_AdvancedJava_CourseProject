package com.saletech;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DbCustomerRepository
 *
 * JDBC backed customer repository.
 * Uses the customers table.
 */
public class DbCustomerRepository implements CustomerRepository {

    private static final Logger LOGGER =
            Logger.getLogger(DbCustomerRepository.class.getName());

    // Keep these consistent with DbProductRepository
    private static final String URL = "jdbc:mysql://localhost:3306/saletech";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        return new Customer(id, name, email);
    }

    @Override
    public Optional<Customer> findById(int id) {
        String sql = "SELECT id, name, email FROM customers WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding customer by id " + id, e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        String sql = "SELECT id, name, email FROM customers WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding customer by email " + email, e);
        }

        return Optional.empty();
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT id, name, email FROM customers ORDER BY name";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customers.add(mapRow(rs));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all customers", e);
        }

        return customers;
    }

    @Override
    public Customer createOrGetByEmail(String name, String email) {
        // Try read first
        Optional<Customer> existing = findByEmail(email);
        if (existing.isPresent()) {
            return existing.get();
        }

        String sql = "INSERT INTO customers (name, email) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    return new Customer(id, name, email);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating customer " + email, e);
        }

        // Fallback if insert failed or key missing
        return new Customer(name, email);
    }
}
