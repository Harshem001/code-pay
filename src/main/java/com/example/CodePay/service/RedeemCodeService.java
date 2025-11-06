package com.example.CodePay.service;

import com.example.CodePay.Security.UserPrincipal;
import com.example.CodePay.enums.Status;
import com.example.CodePay.enums.TransactionEntry;
import com.example.CodePay.enums.TransactionType;
import com.example.CodePay.utils.GeoUtils;
import com.example.CodePay.dto.RedeemCodeRequest;
import com.example.CodePay.dto.RedeemCodeResponse;
import com.example.CodePay.entity.PaymentCode;
import com.example.CodePay.enums.PaymentCodeEnum;
import com.example.CodePay.repo.PaymentRepository;
import com.example.CodePay.entity.Transaction;
import com.example.CodePay.repo.PaymentCodeRepository;
import com.example.CodePay.entity.User;
import com.example.CodePay.repo.UserRepository;
import com.example.CodePay.entity.Wallet;
import com.example.CodePay.repo.WalletRepository;
import lombok.RequiredArgsConstructor;
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
    private final GeoUtils geoUtils;

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

        // validate geo-location
        double distance = geoUtils.calculateDistance(
                paymentCode.getTarget_lat(), paymentCode.getTarget_lng(),
                request.getRedeemerLat(), request.getRedeemerLon());

        if (distance > paymentCode.getRadius_meters()){
            throw new RuntimeException("You're not within the allowed location to redeem this code");
        }
        //check if the code has expired and if the sender's balanace is upto the amount.
        if (paymentCode.getExpireAt().isAfter(Instant.now())
                && senderBalance.compareTo(amountSent) > 0) {

            //set debit transaction
            Transaction debitTransaction = new Transaction();
            debitTransaction.setWallet(paymentCode.getSender().getWallet());
            debitTransaction.setReference(paymentService.generateReference());
            debitTransaction.setStatus(Status.SUCCESSFUL);
            debitTransaction.setTransactionType(TransactionType.CODE_TRANSFER);
            debitTransaction.setTransactionEntry(TransactionEntry.DEBITED);
            debitTransaction.setDate(Instant.now());
            debitTransaction.setAmount(amountSent);

            // set credit transaction
            Transaction creditTransaction = new Transaction();
            creditTransaction.setWallet(redeemerwallet);
            creditTransaction.setReference(debitTransaction.getReference());
            creditTransaction.setStatus(Status.SUCCESSFUL);
            creditTransaction.setTransactionType(TransactionType.CODE_TRANSFER);
            creditTransaction.setTransactionEntry(TransactionEntry.CREDITED);
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
        }else {
                throw new RuntimeException("Payment Code with code " + request.getCode() + " has expired");
            }

        return  RedeemCodeResponse.builder()
                .amount(paymentCode.getAmount())
                .senderName(paymentCode.getSender().getFullName())
                .createdAt(paymentCode.getCreatedAt())
                .expireAt(paymentCode.getExpireAt())
                .build();

    }
}
