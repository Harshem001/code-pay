package com.example.CodePay.code_payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RedeemCodeRequest {
    private String code;
    private double redeemerLat;
    private double redeemerLon;
}
