package com.example.CodePay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PinRequest {
    @NotBlank(message = "pin cannot be empty")
    @Size(min = 4, max = 4, message = "password should be 4 digits")
    private String pin;
}
