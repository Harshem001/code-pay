package com.example.CodePay.repo;

import com.example.CodePay.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    boolean existsByEmail(@NotBlank(message = "E-mail is required") @Email(message = "Email must be valid.") String email);

    Iterable<Long> id(Long id);

    Optional<User> findByEmail(String email);

    String email(String email);
}
