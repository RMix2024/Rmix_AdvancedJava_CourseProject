package com.saletech;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DbSaleRepository
 *
 * JDBC backed sale persistence with a transaction.
 */
public class DbSaleRepository implements SaleRepository {

    private static final Logger LOGGER =
            Logger.getLogger(DbSaleRepository.class.getName());

    // Keep these consistent with DbProductRepository
    private static final String URL = "jdbc:mysql://localhost:3306/saletech";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public Sale save(Sale sale) {
        // Match your actual schema:
        // sales: id, customer_id, sale_date, total
        // sale_lines: id, sale_id, product_id, quantity, unit_price, line_total
        final String insertSaleSql =
                "INSERT INTO sales (customer_id, total) VALUES (?, ?)";
        final String insertLineSql =
                "INSERT INTO sale_lines (sale_id, product_id, quantity, unit_price, line_total) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            int saleId;

            try (PreparedStatement saleStmt =
                         conn.prepareStatement(insertSaleSql, Statement.RETURN_GENERATED_KEYS)) {

                if (sale.getCustomer() == null || sale.getCustomer().getId() <= 0) {
                    conn.rollback();
                    throw new SQLException("Sale must have a valid customer with a DB id.");
                }

                saleStmt.setInt(1, sale.getCustomer().getId());
                saleStmt.setDouble(2, sale.getTotal());

                int affected = saleStmt.executeUpdate();
                if (affected != 1) {
                    conn.rollback();
                    throw new SQLException("Insert into sales failed. Rows affected: " + affected);
                }

                try (ResultSet keys = saleStmt.getGeneratedKeys()) {
                    if (!keys.next()) {
                        conn.rollback();
                        throw new SQLException("Failed to obtain generated sale id.");
                    }
                    saleId = keys.getInt(1);
                }
            }

            // Insert sale lines
            try (PreparedStatement lineStmt = conn.prepareStatement(insertLineSql)) {
                List<SaleLine> lines = sale.getLines();
                if (lines == null || lines.isEmpty()) {
                    conn.rollback();
                    throw new SQLException("Cannot save a sale with no line items.");
                }

                for (SaleLine line : lines) {
                    if (line.getProduct() == null || line.getProduct().getId() <= 0) {
                        conn.rollback();
                        throw new SQLException("SaleLine must have a valid product with a DB id.");
                    }
                    if (line.getQuantity() <= 0) {
                        conn.rollback();
                        throw new SQLException("SaleLine quantity must be > 0.");
                    }

                    double unitPrice = line.getUnitPrice();
                    double lineTotal = unitPrice * line.getQuantity();

                    lineStmt.setInt(1, saleId);
                    lineStmt.setInt(2, line.getProduct().getId());
                    lineStmt.setInt(3, line.getQuantity());
                    lineStmt.setDouble(4, unitPrice);
                    lineStmt.setDouble(5, lineTotal);
                    lineStmt.addBatch();
                }

                int[] counts = lineStmt.executeBatch();
                if (counts.length == 0) {
                    conn.rollback();
                    throw new SQLException("Insert into sale_lines failed. No rows inserted.");
                }
            }

            conn.commit();

            // Update the in-memory object with the DB-generated id
            sale.setId(saleId);
            return sale;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving sale to database", e);
        }

        return sale;
    }

    @Override
    public List<SaleSummary> findRecentSummaries(int limit) {
        List<SaleSummary> results = new ArrayList<>();

        // Match your actual schema (sale_date + total)
        String sql =
                "SELECT s.id AS sale_id, s.sale_date, s.total, c.name, c.email " +
                "FROM sales s " +
                "JOIN customers c ON c.id = s.customer_id " +
                "ORDER BY s.sale_date DESC " +
                "LIMIT ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int saleId = rs.getInt("sale_id");
                    Timestamp ts = rs.getTimestamp("sale_date");
                    LocalDateTime createdAt = (ts == null) ? null : ts.toLocalDateTime();
                    double total = rs.getDouble("total");
                    String name = rs.getString("name");
                    String email = rs.getString("email");

                    results.add(new SaleSummary(saleId, createdAt, name, email, total));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching recent sale summaries", e);
        }

        return results;
    }
}
