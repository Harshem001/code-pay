package com.example.CodePay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FinancialReportDto {
    private Long totalTransaction;
    private Long successfulTransaction;
    private Long failedTransaction;
    private Long pendingTransaction;
    private BigDecimal totalIncome;
    private BigDecimal totalBalance;
    private Long activeAccount;
    private Long inactiveAccount;
}
