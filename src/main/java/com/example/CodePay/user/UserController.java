package com.example.CodePay.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriComponentsBuilder) {

        var user = userService.registerUser(request);

        var uri = uriComponentsBuilder.path("/users/{id}").buildAndExpand(user.getId());
        return ResponseEntity.created(uri.toUri()).body(user);
    }

    @PostMapping("/{id}")
    public ResponseEntity<RegisterUserResponse> updateUser (
            @PathVariable(name = "id") Long id, @RequestBody RegisterUserResponse registerUserResponse) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        user.setFullName(registerUserResponse.getFullName());
        user.setEmail(registerUserResponse.getEmail());

        userRepository.save(user);

        return ResponseEntity.ok(registerUserResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegisterUserResponse> getUserById (@PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);
        RegisterUserResponse registerUserResponse = new RegisterUserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getWallet().getWalletNumber()
        );
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
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
