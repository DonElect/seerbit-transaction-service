package com.seerbit.transact_service.repository;

import com.seerbit.transact_service.dto.TransactionStatistics;
import com.seerbit.transact_service.model.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
