package com.example.CodePay.repo;

import com.example.CodePay.entity.Transaction;
import com.example.CodePay.entity.Wallet;
import com.example.CodePay.enums.Status;
import com.example.CodePay.enums.TransactionEntry;
import com.example.CodePay.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByReference(String reference);

    List<Transaction> findByWallet(Wallet wallet);

    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.transactionType IN :types " +
            "AND t.transactionEntry = :entry " +
            "AND t.date BETWEEN :startDate AND :stopDate")
    BigDecimal calculateMonthlyExpenses(@Param("types") List<String> types,
                                        @Param("entry") TransactionEntry entry,
                                        @Param("startDate") Instant startDate,
                                        @Param("stopDate") Instant stopDate);


    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.transactionType = :type " +
            "AND t.transactionEntry = :entry " +
            "AND t.date BETWEEN :startDate AND :stopDate")
    BigDecimal calculateMonthlyIncome(@Param("type")TransactionType type,
                                      @Param("entry") TransactionEntry entry,
                                      @Param("startDate") Instant startDate,
                                      @Param("stopDate") Instant stopDate);

    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.transactionType = :type " +
            "AND t.transactionEntry = :entry ")
    BigDecimal calculateTotalIncome(@Param("type")TransactionType type,
                                    @Param("entry") TransactionEntry entry);

    Page<Transaction> findAll(Pageable pageable);

    Long countByStatus(Status  status);
}
