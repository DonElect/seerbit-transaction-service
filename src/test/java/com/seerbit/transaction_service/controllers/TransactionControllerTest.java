package com.seerbit.transaction_service.controllers;

import com.seerbit.transaction_service.dto.StatisticsResponse;
import com.seerbit.transaction_service.dto.TransactionRequest;
import com.seerbit.transaction_service.services.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransactionController.class)
@ExtendWith(MockitoExtension.class)
@Slf4j
class TransactionControllerTest {
    @Autowired
    private TransactionController transactionController;

    @MockBean
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createTransaction() throws Exception {
        doNothing().when(transactionService).create(mock(TransactionRequest.class));

        String content = """
                  {
                    "amount": "6000",
                    "timestamp": "2024-09-22T15:51:51.312Z"
                  }
                """;
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/transaction-service/transactions")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON);

        MockMvcBuilders.standaloneSetup(transactionController)
                .build()
                .perform(requestBuilder)
                .andExpect(status().isCreated());
    }

    @Test
    void getTransactionStatistics() throws Exception {
        StatisticsResponse statisticsResponse = new StatisticsResponse(
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                0);
        when(transactionService.getTransactionsStatistics()).thenReturn(statisticsResponse);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/transaction-service/statistics")
                .contentType(MediaType.APPLICATION_JSON);

        MockMvcBuilders.standaloneSetup(transactionController)
                .build()
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("{\"sum\":0.00,\"avg\":0.00,\"max\":0.00,\"min\":0.00,\"count\":0}"));
    }

    @Test
    void deleteAllTransactions() throws Exception {
        doNothing().when(transactionService).deleteAllTransactions();

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/transaction-service/transactions")
                .contentType(MediaType.APPLICATION_JSON);

        MockMvcBuilders.standaloneSetup(transactionController)
                .build()
                .perform(requestBuilder)
                .andExpect(status().isNoContent());
    }
}