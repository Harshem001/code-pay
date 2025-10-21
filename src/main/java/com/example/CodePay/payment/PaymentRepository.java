package com.example.CodePay.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Transaction, Long> {


    Optional<Transaction> findByReference(String reference);
}
