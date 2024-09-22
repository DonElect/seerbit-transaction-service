package com.seerbit.transaction_service.services;

import com.seerbit.transaction_service.dto.StatisticsResponse;
import com.seerbit.transaction_service.dto.TransactionRequest;


public interface TransactionService {
    void create(TransactionRequest transactions);
    StatisticsResponse getTransactionsStatistics();
    void deleteAllTransactions();
}
