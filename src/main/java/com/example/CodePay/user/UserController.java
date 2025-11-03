package com.example.CodePay.user;

import com.example.CodePay.Security.UserPrincipal;
import com.example.CodePay.code_payment.GeneralResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Registering User")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping
    @Operation(
            summary = "Get All Users",
            description = "Retrieve all users registered on CodePay"
    )
    public ResponseEntity<GeneralResponseDto<List<RegisterUserResponse>>> getAllUsers() {

        List<RegisterUserResponse> userList = userRepository.findAll(Sort.by("fullName"))
                .stream()
                .map(user -> new RegisterUserResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getWallet().getWalletNumber()
                ))
                .toList();

        GeneralResponseDto<List<RegisterUserResponse>> response = GeneralResponseDto.<List<RegisterUserResponse>>builder()
                .status("200")
                .message("Here are all the users on CodePay")
                .data(userList)
                .build();

        return ResponseEntity.ok(response);
    }


    @PostMapping
    @Operation(
            summary = "Register User"
    )
    public ResponseEntity<GeneralResponseDto<?>> registerUser(
            @Valid @RequestBody RegisterUserRequest request) {

        var user = userService.registerUser(request);
        var response = GeneralResponseDto.builder()
                .status("200")
                .message("You've successfully registered on CodePay")
                .data(user)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/updateMyProfile")
    @Operation(
            summary = "Update user profile ",
            description = "Endpoint for updating a logged in user"
    )
    public ResponseEntity<GeneralResponseDto<String>> updateUser (
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody UpdateUserProfileDto updateUserProfileDto) {
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

    @GetMapping("/myProfile")
    @Operation(
            summary = "View my profile"
    )
    public ResponseEntity<GeneralResponseDto<RegisterUserResponse>> getUserProfile (
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        var user = userRepository.findByEmail(userPrincipal.getEmail()).
                orElseThrow(() -> new  UsernameNotFoundException("User not found"));
        RegisterUserResponse registerUserResponse = new RegisterUserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getWallet().getWalletNumber()
        );
        GeneralResponseDto<RegisterUserResponse> generalResponseDto = GeneralResponseDto.<RegisterUserResponse>builder()
                .status("200")
                .message("Your Profile")
                .data(registerUserResponse)
                .build();
        return ResponseEntity.ok(generalResponseDto);
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
