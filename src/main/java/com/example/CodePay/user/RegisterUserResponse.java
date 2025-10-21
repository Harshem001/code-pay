package com.example.CodePay.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterUserResponse {
    private  Long id;
    private String fullName;
    private String email;
    private String walletNumber;

}
