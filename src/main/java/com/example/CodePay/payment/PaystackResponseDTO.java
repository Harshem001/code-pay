package com.example.CodePay.payment;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaystackResponseDTO {
    private Boolean status;
    private String message;
    private Map<String, Object> data;
}
