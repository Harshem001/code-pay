package com.example.CodePay.service;

import com.example.CodePay.enums.UserStatus;
import com.example.CodePay.security.UserPrincipal;
import com.example.CodePay.dto.PinRequest;
import com.example.CodePay.dto.PinResponse;
import com.example.CodePay.entity.User;
import com.example.CodePay.repo.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SetPinService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PinResponse setPin(UserPrincipal userPrincipal, PinRequest pinRequest) {
        if (pinRequest == null || pinRequest.getPin() == null || pinRequest.getPin().isBlank()) {
            throw new IllegalArgumentException("Pin cannot be empty");
        }
        String pin = pinRequest.getPin();
        if (!pin.matches("\\d{4,6}")) {
            throw new IllegalArgumentException("PIN must be 4–6 digits");
        }

        User user = userRepository.findByEmail(userPrincipal.getEmail()).
                orElseThrow(() -> new UsernameNotFoundException(userPrincipal.getEmail()));

        user.setPin(passwordEncoder.encode(pin));
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        return new PinResponse("Pin set successfully");
    }
}
