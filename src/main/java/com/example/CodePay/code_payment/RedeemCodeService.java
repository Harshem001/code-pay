package com.example.CodePay.code_payment;

import com.example.CodePay.Security.UserPrincipal;
import com.example.CodePay.payment.PaymentRepository;
import com.example.CodePay.payment.PaymentService;
import com.example.CodePay.payment.Transaction;
import com.example.CodePay.user.User;
import com.example.CodePay.user.UserRepository;
import com.example.CodePay.wallet.Wallet;
import com.example.CodePay.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RedeemCodeService {


    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PaymentCodeRepository paymentCodeRepository;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    public RedeemCodeResponse redeemCode(Authentication authentication, RedeemCodeRequest request) {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        User redeemer = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("invalid email or password")
        );

        //get redeemer wallet, where the money will enter
        Wallet redeemerwallet = walletRepository.getWalletById(redeemer.getId());

        //check if the code is in the DB
        PaymentCode paymentCode = paymentCodeRepository.findByCode(request.getCode()).orElseThrow(
                () -> new RuntimeException("Payment Code with code " + request.getCode() + " not found"));

        BigDecimal senderBalance =  paymentCode.getSender().getWallet().getBalance();
        BigDecimal amountSent = paymentCode.getAmount();

        if (paymentCode.getExpireAt().isAfter(Instant.now())
                && senderBalance.compareTo(amountSent) > 0) {
            //set debit transaction
            Transaction debitTransaction = new Transaction();
            debitTransaction.setWallet(paymentCode.getSender().getWallet());
            debitTransaction.setReference(paymentService.generateReference());
            debitTransaction.setStatus("Successful");
            debitTransaction.setTransactionType("DEBITED");
            debitTransaction.setDate(Instant.now());
            debitTransaction.setAmount(amountSent);

            // set credit transaction
            Transaction creditTransaction = new Transaction();
            creditTransaction.setWallet(redeemerwallet);
            creditTransaction.setReference(debitTransaction.getReference());
            creditTransaction.setStatus("Successful");
            creditTransaction.setTransactionType("CREDITED");
            creditTransaction.setDate(Instant.now());
            creditTransaction.setAmount(amountSent);

            //save transaction
            paymentRepository.save(debitTransaction);
            paymentRepository.save(creditTransaction);

            //get sender wallet
            Wallet senderwallet = paymentCode.getSender().getWallet();

            //set balance after credit and debit both in sender and receiver
            senderwallet.setBalance(senderwallet.getBalance().subtract(amountSent));
            redeemerwallet.setBalance(redeemerwallet.getBalance().add(amountSent));

            walletRepository.save(redeemerwallet);
            walletRepository.save(senderwallet);

            //set payment code status
            paymentCode.setRedeemedAt(Instant.now());
            paymentCode.setStatus(PaymentCodeEnum.REDEEMED);
            paymentCode.setReceiverId(redeemer);

            paymentCodeRepository.save(paymentCode);
        }
        return  RedeemCodeResponse.builder()
                .amount(paymentCode.getAmount())
                .senderName(paymentCode.getSender().getFullName())
                .createdAt(paymentCode.getCreatedAt())
                .expireAt(paymentCode.getExpireAt())
                .build();

    }
}
