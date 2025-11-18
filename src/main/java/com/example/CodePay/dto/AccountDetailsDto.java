package com.example.CodePay.dto;

import com.example.CodePay.entity.Transaction;
import com.example.CodePay.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsDto {
    private String accountName;
    private String phoneNumber;
    private String accountEmail;
    private UserStatus userStatus;
    private BigDecimal balance;
    List<TransactionHistoryDTO>  transactionHistory;
}
