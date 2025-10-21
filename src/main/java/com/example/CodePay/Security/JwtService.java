package com.example.CodePay.Security;

import com.example.CodePay.Config.JwtConfig;
import com.example.CodePay.refresh_token.RefreshToken;
import com.example.CodePay.refresh_token.RefreshTokenRepository;
import com.example.CodePay.user.User;
import com.example.CodePay.user.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

@AllArgsConstructor
@Service
public class JwtService {

    private final UserRepository userRepository;

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private final JwtConfig  jwtConfig;
   private final RefreshTokenRepository refreshTokenRepository;

    //generate token
    public String generateAccessToken(Authentication authentication) {
        Map<String, Object> claims = new HashMap<>();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Long now = System.currentTimeMillis();
        Date issuedDate = new Date(now);
        Date expiryDate = new Date(now + jwtConfig.getAccessTokenExpirationTime());

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(userPrincipal.getEmail())
                .issuedAt(issuedDate)
                .expiration(expiryDate)
                .and()
                .signWith(getKey())
                .compact();
    }

    public String generateAccessTokenFromUser(User user) {
        Map<String, Object> claims = new HashMap<>();
        Long now = System.currentTimeMillis();
        Date issuedDate = new Date(now);
        Date expiryDate = new Date(now + jwtConfig.getAccessTokenExpirationTime());

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(user.getEmail())
                .issuedAt(issuedDate)
                .expiration(expiryDate)
                .and()
                .signWith(getKey())
                .compact();
    }

    @Transactional
    public String generateRefreshToken(Authentication authentication) {
        Map<String, Object> claims = new HashMap<>();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Long now = System.currentTimeMillis();
        Date issuedDate = new Date(now);
        Date expiryDate = new Date(now + jwtConfig.getRefreshTokenExpirationTime());

        String Token =  Jwts.builder()
                .claims()
                .add(claims)
                .subject(userPrincipal.getEmail())
                .issuedAt(issuedDate)
                .expiration(expiryDate)
                .and()
                .signWith(getKey())
                .compact();

        //get user from repo
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id " + userPrincipal.getId()));

        // delete existing refresh token in the db
        refreshTokenRepository.deleteByUserId(user.getId());

        // save new refresh token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(Token);
        refreshToken.setUser(user);
        refreshToken.setCreatedAt(Instant.now());
        refreshToken.setExpiredAt(Instant.now().plusSeconds(jwtConfig.getRefreshTokenExpirationTime()));
        refreshTokenRepository.save(refreshToken);

        return refreshToken.getToken();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build().parseSignedClaims(token).getPayload();
    }

    public Boolean validateAccessToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(getKey()).build().parseClaimsJws(token);
            return claims.getBody().getExpiration().after(new Date());
        }catch (Exception e) {
            return false;
        }
    }
        //check refreshToken expiry date and existence in db
    public Optional<User> validateRefreshToken(String refreshToken) {
       return refreshTokenRepository.findByToken(refreshToken)
               .filter(rt -> rt.getExpiredAt().isAfter(Instant.now()))
               .flatMap(rt -> userRepository.findById(rt.getUser().getId()));
    }
}
