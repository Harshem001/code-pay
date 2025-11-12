package com.example.CodePay.controller;

import com.example.CodePay.service.AuthService;
import com.example.CodePay.dto.LoginRequest;
import com.example.CodePay.dto.LoginResponse;
import com.example.CodePay.security.JwtService;
import com.example.CodePay.dto.GeneralResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Login", description = "Endpoint to Login")
public class AuthController {

    private final JwtService jwtService;
    private final AuthService authService;


    @PostMapping("/login")
    @Operation(
            summary = "Login user",
            description = "Login user with correct email and password",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login Successful"),
                    @ApiResponse(responseCode = "400", description = "Could not Log user In due to incorrect credential")
            }
    )
    public ResponseEntity<GeneralResponseDto<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);

        GeneralResponseDto<LoginResponse> generalResponseDto = GeneralResponseDto.<LoginResponse>builder()
                .status("200")
                .message("You've Successfully Logged in")
                .data(response)
                .build();

        return ResponseEntity.ok(generalResponseDto);
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Generate a new access and refresh token",
            description = "New access and refresh token to be generated are needed when the initial token expires"
    )
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody Map<String ,String> request) {
        String refreshToken = request.get("refreshToken");
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        return ResponseEntity.ok().body("message: Logged out");
    }
}
