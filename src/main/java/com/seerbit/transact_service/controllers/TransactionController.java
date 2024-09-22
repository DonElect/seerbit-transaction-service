package com.seerbit.transact_service.controllers;

import com.seerbit.transact_service.dto.StatisticsResponse;
import com.seerbit.transact_service.dto.TransactionRequest;
import com.seerbit.transact_service.dto.TransactionStatistics;
import com.seerbit.transact_service.model.ApiResponse;
import com.seerbit.transact_service.services.StatisticsService;
import com.seerbit.transact_service.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction-service")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final StatisticsService statisticsService;

    @PostMapping("/transactions")
    public ResponseEntity<ApiResponse> createTransaction(@Valid @RequestBody TransactionRequest transactionRequest){
        transactionService.create(transactionRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> getTransactionStatistics(){
        StatisticsResponse statistics = transactionService.getTransactionsStatistics();
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteAllTransactions(){
        transactionService.deleteAllTransactions();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
