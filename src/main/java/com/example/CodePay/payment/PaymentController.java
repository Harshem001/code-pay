package com.example.CodePay.payment;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

   @PostMapping("/deposit")
    public ResponseEntity<DepositResponse> initializeDeposit(@RequestBody DepositRequest depositRequest) {
        return ResponseEntity.ok(paymentService.payStackInitDeposit(depositRequest));
    }

    @PostMapping("/deposit/webhook")
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
