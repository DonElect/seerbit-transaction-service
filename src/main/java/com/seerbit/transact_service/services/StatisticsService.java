package com.seerbit.transact_service.services;

import com.seerbit.transact_service.dto.StatisticsResponse;
import com.seerbit.transact_service.dto.TransactionStatistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class StatisticsService {

    private static final int TIME_WINDOW = 30;
    private final ReentrantLock lock = new ReentrantLock();

    private static final TransactionStatistics[] statistics = new TransactionStatistics[TIME_WINDOW];
    private static final Map<String, TransactionStatistics> statisticsMap = new ConcurrentHashMap<>(2);
    private volatile BigDecimal sum = BigDecimal.ZERO;
    private volatile BigDecimal max = BigDecimal.valueOf(Double.MIN_VALUE);
    private volatile BigDecimal min = BigDecimal.valueOf(Double.MAX_VALUE);
    private volatile long count = 0;
    private volatile long lastTransactionTime;
    private static final String KEY = "stats";

    public StatisticsService() {
        // Initialize array with empty statistics
        for (int i = 0; i < TIME_WINDOW; i++) {
            statistics[i] = new TransactionStatistics();
        }
    }

    public void addTransaction(BigDecimal amount, Instant timestamp) {
        long now = Instant.now().getEpochSecond();
        long transactionTime = timestamp.getEpochSecond();

        if (now - transactionTime >= TIME_WINDOW) {
            return; // Transaction is older than 30 seconds, ignore it
        }

        lastTransactionTime = transactionTime;

        int index = (int) (transactionTime % TIME_WINDOW);
        lock.lock();
        try {
            // Clear old data at this index if it's from an old time
            if (statistics[index].getTimestamp() != transactionTime) {
                removeOldStatistics(index);
                statistics[index] = new TransactionStatistics(transactionTime, amount, amount, amount, 1);

            } else {
                // Update existing statistics for this second
                statistics[index].addTransaction(amount);
            }
            // Update global statistics
            updateGlobalStatistics();
        } finally {
            lock.unlock();
        }
    }

    private void removeOldStatistics(int index) {
        sum = sum.subtract(statistics[index].getSum());
        count -= statistics[index].getCount();
        if (count == 0) {
            min = BigDecimal.valueOf(Double.MAX_VALUE);
            max = BigDecimal.valueOf(Double.MIN_VALUE);
        } else {
            min = BigDecimal.valueOf(Double.MAX_VALUE);
            max = BigDecimal.valueOf(Double.MIN_VALUE);
            // Recompute min and max
            for (TransactionStatistics stat : statistics) {
                if (stat.getCount() > 0) {
                    min = min.min(stat.getMin());
                    max = max.max(stat.getMax());
                }
            }
        }
    }

    private void updateGlobalStatistics() {
        sum = BigDecimal.ZERO;
        count = 0;
        min = BigDecimal.valueOf(Double.MAX_VALUE);
        max = BigDecimal.valueOf(Double.MIN_VALUE);
        for (TransactionStatistics stat : statistics) {
            if (stat.getCount() > 0) {
                sum = sum.add(stat.getSum());
                count += stat.getCount();
                min = min.min(stat.getMin());
                max = max.max(stat.getMax());
            }
        }
    }

    public StatisticsResponse getStatistics() {
        lock.lock();
        try {
            long now = Instant.now().getEpochSecond();
            if (now - lastTransactionTime >= TIME_WINDOW || count == 0) {
                return new StatisticsResponse(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                        BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                        BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                        BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                        0);
            }
            BigDecimal avg = count == 0 ? BigDecimal.ZERO : sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
            return new StatisticsResponse(sum.setScale(2, RoundingMode.HALF_UP),
                    avg,
                    max.setScale(2, RoundingMode.HALF_UP),
                    min.setScale(2, RoundingMode.HALF_UP),
                    count);
        } finally {
            lock.unlock();
        }
    }
}

