package com.saletech;

import java.util.List;

/**
 * SaleRepository
 *
 * Abstraction for persisting sales transactions.
 */
public interface SaleRepository {

    /**
     * Saves the sale and its line items to the database.
     * Returns the saved Sale with an assigned database id.
     */
    Sale save(Sale sale);

    /**
     * Returns a simple list of recent sales for reporting.
     */
    List<SaleSummary> findRecentSummaries(int limit);
}
