package com.example.CodePay.code_payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralResponseDto<T> {
    private String status;
    private String message;
    private T data;
}
