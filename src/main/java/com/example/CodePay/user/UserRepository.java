package com.example.CodePay.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    boolean existsByEmail(@NotBlank(message = "E-mail is required") @Email(message = "Email must be valid.") String email);

    Iterable<Long> id(Long id);

    Optional<User> findByEmail(String email);

    String email(String email);
}
