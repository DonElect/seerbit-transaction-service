package com.seerbit.transaction_service.services.impl;

import com.seerbit.transaction_service.dto.TransactionRequest;
import com.seerbit.transaction_service.exceptions.OldTransactionException;
import com.seerbit.transaction_service.exceptions.UnprocessableEntityException;
import com.seerbit.transaction_service.repository.TransactionsRepository;
import com.seerbit.transaction_service.services.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {
    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private TransactionsRepository transactionsRepository;
    @Mock
    private StatisticsService statisticsService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void create_success() {
        TransactionRequest transactionRequest = TransactionRequest.builder()
                .amount("550.98")
                .timestamp(Instant.now().minusSeconds(10))
                .build();
        assertAll(()-> transactionService.create(transactionRequest));
    }

    @Test
    void create_oldTransactionException() {
        TransactionRequest transactionRequest = TransactionRequest.builder()
                .amount("550.98")
                .timestamp(Instant.now().minusSeconds(40))
                .build();
        assertThrows(OldTransactionException.class, ()-> transactionService.create(transactionRequest));
    }

    @Test
    void create_unprocessableEntityException() {
        TransactionRequest transactionRequest = TransactionRequest.builder()
                .amount("550.98")
                .timestamp(Instant.now().plusSeconds(40))
                .build();
        assertThrows(UnprocessableEntityException.class, ()-> transactionService.create(transactionRequest));
    }

    @Test
    void getTransactionsStatistics() {
        assertAll(()-> transactionService.getTransactionsStatistics());
    }

    @Test
    void deleteAllTransactions() {
        assertAll(()-> transactionService.deleteAllTransactions());
    }
}