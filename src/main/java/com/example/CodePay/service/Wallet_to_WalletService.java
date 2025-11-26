package com.example.CodePay.service;

import com.example.CodePay.enums.Roles;
import com.example.CodePay.enums.TransactionEntry;
import com.example.CodePay.exception.*;
import com.example.CodePay.security.UserPrincipal;
import com.example.CodePay.dto.TransactionHistoryDTO;
import com.example.CodePay.dto.Wallet_to_Wallet_Sender_Request;
import com.example.CodePay.dto.Wallet_to_Wallet_Sender_Response;
import com.example.CodePay.entity.Wallet;
import com.example.CodePay.enums.Status;
import com.example.CodePay.enums.TransactionType;
import com.example.CodePay.repo.TransactionRepository;
import com.example.CodePay.entity.Transaction;
import com.example.CodePay.entity.User;
import com.example.CodePay.repo.UserRepository;
import com.example.CodePay.repo.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.InsufficientResourcesException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class Wallet_to_WalletService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final DepositService depositService;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

@Transactional
    public Wallet_to_Wallet_Sender_Response walletTransfer(
            Authentication authentication,
            Wallet_to_Wallet_Sender_Request request) {
        UserPrincipal  userPrincipal = (UserPrincipal) authentication.getPrincipal();

        User sender = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(
                () -> new AuthenticatedUserNotFound());

        BigDecimal amount = request.getAmount();
        BigDecimal fee = calculateFees(amount);
        BigDecimal totalAmount = fee.add(amount);
        BigDecimal balance = sender.getWallet().getBalance();

        if (totalAmount.compareTo(balance) > 0 ){
            throw new InsufficientFundException();
        }

        User superAdmin = userRepository.findByRoles(Roles.SUPERADMIN).orElseThrow(
                () -> new ForbiddenUserException("SuperAdmin not found")
        );

        String receiverWalletNumber =  request.getReceiverWalletNumber();
        String superAdminWalletNumber = superAdmin.getWallet().getWalletNumber();

        System.out.println("Receiver wallet number: " + receiverWalletNumber);
        System.out.println("Wallet exists: " + walletRepository.existsByWalletNumber(receiverWalletNumber));

        // check if receiver wallet number is null or empty

        if (receiverWalletNumber == null || receiverWalletNumber.isEmpty() ||
                !walletRepository.existsByWalletNumber(receiverWalletNumber)) {
            throw new WalletNumberException();
        }

        // check if the receiver wallet is the same with the sender wallet

        if (receiverWalletNumber.equals(sender.getWallet().getWalletNumber())){
            throw new WalletException("You cannot Send Money to yourself");
        }

        // check if the user is a super admin

        if (sender.getRoles() == Roles.SUPERADMIN) {
            throw new ForbiddenUserException("SuperAdmin are not allowed to make transfers");
        }

        //check if the pin is matching the one with the user in the db

        boolean pinMatches = passwordEncoder.matches(request.getPin(), sender.getPin());

        if (!pinMatches) {
            throw new PinException("invalid pin");
        }


        Transaction debitTransaction = new Transaction();
        debitTransaction.setWallet(sender.getWallet());
        debitTransaction.setReference(depositService.generateReference());
        debitTransaction.setAmount(amount);
        debitTransaction.setStatus(Status.SUCCESSFUL);
        debitTransaction.setTransactionType(TransactionType.TRANSFER);
        debitTransaction.setTransactionEntry(TransactionEntry.DEBITED);
        debitTransaction.setDate(Instant.now());

        Wallet receiverwWallet = walletRepository.findByWalletNumber(receiverWalletNumber).orElseThrow(
                () -> new WalletNumberException());
        Wallet superAdminWallet = walletRepository.findByWalletNumber(superAdminWalletNumber).orElseThrow(
                () -> new WalletNumberException());

        Transaction creditTransaction = new Transaction();
        creditTransaction.setWallet(receiverwWallet);
        creditTransaction.setReference(debitTransaction.getReference());
        creditTransaction.setAmount(amount);
        creditTransaction.setStatus(Status.SUCCESSFUL);
        creditTransaction.setTransactionType(TransactionType.TRANSFER);
        creditTransaction.setTransactionEntry(TransactionEntry.CREDITED);
        creditTransaction.setDate(Instant.now());

        // creating fee transactions
        Transaction feeDebitTransaction = new Transaction();
        feeDebitTransaction.setWallet(debitTransaction.getWallet());
        feeDebitTransaction.setReference(depositService.generateReference());
        feeDebitTransaction.setAmount(fee);
        feeDebitTransaction.setStatus(Status.SUCCESSFUL);
        feeDebitTransaction.setTransactionType(TransactionType.FEE);
        feeDebitTransaction.setTransactionEntry(TransactionEntry.DEBITED);
        feeDebitTransaction.setDate(Instant.now());

        Transaction feeCreditTransaction = new Transaction();
        feeCreditTransaction.setWallet(superAdminWallet);
        feeCreditTransaction.setReference(feeDebitTransaction.getReference());
        feeCreditTransaction.setAmount(fee);
        feeCreditTransaction.setStatus(Status.SUCCESSFUL);
        feeCreditTransaction.setTransactionType(TransactionType.FEE);
        feeCreditTransaction.setTransactionEntry(TransactionEntry.CREDITED);
        feeCreditTransaction.setDate(Instant.now());

    // removing and adding money in the wallet
    sender.getWallet().setBalance(sender.getWallet().getBalance().subtract(totalAmount));
    receiverwWallet.setBalance(receiverwWallet.getBalance().add(amount));
    superAdminWallet.setBalance(superAdminWallet.getBalance().add(fee));

    //save wallet
    walletRepository.save(sender.getWallet());
    walletRepository.save(receiverwWallet);
    walletRepository.save(superAdminWallet);

    //save transaction
    transactionRepository.save(debitTransaction);
    transactionRepository.save(creditTransaction);
    transactionRepository.save(feeDebitTransaction);
    transactionRepository.save(feeCreditTransaction);


        Wallet_to_Wallet_Sender_Response response = new Wallet_to_Wallet_Sender_Response();
        response.setAmount(amount);
        response.setMessage("You've Successfully transferred " + totalAmount + " to " + receiverwWallet.getWalletNumber());
        response.setReceiverName(receiverwWallet.getUser().getFullName());
        response.setReference(debitTransaction.getReference());
        response.setDateTime(LocalDateTime.now());

        return  response;
    }

    public BigDecimal calculateFees(BigDecimal amount){
        BigDecimal feePercentage = new BigDecimal("0.015");
        return amount.multiply(feePercentage);
    }


    public List<TransactionHistoryDTO> getTransactionHistory(UserPrincipal userPrincipal) {
        User user = userRepository.findByEmail(userPrincipal.getEmail()).
                orElseThrow(() -> new AuthenticatedUserNotFound());

        List<Transaction> transactions = transactionRepository.findByWallet(user.getWallet());

        List<TransactionHistoryDTO> transactionHistory = transactions.stream()
                .map(transaction ->new TransactionHistoryDTO(
                        transaction.getWallet().getUser().getFullName(),
                        transaction.getWallet().getUser().getPhoneNumber(),
                        transaction.getReference(),
                        transaction.getAmount(),
                        transaction.getStatus(),
                        transaction.getTransactionType(),
                        transaction.getDate()
                ))
                .toList();
        return transactionHistory;
    }
}