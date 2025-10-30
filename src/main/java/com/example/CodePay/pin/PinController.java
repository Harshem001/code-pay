package com.example.CodePay.pin;

import com.example.CodePay.Security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/setPin")
@Tag(name = "Set Pin", description = "Set Pin to confirm transfer")
public class PinController {

    private final SetPinService setPinService;

    @PostMapping
    @Operation(
            summary = "Set Pin",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pin Has been Set Successfully"),
                    @ApiResponse(responseCode = "400", description = "Could not set pin")
            }
    )
    public ResponseEntity<PinResponse> setPin(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody PinRequest pinRequest) {
        setPinService.setPin(userPrincipal, pinRequest);
        PinResponse response = new PinResponse();
        response.setMessage("You have Successfully Set your Pin");
        return ResponseEntity.ok(response);
    }
}
