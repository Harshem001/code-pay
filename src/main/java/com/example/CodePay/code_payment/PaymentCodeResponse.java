package com.example.CodePay.code_payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentCodeResponse {
    private String code;
    private BigDecimal amount;
    private String senderName;
    private PaymentCodeEnum status;
    private Instant createdAt;
    private Instant expireAt;

}
