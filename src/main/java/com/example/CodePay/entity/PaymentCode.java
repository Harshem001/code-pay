package com.example.CodePay.entity;

import com.example.CodePay.enums.PaymentCodeEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "payment_code", schema = "codepay_api")
public class PaymentCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiverId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'PENDING'")
    @Column(name = "status")
    private PaymentCodeEnum status;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "expire_at")
    private Instant expireAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "redeemed_at")
    private Instant redeemedAt;

    @Column(name = "target_lat")
    private double target_lat;

    @Column(name = "target_lon")
    private double target_lng;

    @Column(name = "radius_meters")
    private Long radius_meters;

}