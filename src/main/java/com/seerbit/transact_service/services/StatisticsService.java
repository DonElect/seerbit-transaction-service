package com.seerbit.transact_service.services;

import com.seerbit.transact_service.dto.StatisticsResponse;
import com.seerbit.transact_service.dto.TransactionStatistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class StatisticsService {

    private static final int TIME_WINDOW = 30;  // Time window for valid transactions (in seconds)
    private final ReentrantLock lock = new ReentrantLock();  // Ensures thread-safe access to statistics

    // Global statistics variables (shared across transactions)
    private volatile BigDecimal sum = BigDecimal.ZERO;
    private volatile BigDecimal max = BigDecimal.valueOf(Double.MIN_VALUE);
    private volatile BigDecimal min = BigDecimal.valueOf(Double.MAX_VALUE);
    private volatile long count = 0;  // Number of transactions within the time window
    private volatile long lastTransactionTime;  // Stores the timestamp of the last transaction processed

    // Holds statistics for the current time window (reset when new transactions occur after a new second)
    private TransactionStatistics currentStatistics = new TransactionStatistics();

    /**
     * Adds a transaction to the statistics if it falls within the 30-second time window.
     *
     * @param amount    The transaction amount.
     * @param timestamp The timestamp when the transaction occurred.
     */
    public boolean addTransaction(BigDecimal amount, Instant timestamp) {
        long now = Instant.now().getEpochSecond();
        long transactionTime = timestamp.getEpochSecond();

        // Ignore transactions older than the defined time window (30 seconds)
        if (now - transactionTime >= TIME_WINDOW) {
            return false;
        }

        // Lock the critical section to ensure thread safety during updates
        lock.lock();
        try {
            // If a new second's transaction comes in, reset statistics for this new second
            if (lastTransactionTime != transactionTime) {
                resetStatistics(transactionTime);
            }
            // Update statistics for the current second
            currentStatistics.addTransaction(amount);
            updateGlobalStatistics(amount);  // Update global min, max, sum, count
        } finally {
            lock.unlock();  // Always unlock to avoid potential deadlocks
        }
        return true;
    }

    /**
     * Resets the statistics for a new second, clearing out old data and initializing the time.
     *
     * @param transactionTime The timestamp of the new transaction (in seconds).
     */
    private void resetStatistics(long transactionTime) {
        // Reset global statistics when the time window shifts to a new second
        sum = BigDecimal.ZERO;
        count = 0;
        min = BigDecimal.valueOf(Double.MAX_VALUE);
        max = BigDecimal.valueOf(Double.MIN_VALUE);
        lastTransactionTime = transactionTime;

        // Create a new instance of TransactionStatistics for the new second
        currentStatistics = new TransactionStatistics(transactionTime, BigDecimal.ZERO, min, max, 0);
    }

    /**
     * Updates the global statistics (sum, min, max, and count) using the provided transaction amount.
     *
     * @param amount The amount of the transaction to be added to the statistics.
     */
    private void updateGlobalStatistics(BigDecimal amount) {
        sum = sum.add(amount);  // Increment the sum by the new transaction amount
        count++;  // Increment the count of transactions
        min = min.min(amount);  // Update the minimum transaction if applicable
        max = max.max(amount);  // Update the maximum transaction if applicable
    }

    /**
     * Returns the current statistics based on transactions within the last 30 seconds.
     * If no transactions exist within this window, returns zeroed statistics.
     *
     * @return A StatisticsResponse object containing the sum, average, max, min, and count.
     */
    public StatisticsResponse getStatistics() {
        lock.lock();
        StatisticsResponse statisticsResponse;
        try {
            long now = Instant.now().getEpochSecond();
            // If no transactions in the last 30 seconds, return zeroed statistics
            if (now - lastTransactionTime >= TIME_WINDOW || count == 0) {
                statisticsResponse = new StatisticsResponse(
                        BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                        BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                        BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                        BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                        0);
            } else {
                // Calculate average (sum / count) and return the statistics response
                BigDecimal avg = sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
                statisticsResponse = new StatisticsResponse(
                        sum.setScale(2, RoundingMode.HALF_UP),
                        avg,
                        max.setScale(2, RoundingMode.HALF_UP),
                        min.setScale(2, RoundingMode.HALF_UP),
                        count);
            }
        } finally {
            lock.unlock();  // Ensure the lock is released after operation
        }
        return statisticsResponse;
    }
}
