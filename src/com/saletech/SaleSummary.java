package com.saletech;

import java.time.LocalDateTime;

/**
 * SaleSummary
 *
 * Lightweight view for reports.
 */
public class SaleSummary {

    private final int saleId;
    private final LocalDateTime createdAt;
    private final String customerName;
    private final String customerEmail;
    private final double total;

    public SaleSummary(int saleId, LocalDateTime createdAt, String customerName, String customerEmail, double total) {
        this.saleId = saleId;
        this.createdAt = createdAt;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.total = total;
    }

    public int getSaleId() { return saleId; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public String getCustomerName() { return customerName; }

    public String getCustomerEmail() { return customerEmail; }

    public double getTotal() { return total; }

    @Override
    public String toString() {
        return "Sale[id=" + saleId + ", createdAt=" + createdAt + ", customer=" + customerName +
                ", email=" + customerEmail + ", total=" + String.format("%.2f", total) + "]";
    }
}
