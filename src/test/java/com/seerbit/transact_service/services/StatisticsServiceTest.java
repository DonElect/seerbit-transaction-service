package com.seerbit.transact_service.services;

import com.seerbit.transact_service.dto.StatisticsResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {
    @InjectMocks
    private StatisticsService statisticsService;

    @Test
    void addTransaction_success_test() {
        BigDecimal amount = BigDecimal.valueOf(4555.67);
        Instant timestamp = Instant.now().minusSeconds(5);
        boolean isAdded = statisticsService.addTransaction(amount, timestamp);
        assertTrue(isAdded);
    }

    @Test
    void addTransaction_failure_test_time_greater_than_30() {
        BigDecimal amount = BigDecimal.valueOf(4555.67);
        Instant timestamp = Instant.now().minusSeconds(35);
        boolean isAdded = statisticsService.addTransaction(amount, timestamp);
        assertFalse(isAdded);
    }

    @Test
    void getStatistics_success() {
        statisticsService.addTransaction(BigDecimal.valueOf(4555.67), Instant.now().minusSeconds(5));
        statisticsService.addTransaction(BigDecimal.valueOf(50000), Instant.now().minusSeconds(5));
        statisticsService.addTransaction(BigDecimal.valueOf(6798.9899), Instant.now().minusSeconds(5));

        StatisticsResponse statistics = statisticsService.getStatistics();
        assertEquals(3, statistics.count());
    }

    @Test
    void getStatistics_zeros() {
        statisticsService.addTransaction(BigDecimal.valueOf(4555.67), Instant.now().minusSeconds(35));
        statisticsService.addTransaction(BigDecimal.valueOf(50000), Instant.now().minusSeconds(45));
        statisticsService.addTransaction(BigDecimal.valueOf(6798.9899), Instant.now().minusSeconds(50));

        StatisticsResponse statistics = statisticsService.getStatistics();
        assertEquals(0, statistics.count());
    }
}