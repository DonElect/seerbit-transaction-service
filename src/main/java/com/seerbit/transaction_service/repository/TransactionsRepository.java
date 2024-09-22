package com.seerbit.transaction_service.repository;

import com.seerbit.transaction_service.model.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Long> {
    @Query("""
            SELECT \
            COALESCE(SUM(t.amount), 0) as sum, \
            COALESCE(AVG(t.amount), 0) as avg, \
            COALESCE(MAX(t.amount), 0) as max, \
            COALESCE(MIN(t.amount), 0) as min, \
            COUNT(t) as count \
            FROM Transactions t \
            WHERE t.timestamp >= NOW() - 30 SECOND""")
    Map<String, Object> getLast30SecondsStatistics();
}
