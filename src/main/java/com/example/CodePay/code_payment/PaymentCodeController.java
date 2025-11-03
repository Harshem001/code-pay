package com.example.CodePay.code_payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/codePayment")
@AllArgsConstructor
@Tag(name = "Code Payment", description = "Endpoint for generating Redeemable code")
public class PaymentCodeController {
    private final RestClient.Builder builder;
    private PaymentCodeService paymentCodeService;

    @PostMapping("/generateCode")
    @Operation(
            summary = "Code pay",
            description = "Generate a code while transferring funds to an individual that has no account with the system"
    )
    public ResponseEntity<GeneralResponseDto<PaymentCodeResponse>> generatePaymentCode(
            Authentication authentication,
            @RequestBody PaymentCodeRequest request) {

       PaymentCodeResponse response = paymentCodeService.generatePaymentCode(authentication, request);

       GeneralResponseDto<PaymentCodeResponse> generalResponseDto = GeneralResponseDto.<PaymentCodeResponse>builder()
               .status("200")
               .message("Payment Code Generated Successfully")
               .data(response)
               .build();
       return ResponseEntity.ok(generalResponseDto);
    }

}
