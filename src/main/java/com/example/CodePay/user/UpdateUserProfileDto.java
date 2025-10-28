package com.example.CodePay.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserProfileDto {

    @NotBlank
    private String fullName;
    private String address;
    private LocalDate dateOfBirth;
    private String phoneNumber;

}
