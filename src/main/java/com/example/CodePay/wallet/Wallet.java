package com.example.CodePay.wallet;

import com.example.CodePay.payment.Transaction;
import com.example.CodePay.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "wallet")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id")
    private User user;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "wallet_number")
    private String walletNumber;

    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "wallet")
    private List<Transaction> transactions;

}