package com.example.CodePay.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterUserResponse {
    private  Long id;
    private String fullName;
    private String email;
    private String walletNumber;


}
