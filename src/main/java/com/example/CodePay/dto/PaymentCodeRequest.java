package com.example.CodePay.dto;

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
    private double target_lat;
    private double target_lng;
    private Long radius_meters;
}
