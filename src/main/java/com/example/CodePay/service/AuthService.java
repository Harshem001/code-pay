package com.example.CodePay.service;

import com.example.CodePay.dto.LoginRequest;
import com.example.CodePay.dto.LoginResponse;
import com.example.CodePay.exception.LoginException;
import com.example.CodePay.security.JwtService;
import com.example.CodePay.repo.RefreshTokenRepository;
import com.example.CodePay.entity.User;
import com.example.CodePay.repo.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new LoginException("invalid email"));

        boolean isPasswordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!isPasswordMatch) {
            throw new LoginException("wrong password");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );


        String accessToken = jwtService.generateAccessToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication);

        return new LoginResponse(accessToken, refreshToken, "Bearer");
    }

    public LoginResponse refreshToken(String refreshToken) {

        var optionalUser =  jwtService.validateRefreshToken(refreshToken);
        var user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("invalid email or password"));

        String newAccessToken = jwtService.generateAccessTokenFromUser(user);

        return new LoginResponse(newAccessToken, refreshToken, "Bearer");
    }

    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(refreshTokenRepository::delete);
    }

}
