package com.seerbit.transact_service.dto;

import java.math.BigDecimal;

public record StatisticsResponse(BigDecimal sum, BigDecimal avg, BigDecimal max, BigDecimal min, long count) {
}

