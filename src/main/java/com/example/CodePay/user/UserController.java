package com.example.CodePay.user;

import com.example.CodePay.Security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping
    public List<RegisterUserResponse> getAllUsers() {
        return userRepository.findAll(Sort.by("fullName")) // always sort by fullName
                .stream()
                .map(user -> new RegisterUserResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getWallet().getWalletNumber()
                ))
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterUserRequest request) {

        var user = userService.registerUser(request);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/updateMyProfile")
    public ResponseEntity<String> updateUser (
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody UpdateUserProfileDto updateUserProfileDto) {
        var user = userRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setFullName(updateUserProfileDto.getFullName());
        user.setAddress(updateUserProfileDto.getAddress());
        user.setDateOfBirth(updateUserProfileDto.getDateOfBirth());
        user.setPhoneNumber(updateUserProfileDto.getPhoneNumber());

        userRepository.save(user);

        return ResponseEntity.ok("Profile updated successfully");
    }

    @GetMapping("/myProfile")
    public ResponseEntity<RegisterUserResponse> getUserProfile (
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        var user = userRepository.findByEmail(userPrincipal.getEmail()).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        RegisterUserResponse registerUserResponse = new RegisterUserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getWallet().getWalletNumber()
        );
        return ResponseEntity.ok(registerUserResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById (@PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

}
