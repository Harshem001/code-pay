package com.example.CodePay.user;

import com.example.CodePay.wallet.Wallet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "roles")
    @Enumerated(EnumType.STRING)
    private Roles roles;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "pin")
    private String pin;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Wallet wallet;

}