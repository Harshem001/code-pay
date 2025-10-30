package com.example.CodePay.payment;

import com.example.CodePay.user.User;
import com.example.CodePay.wallet.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Transaction, Long> {


    Optional<Transaction> findByReference(String reference);

    List<Transaction> findByWallet(Wallet wallet);
}
