package com.example.CodePay.Config;

import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.util.Base64;

@Configuration
@Data
public class JwtConfig {

    @Value("${spring.jwt.secret}")
    private String secret;

    @Value("${spring.jwt.accessTokenExpiration}")
    private Long accessTokenExpirationTime;

    @Value("${spring.jwt.refreshTokenExpiration}")
    private Long refreshTokenExpirationTime;

}
