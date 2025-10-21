package com.example.CodePay.Authentication;

import com.example.CodePay.Security.JwtService;
import com.example.CodePay.refresh_token.RefreshTokenRepository;
import com.example.CodePay.user.User;
import com.example.CodePay.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("invalid email or password"));

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
