package com.seerbit.transact_service.services;

import com.seerbit.transact_service.dto.TransactionRequest;
import com.seerbit.transact_service.dto.TransactionStatistics;
import com.seerbit.transact_service.model.Transactions;


public interface TransactionService {
    void create(TransactionRequest transactions);
    TransactionStatistics getTransactionsStatistics();
    void deleteAllTransactions();
}
