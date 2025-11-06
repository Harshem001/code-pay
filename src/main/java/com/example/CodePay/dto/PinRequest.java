package com.example.CodePay.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PinRequest {
    @NotNull(message = "pin is required")
    @Size(min = 4, max = 6, message = "password should either be 4 digits or 6 digits")
    private String pin;
}
