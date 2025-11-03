package com.example.CodePay.pin;

import com.example.CodePay.Security.UserPrincipal;
import com.example.CodePay.user.User;
import com.example.CodePay.user.UserRepository;
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
        userRepository.save(user);
        return new PinResponse("Pin set successfully");
    }
}
