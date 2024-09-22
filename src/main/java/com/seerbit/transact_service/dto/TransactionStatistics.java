package com.seerbit.transact_service.dto;

import lombok.*;
import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) representing statistics for transactions
 * within a certain time window.
 */
@Getter
@Setter
@Builder
@ToString
public class TransactionStatistics {

    // Timestamp of when the transaction occurred
    private long timestamp;

    // Sum of transaction amounts
    private BigDecimal sum;

    // Maximum transaction amount
    private BigDecimal max;

    // Minimum transaction amount
    private BigDecimal min;

    // Count of transactions
    private long count;

    /**
     * Default constructor initializes the statistics with
     * default values (timestamp 0, sum 0, count 0, min as max possible, max as min possible).
     */
    public TransactionStatistics() {
        this.timestamp = 0;
        this.sum = BigDecimal.ZERO;
        this.max = BigDecimal.valueOf(Double.MIN_VALUE);  // Initialize to smallest value
        this.min = BigDecimal.valueOf(Double.MAX_VALUE);  // Initialize to largest value
        this.count = 0;
    }

    /**
     * Parameterized constructor to initialize statistics with specific values.
     *
     * @param timestamp The timestamp of the transaction.
     * @param sum The sum of transaction amounts.
     * @param max The maximum transaction amount.
     * @param min The minimum transaction amount.
     * @param count The count of transactions.
     */
    public TransactionStatistics(long timestamp, BigDecimal sum, BigDecimal max, BigDecimal min, long count) {
        this.timestamp = timestamp;
        this.sum = sum;
        this.max = max;
        this.min = min;
        this.count = count;
    }

    /**
     * Updates the current statistics by adding a new transaction amount.
     *
     * @param amount The amount of the new transaction.
     */
    public void addTransaction(BigDecimal amount) {
        this.sum = this.sum.add(amount);  // Add the amount to the total sum
        this.max = this.max.max(amount);  // Update the maximum if the new amount is larger
        this.min = this.min.min(amount);  // Update the minimum if the new amount is smaller
        this.count++;  // Increment the transaction count
    }
}
