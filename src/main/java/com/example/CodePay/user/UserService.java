package com.example.CodePay.user;

import com.example.CodePay.wallet.Wallet;
import com.example.CodePay.wallet.WalletRepository;
import lombok.AllArgsConstructor;
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
        user.setRoles(Roles.USER);
        user.setCreatedAt(Instant.now());
        user.setPin(passwordEncoder.encode(request.getPin()));

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

}
