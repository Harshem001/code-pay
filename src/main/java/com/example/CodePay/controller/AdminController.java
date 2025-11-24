package com.example.CodePay.controller;

import com.example.CodePay.dto.*;
import com.example.CodePay.repo.UserRepository;
import com.example.CodePay.security.UserPrincipal;
import com.example.CodePay.service.AdminService;
import com.example.CodePay.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    @Operation(
            summary = "Get All Users",
            description = "Retrieve all users registered on CodePay"
    )
    public ResponseEntity<GeneralResponseDto<List<RegisterUserResponse>>> getAllUsers() {

        GeneralResponseDto<List<RegisterUserResponse>> response = adminService.getListOfUsers();

        return ResponseEntity.ok(response);
    }
    @GetMapping("/userById")
    public ResponseEntity<GeneralResponseDto<RegisterUserResponse>> getUserId (Long userId) {
        GeneralResponseDto<RegisterUserResponse> response = adminService.getUserById(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<GeneralResponseDto<String>> deleteUser (
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable DeleteUserRequest email) {
        return adminService.deleteUserByEmail(userPrincipal, email);
    }

    @GetMapping("/getTransaction/{transactionId}")
    public ResponseEntity<GeneralResponseDto<TransactionDto>> getTransactionById(@PathVariable Long transactionId) {
        GeneralResponseDto<TransactionDto> response = adminService.getTransactionById(transactionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/allTransactions")
    public ResponseEntity<GeneralResponseDto<Map<String, Object>>> getListOfTransactions(
            @RequestParam (defaultValue = "0" ) int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GeneralResponseDto<Map<String, Object>> response = adminService.getListOfTransactions(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/accountDetails/{Id}")
    public ResponseEntity<GeneralResponseDto<AccountDetailsDto>> accountDetails(
            @PathVariable Long Id) {
        GeneralResponseDto<AccountDetailsDto> response = adminService.getAccountDetails(Id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/changeUserStaus/{Id}")
    public ResponseEntity<GeneralResponseDto<String>> changeUserStatus (@PathVariable Long Id) {
        GeneralResponseDto<String> response = adminService.changeUserStatus(Id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashBoard")
    public ResponseEntity<GeneralResponseDto<DashBoardStatsDto>> getDashBoardStats() {
        GeneralResponseDto<DashBoardStatsDto> response = adminService.getDashBoardStats();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/financeReport")
    public ResponseEntity<GeneralResponseDto<FinancialReportDto>> getFinancialReport() {
        GeneralResponseDto<FinancialReportDto> response = adminService.getFinancialReport();
        return ResponseEntity.ok(response);
    }
}
