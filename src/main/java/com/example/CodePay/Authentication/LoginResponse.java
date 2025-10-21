package com.example.CodePay.Authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;

}
