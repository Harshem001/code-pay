package com.example.CodePay.payment;

import com.example.CodePay.wallet.Wallet;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @Column(name = "reference")
    private String reference;

    @Column(name = "amount")
    private BigDecimal amount;

    @ColumnDefault("'PENDING'")
    @Column(name = "status")
    private String status;

    @ColumnDefault("'CREDITED'")
    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "date", nullable = false)
    private Instant date;

}