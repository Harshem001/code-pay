package com.example.CodePay.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {

    @NotBlank(message = "Name is required.")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String fullName;

    @NotBlank(message = "E-mail is required")
    @Email(message = "Email must be valid.")
    private String email;

    @NotBlank(message = "Password required")
    @Size(min = 6, max = 20, message = "Password must be between 6 to 25 character")
    private String password;

    @NotNull(message = "pin is required")
    @Size(min = 4, max = 6, message = "password should either be 4 digits or 6 digits")
    private String pin;
}
