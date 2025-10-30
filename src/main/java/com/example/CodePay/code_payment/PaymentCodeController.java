package com.example.CodePay.code_payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/codePayment")
@AllArgsConstructor
@Tag(name = "Code Payment", description = "Endpoint for generating Redeemable code")
public class PaymentCodeController {
    private PaymentCodeService paymentCodeService;

    @PostMapping("/generateCode")
    @Operation(
            summary = "Code pay",
            description = "Generate a code while transferring funds to an individual that has no account with the system"
    )
    public ResponseEntity<PaymentCodeResponse> generatePaymentCode(
            Authentication authentication,
            @RequestBody PaymentCodeRequest request) {

        return  ResponseEntity.ok(paymentCodeService.generatePaymentCode(authentication, request));
    }

}
