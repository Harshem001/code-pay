package com.example.CodePay.wallet;

import com.example.CodePay.Security.UserPrincipal;
import com.example.CodePay.payment.PaymentRepository;
import com.example.CodePay.payment.PaymentService;
import com.example.CodePay.payment.Transaction;
import com.example.CodePay.user.User;
import com.example.CodePay.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@AllArgsConstructor
@Service
public class Wallet_to_WalletService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;


    public Wallet_to_Wallet_Sender_Response walletTransfer(
            Authentication authentication,
            Wallet_to_Wallet_Sender_Request request) {
        UserPrincipal  userPrincipal = (UserPrincipal) authentication.getPrincipal();

        User sender = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("invalid email or password"));

        BigDecimal amount = request.getAmount();
        BigDecimal balance = sender.getWallet().getBalance();

        if (amount.compareTo(balance) > 0 ){
            throw new RuntimeException("insufficient funds");
        }
        String receiverWalletNumber =  request.getReceiverWalletNumber();

        System.out.println("Receiver wallet number: " + receiverWalletNumber);
        System.out.println("Wallet exists: " + walletRepository.existsByWalletNumber(receiverWalletNumber));


        if (receiverWalletNumber == null || receiverWalletNumber.isEmpty() ||
                !walletRepository.existsByWalletNumber(receiverWalletNumber)) {
            throw new RuntimeException("Invalid receiver wallet number");
        }

        //check if the pin is matching the one with the user in the db

        boolean pinMatches = passwordEncoder.matches(request.getPin(), sender.getPin());

        if (!pinMatches) {
            throw new IllegalArgumentException("invalid pin");
        }

        Transaction debitTransaction = new Transaction();
        debitTransaction.setWallet(sender.getWallet());
        debitTransaction.setReference(paymentService.generateReference());
        debitTransaction.setAmount(amount);
        debitTransaction.setStatus("Successful");
        debitTransaction.setTransactionType("Debited");
        debitTransaction.setDate(Instant.now());

        Wallet receiverwWallet = walletRepository.findByWalletNumber(receiverWalletNumber).orElseThrow(
                () -> new UsernameNotFoundException("invalid receiver wallet number")
        );

        Transaction creditTransaction = new Transaction();
        creditTransaction.setWallet(receiverwWallet);
        creditTransaction.setReference(debitTransaction.getReference());
        creditTransaction.setAmount(amount);
        creditTransaction.setStatus("Successful");
        creditTransaction.setTransactionType("Credited");
        creditTransaction.setDate(Instant.now());

        paymentRepository.save(debitTransaction);
        paymentRepository.save(creditTransaction);

        // removing and adding money in the wallet
        sender.getWallet().setBalance(sender.getWallet().getBalance().subtract(amount));
        receiverwWallet.setBalance(receiverwWallet.getBalance().add(amount));

        walletRepository.save(sender.getWallet());
        walletRepository.save(receiverwWallet);

        Wallet_to_Wallet_Sender_Response response = new Wallet_to_Wallet_Sender_Response();
        response.setAmount(amount);
        response.setMessage("You've Successfully transferred " + amount + " to " + receiverwWallet.getWalletNumber());
        response.setReceiverName(receiverwWallet.getUser().getFullName());
        response.setReference(debitTransaction.getReference());

        return  response;
    }
}