package com.seerbit.transact_service.services;

import com.seerbit.transact_service.dto.StatisticsResponse;
import com.seerbit.transact_service.dto.TransactionRequest;


public interface TransactionService {
    void create(TransactionRequest transactions);
    StatisticsResponse getTransactionsStatistics();
    void deleteAllTransactions();
}
