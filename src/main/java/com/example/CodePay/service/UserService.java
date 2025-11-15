package com.example.CodePay.service;

import com.example.CodePay.dto.*;
import com.example.CodePay.entity.User;
import com.example.CodePay.enums.Roles;
import com.example.CodePay.exception.EmailAlreadyExistException;
import com.example.CodePay.repo.UserRepository;
import com.example.CodePay.entity.Wallet;
import com.example.CodePay.repo.WalletRepository;
import com.example.CodePay.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Random;

@AllArgsConstructor
@Service
public class UserService {
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletRepository walletRepository;


    public RegisterUserResponse registerUser(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistException();
        }
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAddress(request.getAddress());
        user.setRoles(Roles.USER);
        user.setCreatedAt(Instant.now());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setGender(request.getGender());
        user.setBvn(request.getBvn());

        User savedUser = userRepository.save(user);

        String walletNumber;
        do{
            walletNumber = generateWalletNumber();
        } while(walletRepository.existsByWalletNumber(walletNumber));

        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setWalletNumber(walletNumber);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCreatedAt(Instant.now());

        Wallet savedWallet = walletRepository.save(wallet);

        savedUser.setWallet(savedWallet);

        userRepository.save(savedUser);

        RegisterUserResponse registerUserResponse = new RegisterUserResponse();
        registerUserResponse.setId(savedUser.getId());
        registerUserResponse.setFullName(savedUser.getFullName());
        registerUserResponse.setEmail(savedUser.getEmail());
        registerUserResponse.setWalletNumber(savedWallet.getWalletNumber());

        return registerUserResponse;

    }

    public String generateWalletNumber(){
        Random random = new Random();
        long min = 1_000_000_000L;
        long max = 9_999_999_999L;
        long walletNumber = min + ((long) (random.nextDouble() * (max - min + 1)));
        return Long.toString(walletNumber);
    }

    public ResponseEntity<GeneralResponseDto<String>> deleteUserByEmail(UserPrincipal userPrincipal, DeleteUserRequest request) {
        var user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));

        if (user.getEmail().equals(request.getEmail())) {
            throw new IllegalArgumentException("You cannot delete yourself");
        }

        //check if the authorized user is an admin before deleting
        if (!userPrincipal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new IllegalArgumentException("You do not have permission to delete user");
        }

        User userToDelete = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () ->  new UsernameNotFoundException("User not found"));

        userRepository.delete(userToDelete);

        GeneralResponseDto<String> response = GeneralResponseDto.<String>builder()
                .status("200")
                .message("You've Successfully deleted the user with " + request.getEmail())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<GeneralResponseDto<RegisterUserResponse>> getProfile(UserPrincipal userPrincipal) {
        var user = userRepository.findByEmail(userPrincipal.getEmail()).
                orElseThrow(() -> new  UsernameNotFoundException("User not found"));
        RegisterUserResponse registerUserResponse = new RegisterUserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getWallet().getWalletNumber(),
                user.getWallet().getBalance(),
                user.getPhoneNumber(),
                user.getWallet().getTransactions().getLast().getStatus()
        );
        GeneralResponseDto<RegisterUserResponse> generalResponseDto = GeneralResponseDto.<RegisterUserResponse>builder()
                .status("200")
                .message("Your Profile")
                .data(registerUserResponse)
                .build();
        return ResponseEntity.ok(generalResponseDto);
    }


    public ResponseEntity<GeneralResponseDto<String>> updateProfile(UserPrincipal userPrincipal, UpdateUserProfileDto updateUserProfileDto) {
        var user = userRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setFullName(updateUserProfileDto.getFullName());
        user.setAddress(updateUserProfileDto.getAddress());
        user.setDateOfBirth(updateUserProfileDto.getDateOfBirth());
        user.setPhoneNumber(updateUserProfileDto.getPhoneNumber());

        userRepository.save(user);
        GeneralResponseDto<String> response = GeneralResponseDto.<String>builder()
                .status("200")
                .message("Profile Updated succesfully")
                .data("Profile Updated successfully")
                .build();

        return ResponseEntity.ok(response);
    }

}
