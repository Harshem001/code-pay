package com.example.CodePay.controller;

import com.example.CodePay.security.UserPrincipal;
import com.example.CodePay.dto.DepositRequest;
import com.example.CodePay.dto.DepositResponse;
import com.example.CodePay.dto.GeneralResponseDto;
import com.example.CodePay.service.DepositService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/payments")
@Tag(name = "Deposit Money", description = "Deposit Money Via PayStack")
public class PaymentController {

    private final DepositService depositService;

   @PostMapping("/deposit")
   @Operation(
           summary = "Initialize deposit",
           description = "Initialize a deposit request from PayStack"
   )
    public ResponseEntity<GeneralResponseDto<DepositResponse>> initializeDeposit(
           @AuthenticationPrincipal UserPrincipal userPrincipal,
           @RequestBody DepositRequest depositRequest) {
       DepositResponse depositResponse = depositService.payStackInitDeposit(userPrincipal, depositRequest);

       GeneralResponseDto<DepositResponse> response = GeneralResponseDto.<DepositResponse>builder()
               .status("200")
               .message("Deposit Successful")
               .data(depositResponse)
               .build();
       return ResponseEntity.ok().body(response);
    }

    @PostMapping("/deposit/webhook")
    @Operation(
            summary = "Confirm Deposit via webhook",
            description = "Check Ngrok has been notified a successful transaction "
    )
    public ResponseEntity<String> handleWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader("x-paystack-signature") String signature
    ) {
        //  Verify webhook signature here before trusting payload
        String reference = (String) ((Map) payload.get("data")).get("reference");

        depositService.verifyPayment(reference);

        return ResponseEntity.ok("Webhook processed");
    }

}
