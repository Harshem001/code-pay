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

    public void setPin(UserPrincipal userPrincipal, PinRequest pinRequest) {
        if (pinRequest == null || pinRequest.getPin() == null || pinRequest.getPin().isBlank()) {
            throw new UsernameNotFoundException("Pin cannot be empty");
        }

        User user = userRepository.findByEmail(userPrincipal.getEmail()).
                orElseThrow(() -> new UsernameNotFoundException(userPrincipal.getEmail()));

        user.setPin(passwordEncoder.encode(pinRequest.getPin()));
        userRepository.save(user);
        new PinResponse();
    }
}
