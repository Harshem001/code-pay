package com.example.CodePay.repo;

import com.example.CodePay.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    boolean existsByWalletNumber(String walletNumber);

    Wallet getWalletById(Long id);

    Optional<Wallet> findByWalletNumber(String receiverWalletNumber);

//    @Query("SELECT COALESCE(SUM (w.balance), 0) FROM Wallet w ")
//    BigDecimal getTotalAmount();
}
