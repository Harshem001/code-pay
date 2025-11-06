package com.example.CodePay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RedeemCodeResponse {
    private BigDecimal amount;
    private String senderName;
    private Instant createdAt;
    private Instant expireAt;
}
