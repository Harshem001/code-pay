package com.example.CodePay.payment;

import com.example.CodePay.user.User;
import com.example.CodePay.user.UserRepository;
import com.example.CodePay.wallet.Wallet;
import com.example.CodePay.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestTemplateBuilder restTemplateBuilder;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${PAYSTACK_SECRET_KEY}")
    private String secretKey;

    @Value("${PAYSTACK_URL}")
    private String payStackUrl;

    @Value("${PAYSTACK_VERIFY_URL}")
    private String payStackVerifyUrl;

    public DepositResponse payStackInitDeposit(DepositRequest depositRequest) {
       String reference;
       reference = generateReference();
        User user = userRepository.findByEmail(depositRequest.getEmail()).orElseThrow(
                () -> new RuntimeException("User not found with email: " + depositRequest.getEmail())
        );

        //save pending Transaction
        Transaction transaction = new Transaction();
        transaction.setWallet(user.getWallet());
        transaction.setReference(reference);
        transaction.setAmount(depositRequest.getAmount());
        transaction.setStatus("PENDING");
        transaction.setTransactionType("CREDITED");
        transaction.setDate(Instant.now());

        paymentRepository.save(transaction);

        // paystack initializer API
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> request = new HashMap<>();
        request.put("email",  depositRequest.getEmail());
        request.put("amount",  depositRequest.getAmount().multiply(new BigDecimal(100)));
        request.put("reference", reference);
        request.put("metaData",  Map.of("walletNumber", user.getWallet().getWalletNumber()));


        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(request, headers); // wrapping the header and request together

        ResponseEntity<PaystackResponseDTO>  response = restTemplate.exchange(payStackUrl, HttpMethod.POST, requestEntity, PaystackResponseDTO.class);


        String authorizationUrl = response.getBody().getData().get("authorization_url").toString();

        DepositResponse depositResponse = new DepositResponse();
        depositResponse.setAuthorizationUrl(authorizationUrl);
        depositResponse.setTransactionId(transaction.getId());
        depositResponse.setStatus((Boolean) response.getBody().getStatus());
        depositResponse.setReference(reference);
        depositResponse.setAmount(depositRequest.getAmount());
        depositResponse.setDate(Instant.now());

        return depositResponse;
    }

    public String generateReference(){
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);

    }

    public void verifyPayment(String reference) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(secretKey);

        ResponseEntity<Map> response = restTemplateBuilder.build().exchange(
                payStackVerifyUrl + reference, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody().get("data");
        String status = (String) responseBody.get("status");

        // to check the reference in my db that was created when initializing payment.
        Transaction transaction = paymentRepository.findByReference(reference).orElseThrow(
                () -> new RuntimeException("No matching reference found in the DB"));

        if ("success".equalsIgnoreCase(status)) {
            transaction.setStatus("SUCCESSFUL");

            Wallet wallet = walletRepository.findById(transaction.getWallet().getId()).orElseThrow(
                    () -> new RuntimeException("No matching wallet found in the DB"));

            transaction.setWallet(wallet);

            wallet.setBalance(wallet.getBalance().add(transaction.getAmount().divide(BigDecimal.valueOf(100)))
            );
            walletRepository.save(wallet);
        }else  {
            transaction.setStatus("FAILED");
        }
        paymentRepository.save(transaction);
    }
}
