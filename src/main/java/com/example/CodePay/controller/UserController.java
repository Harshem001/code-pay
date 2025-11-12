package com.example.CodePay.controller;

import com.example.CodePay.dto.*;
import com.example.CodePay.security.UserPrincipal;
import com.example.CodePay.repo.UserRepository;
import com.example.CodePay.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Registering User")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;


    @PostMapping("/registration")
    @Operation(
            summary = "Register User"
    )
    public ResponseEntity<GeneralResponseDto<?>> registerUser(
            @Valid @RequestBody RegisterUserRequest request) {

        var user = userService.registerUser(request);
        var response = GeneralResponseDto.builder()
                .status("200")
                .message("You've successfully registered on CodePay")
                .data(user)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/updateMyProfile")
    @Operation(
            summary = "Update user profile ",
            description = "Endpoint for updating a logged in user"
    )
    public ResponseEntity<GeneralResponseDto<String>> updateUser (
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody UpdateUserProfileDto updateUserProfileDto) {
        return userService.updateProfile(userPrincipal, updateUserProfileDto);
    }

    @GetMapping("/myProfile")
    @Operation(
            summary = "View my profile"
    )
    public ResponseEntity<GeneralResponseDto<RegisterUserResponse>> getUserProfile (
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return userService.getProfile(userPrincipal);
    }


    @DeleteMapping("/delete")
    public ResponseEntity<GeneralResponseDto<String>> deleteUser (
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody DeleteUserRequest request) {
        return userService.deleteUserByEmail(userPrincipal, request);
    }

}
