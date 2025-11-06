package com.example.CodePay.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

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
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{6,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one special character"
    )
    private String password;

    @NotBlank(message = "Address is required")
    private  String address;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Phone number is required")
    @Size(min = 11, max = 11, message = "Phone Number has to be 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Gender is required, Male or Female")
    private String gender;

    @NotBlank(message = "bvn is required")
    @Size(min = 11, max = 11, message = "Must be up to 11 digits")
    private String bvn;


}
