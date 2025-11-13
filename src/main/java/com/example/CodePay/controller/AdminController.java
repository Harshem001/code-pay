package com.example.CodePay.controller;

import com.example.CodePay.dto.GeneralResponseDto;
import com.example.CodePay.dto.RegisterUserResponse;
import com.example.CodePay.repo.UserRepository;
import com.example.CodePay.service.AdminService;
import com.example.CodePay.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    private final UserRepository userRepository;
    private final UserService userService;
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

    @GetMapping("/users/count")
    public ResponseEntity<GeneralResponseDto<Long>> getTotalUsers() {
        GeneralResponseDto<Long> response = adminService.getTotalUsers();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/codepay/totalAmount")
    public ResponseEntity<GeneralResponseDto<BigDecimal>> getTotalSystemBalance() {
        GeneralResponseDto<BigDecimal> response = adminService.getTotalSystemBalance();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/codepay/monthlyExpenses")
    public ResponseEntity<GeneralResponseDto<BigDecimal>> getMonthlyExpenses(){
        GeneralResponseDto<BigDecimal> response = adminService.getMonthlyExpenses();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/codepay/monthlyIncome")
    public ResponseEntity<GeneralResponseDto<BigDecimal>> getMonthlyIncome(){
        GeneralResponseDto<BigDecimal> response = adminService.getMonthlyIncome();
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
}
