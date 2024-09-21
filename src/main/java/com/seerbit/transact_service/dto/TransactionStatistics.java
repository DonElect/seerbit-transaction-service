package com.seerbit.transact_service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class TransactionStatistics {
    private BigDecimal sum;
    private BigDecimal avg;
    private BigDecimal max;
    private BigDecimal min;
    private long count;

    public TransactionStatistics(BigDecimal sum, BigDecimal avg, BigDecimal max, BigDecimal min, long count) {
        this.sum = sum.setScale(2, RoundingMode.HALF_EVEN);
        this.avg = avg.setScale(2, RoundingMode.HALF_EVEN);
        this.max = max.setScale(2, RoundingMode.HALF_EVEN);
        this.min = min.setScale(2, RoundingMode.HALF_EVEN);
        this.count = count;
    }
}
