package com.example.CodePay.Authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
@Builder
@AllArgsConstructor
@Data
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;

}
