package com.example.CodePay.payment;

import com.example.CodePay.Security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    private final PaymentService paymentService;

   @PostMapping("/deposit")
   @Operation(
           summary = "Initialize deposit",
           description = "Initialize a deposit request from PayStack"
   )
    public ResponseEntity<DepositResponse> initializeDeposit(
           @AuthenticationPrincipal UserPrincipal userPrincipal,
           @RequestBody DepositRequest depositRequest) {
        return ResponseEntity.ok(paymentService.payStackInitDeposit(userPrincipal, depositRequest));
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

        paymentService.verifyPayment(reference);

        return ResponseEntity.ok("Webhook processed");
    }

}
