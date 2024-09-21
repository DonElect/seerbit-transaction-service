package com.seerbit.transact_service.services.impl;

import com.seerbit.transact_service.dto.TransactionRequest;
import com.seerbit.transact_service.dto.TransactionStatistics;
import com.seerbit.transact_service.exceptions.OldTransactionException;
import com.seerbit.transact_service.exceptions.UnprocessableEntityException;
import com.seerbit.transact_service.model.Transactions;
import com.seerbit.transact_service.repository.TransactionsRepository;
import com.seerbit.transact_service.services.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionsRepository transactionsRepository;


    @Override
    public void create(TransactionRequest transactionsRequest) {
        if (transactionsRequest.getTimestamp().isBefore(LocalDateTime.now().minusSeconds(30)))
            throw new OldTransactionException("Transaction is older than 30 seconds.");
        if (transactionsRequest.getTimestamp().isAfter(LocalDateTime.now()))
            throw new UnprocessableEntityException("Transaction is in the future.");

        Transactions transactions = Transactions.builder()
                .amount(BigDecimal.valueOf(Double.parseDouble(transactionsRequest.getAmount())))
                .timestamp(transactionsRequest.getTimestamp())
                .build();
        transactionsRepository.save(transactions);
    }

    @Override
    public TransactionStatistics getTransactionsStatistics() {
        Map<String, Object> result = transactionsRepository.getLast30SecondsStatistics();
        log.info("Statistics: {}", result.entrySet());
        if (result.isEmpty())
            return new TransactionStatistics(
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(Double.MIN_VALUE), BigDecimal.valueOf(Double.MAX_VALUE), 0
            );
        BigDecimal sum = (BigDecimal) result.get("sum") ;
        BigDecimal avg = BigDecimal.valueOf((Double) result.get("avg"));
        BigDecimal min = (BigDecimal) result.get("min");
        BigDecimal max = (BigDecimal) result.get("max");
        long count = (long) result.get("count");
        return new TransactionStatistics(sum, avg, max, min, count);
    }

    @Override
    public void deleteAllTransactions() {
        transactionsRepository.deleteAll();
    }
}
