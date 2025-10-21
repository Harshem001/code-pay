package com.example.CodePay.code_payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentCodeRequest {
    private BigDecimal amount;
    private String pin;
}
