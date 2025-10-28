package com.example.CodePay.pin;

import com.example.CodePay.Security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/setPin")
public class PinController {

    private final SetPinService setPinService;

    public PinController(SetPinService setPinService) {
        this.setPinService = setPinService;
    }

    @PostMapping
    public ResponseEntity<PinResponse> setPin(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody PinRequest pinRequest) {
        setPinService.setPin(userPrincipal, pinRequest);
        PinResponse response = new PinResponse();
        response.setMessage("You have Successfully Set your Pin");
        return ResponseEntity.ok(response);
    }
}
