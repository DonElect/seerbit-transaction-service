package com.seerbit.transaction_service.services.impl;

import com.seerbit.transaction_service.dto.StatisticsResponse;
import com.seerbit.transaction_service.dto.TransactionRequest;
import com.seerbit.transaction_service.exceptions.OldTransactionException;
import com.seerbit.transaction_service.exceptions.UnprocessableEntityException;
import com.seerbit.transaction_service.model.Transactions;
import com.seerbit.transaction_service.repository.TransactionsRepository;
import com.seerbit.transaction_service.services.StatisticsService;
import com.seerbit.transaction_service.services.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionsRepository transactionsRepository;  // Handles database operations for transactions
    private final StatisticsService statisticsService;  // Manages transaction statistics within a 30-second window

    /**
     * Creates a new transaction, validates its timestamp, and updates the statistics.
     * @param transactionsRequest The transaction request data from the client.
     * @throws OldTransactionException If the transaction is older than 30 seconds.
     * @throws UnprocessableEntityException If the transaction timestamp is in the future.
     */
    @Override
    public void create(TransactionRequest transactionsRequest) {
        // Check if the transaction is older than 30 seconds
        if (transactionsRequest.getTimestamp().isBefore(Instant.now().minusSeconds(30)))
            throw new OldTransactionException("Transaction is older than 30 seconds.");

        // Ensure the transaction timestamp is not in the future
        if (transactionsRequest.getTimestamp().isAfter(Instant.now()))
            throw new UnprocessableEntityException("Transaction is in the future.");

        // Convert the amount from String to BigDecimal
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(transactionsRequest.getAmount()));

        // Add the transaction to the statistics (valid within 30-second window)
        statisticsService.addTransaction(amount, transactionsRequest.getTimestamp());

        try {
            // Create and save the transaction to the database
            Transactions transactions = Transactions.builder()
                    .amount(amount)
                    .timestamp(transactionsRequest.getTimestamp())
                    .build();
            transactionsRepository.save(transactions);
        } catch (Exception ex) {
            // Log an error if the transaction fails to save
            log.error("Failed to save transaction to the database: {}", ex.getMessage());
        }
    }

    /**
     * Retrieves statistics about transactions from the last 30 seconds.
     * @return A StatisticsResponse containing transaction statistics (sum, avg, min, max, count).
     */
    @Override
    public StatisticsResponse getTransactionsStatistics() {
        return statisticsService.getStatistics();
    }

    /**
     * Deletes all transactions from the database.
     */
    @Override
    public void deleteAllTransactions() {
        transactionsRepository.deleteAll();
    }
}
