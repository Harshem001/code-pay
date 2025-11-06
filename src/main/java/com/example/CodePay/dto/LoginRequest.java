package com.example.CodePay.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email required")
    @Email
    private String email;

    @NotBlank(message = "Password required")
    private String password;
}
