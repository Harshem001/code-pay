package com.example.CodePay.dto;

import com.example.CodePay.enums.Status;
import com.example.CodePay.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterUserResponse {
    private  Long id;
    private String fullName;
    private String email;
    private String walletNumber;
    private BigDecimal balance;
    private String phoneNumber;
    private UserStatus userStatus;
}
