package com.example.CodePay.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.accessTokenExpiration}")
    private Long accessTokenExpirationTime;

    @Value("${jwt.refreshTokenExpiration}")
    private Long refreshTokenExpirationTime;

}
