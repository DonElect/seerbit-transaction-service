package com.seerbit.transact_service.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@ToString
public class TransactionStatistics {
    private long timestamp;
    private BigDecimal sum;
    private BigDecimal max;
    private BigDecimal min;
    private long count;

    public TransactionStatistics() {
        this.timestamp = 0;
        this.sum = BigDecimal.ZERO;
        this.max = BigDecimal.valueOf(Double.MIN_VALUE);
        this.min = BigDecimal.valueOf(Double.MAX_VALUE);
        this.count = 0;
    }

    public TransactionStatistics(long timestamp, BigDecimal sum, BigDecimal max, BigDecimal min, long count) {
        this.timestamp = timestamp;
        this.sum = sum;
        this.max = max;
        this.min = min;
        this.count = count;
    }

    public void addTransaction(BigDecimal amount) {
        this.sum = this.sum.add(amount);
        this.max = this.max.max(amount);
        this.min = this.min.min(amount);
        this.count++;
    }
}
