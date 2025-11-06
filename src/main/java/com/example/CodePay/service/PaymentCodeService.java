package com.example.CodePay.service;

import com.example.CodePay.Security.UserPrincipal;
import com.example.CodePay.dto.PaymentCodeRequest;
import com.example.CodePay.dto.PaymentCodeResponse;
import com.example.CodePay.entity.PaymentCode;
import com.example.CodePay.enums.PaymentCodeEnum;
import com.example.CodePay.repo.PaymentCodeRepository;
import com.example.CodePay.entity.User;
import com.example.CodePay.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PaymentCodeService {


    private final UserRepository userRepository;
    private final PaymentCodeRepository paymentCodeRepository;
    private final PasswordEncoder passwordEncoder;

    public PaymentCodeResponse generatePaymentCode(Authentication authentication, PaymentCodeRequest request){

        //get userPrincipal from authentication object
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        //get the user from the dataBase
        User sender = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("invalid email or password"));



        if (sender.getWallet() == null) {
            throw new RuntimeException("Sender does not have a wallet assigned");
        }

        BigDecimal balance = sender.getWallet().getBalance();

        if (balance == null) {
            throw new RuntimeException("Wallet balance not initialized");
        }

        System.out.println("Amount received: " + request.getAmount());

        if (balance.compareTo(request.getAmount()) < 0){
            throw new RuntimeException("Insufficient funds");
        }

        //check if the pin is matching the one with the user in the db

        boolean pinMatches = passwordEncoder.matches(request.getPin(), sender.getPin());

        if (!pinMatches) {
            throw new IllegalArgumentException("invalid pin");
        }

        //generate unique payment code and set expiry date
        String code;
        do {
            code = generateCode();
        }while(paymentCodeRepository.existsByCode(code)); // checking the db if a generated code exist

        Instant createdAt = Instant.now();
        Instant expiredAt = createdAt.plusMillis(86400000);


        PaymentCode paymentCode = PaymentCode.builder()
                .code(code)
                .sender(sender)
                .amount(request.getAmount())
                .status(PaymentCodeEnum.PENDING)
                .createdAt(createdAt)
                .expireAt(expiredAt)
                .target_lat(request.getTarget_lat())
                .target_lng(request.getTarget_lng())
                .radius_meters(request.getRadius_meters())
                .build();

        paymentCodeRepository.save(paymentCode);

        return PaymentCodeResponse.builder()
                .code(code)
                .amount(request.getAmount())
                .senderName(sender.getFullName())
                .createdAt(createdAt)
                .expireAt(expiredAt)
                .status(paymentCode.getStatus())
                .build();
    }

    public String generateCode(){
        UUID code = UUID.randomUUID();
        return code.toString().replace("-", "").substring(0, 10);
    }

}
