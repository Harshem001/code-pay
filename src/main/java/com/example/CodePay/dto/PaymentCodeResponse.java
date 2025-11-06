package com.example.CodePay.dto;

import com.example.CodePay.enums.PaymentCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

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
