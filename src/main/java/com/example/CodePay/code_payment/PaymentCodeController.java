package com.example.CodePay.code_payment;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/codePayment")
@AllArgsConstructor
public class PaymentCodeController {
    private PaymentCodeService paymentCodeService;

    @PostMapping("/generateCode")
    public ResponseEntity<PaymentCodeResponse> generatePaymentCode(
            Authentication authentication,
            @RequestBody PaymentCodeRequest request) {

        return  ResponseEntity.ok(paymentCodeService.generatePaymentCode(authentication, request));
    }

}
