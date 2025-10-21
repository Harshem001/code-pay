package com.example.CodePay.code_payment;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/code")
public class RedeemCodeController {


    private final RedeemCodeService redeemCodeService;

    @PostMapping("/redeemCode")
    public ResponseEntity<RedeemCodeResponse> getRedeemCodeResponse(
            Authentication authentication,
            @RequestBody RedeemCodeRequest request) {
        return ResponseEntity.ok(redeemCodeService.redeemCode(authentication, request));
    }
}
