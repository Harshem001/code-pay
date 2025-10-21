package com.example.CodePay.code_payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentCodeRepository extends JpaRepository<PaymentCode, Long> {
    boolean existsByCode(String paymentCode);

    Optional<PaymentCode> findByCode(String code);
}