package com.seerbit.transaction_service.controllers;

import com.seerbit.transaction_service.dto.StatisticsResponse;
import com.seerbit.transaction_service.dto.TransactionRequest;
import com.seerbit.transaction_service.model.ApiResponse;
import com.seerbit.transaction_service.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction-service")  // Base URL for all endpoints in this controller
@RequiredArgsConstructor  // Automatically generates constructor for final fields
public class TransactionController {

    private final TransactionService transactionService;  // Handles core transaction-related operations

    /**
     * POST /transactions
     * Endpoint to create a new transaction.
     * It accepts a transaction request in JSON format and processes it.
     * @param transactionRequest The transaction details sent by the client.
     * @return ResponseEntity with HTTP status CREATED (201) on success.
     */
    @PostMapping("/transactions")
    public ResponseEntity<ApiResponse> createTransaction(@Valid @RequestBody TransactionRequest transactionRequest) {
        // The @Valid annotation ensures that the request body is validated before processing.
        transactionService.create(transactionRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);  // Returns 201 status for successful creation.
    }

    /**
     * GET /statistics
     * Endpoint to retrieve transaction statistics from the last 30 seconds.
     * @return ResponseEntity containing the statistics and HTTP status OK (200).
     */
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> getTransactionStatistics() {
        StatisticsResponse statistics = transactionService.getTransactionsStatistics();
        return new ResponseEntity<>(statistics, HttpStatus.OK);  // Return statistics with 200 status.
    }

    /**
     * DELETE /transactions
     * Endpoint to delete all transactions from the system.
     * @return ResponseEntity with HTTP status NO_CONTENT (204) on success.
     */
    @DeleteMapping("/transactions")
    public ResponseEntity<ApiResponse> deleteAllTransactions() {
        transactionService.deleteAllTransactions();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // Returns 204 status when deletion is successful.
    }
}
