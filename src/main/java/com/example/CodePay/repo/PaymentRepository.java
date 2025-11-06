package com.example.CodePay.repo;

import com.example.CodePay.entity.Transaction;
import com.example.CodePay.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Transaction, Long> {


    Optional<Transaction> findByReference(String reference);

    List<Transaction> findByWallet(Wallet wallet);
}
