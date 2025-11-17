package com.example.CodePay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DashBoardStatsDto {
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpenses;
    private BigDecimal totalSystemBalance;
    private Long totalUser;
}
