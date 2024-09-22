package com.seerbit.transact_service.services.impl;

import com.seerbit.transact_service.dto.StatisticsResponse;
import com.seerbit.transact_service.dto.TransactionRequest;
import com.seerbit.transact_service.exceptions.OldTransactionException;
import com.seerbit.transact_service.exceptions.UnprocessableEntityException;
import com.seerbit.transact_service.repository.TransactionsRepository;
import com.seerbit.transact_service.services.StatisticsService;
import com.seerbit.transact_service.services.TransactionService;
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
    private final TransactionsRepository transactionsRepository;
    private final StatisticsService statisticsService;

    @Override
    public void create(TransactionRequest transactionsRequest) {
        if (transactionsRequest.getTimestamp().isBefore(Instant.now().minusSeconds(30)))
            throw new OldTransactionException("Transaction is older than 30 seconds.");
        if (transactionsRequest.getTimestamp().isAfter(Instant.now()))
            throw new UnprocessableEntityException("Transaction is in the future.");

        statisticsService.addTransaction(BigDecimal.valueOf(Double.parseDouble(transactionsRequest.getAmount())), transactionsRequest.getTimestamp());
    }

    @Override
    public StatisticsResponse getTransactionsStatistics() {
       return statisticsService.getStatistics();
    }

    @Override
    public void deleteAllTransactions() {
        transactionsRepository.deleteAll();
    }
}
