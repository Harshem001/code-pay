package com.example.CodePay.code_payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/code")
@Tag(name = "Redeem Payment Code", description = "Endpoint for redeeming Payment Code sent by Sender")
public class RedeemCodeController {


    private final RedeemCodeService redeemCodeService;

    @PostMapping("/redeemCode")
    @Operation(
            summary = "Redeem Code"
    )
    public ResponseEntity<RedeemCodeResponse> getRedeemCodeResponse(
            Authentication authentication,
            @RequestBody RedeemCodeRequest request) {
        return ResponseEntity.ok(redeemCodeService.redeemCode(authentication, request));
    }
}
