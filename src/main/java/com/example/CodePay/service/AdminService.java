package com.example.CodePay.service;

import com.example.CodePay.dto.DashBoardStatsDto;
import com.example.CodePay.dto.GeneralResponseDto;
import com.example.CodePay.dto.RegisterUserResponse;
import com.example.CodePay.dto.TransactionDto;
import com.example.CodePay.entity.Transaction;
import com.example.CodePay.entity.User;
import com.example.CodePay.enums.TransactionEntry;
import com.example.CodePay.enums.TransactionType;
import com.example.CodePay.repo.TransactionRepository;
import com.example.CodePay.repo.UserRepository;
import com.example.CodePay.repo.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class AdminService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final RestClient.Builder builder;


    public GeneralResponseDto<List<RegisterUserResponse>> getListOfUsers() {
        List<RegisterUserResponse> userList = userRepository.findAllUserWithWallet()
                .stream()
                .map(user -> new RegisterUserResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getWallet().getWalletNumber(),
                        user.getWallet().getBalance(),
                        user.getPhoneNumber(),
                        user.getWallet().getTransactions().getLast().getStatus()
                ))
                .toList();

        GeneralResponseDto<List<RegisterUserResponse>> response = GeneralResponseDto.<List<RegisterUserResponse>>builder()
                .status("200")
                .message("Here are all the users on CodePay")
                .data(userList)
                .build();
        return response;
    }

    public GeneralResponseDto<RegisterUserResponse> getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User with id " + id + " not found"));

        RegisterUserResponse response = RegisterUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .fullName(user.getFullName())
                .walletNumber(user.getWallet().getWalletNumber())
                .balance(user.getWallet().getBalance())
                .status(user.getWallet().getTransactions().getLast().getStatus())
                .build();

        GeneralResponseDto<RegisterUserResponse> userProfile = GeneralResponseDto.<RegisterUserResponse>builder()
                .status("200")
                .message("User profile")
                .data(response)
                .build();
        return userProfile;
    }

    public GeneralResponseDto<Map<String, Object>> getListOfTransactions(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactionPage = transactionRepository.findAll(pageable);

        List<TransactionDto> transactionList = transactionPage.getContent()
                .stream()
                .map(transaction -> new TransactionDto(
                        transaction.getId(),
                        transaction.getAmount(),
                        transaction.getTransactionType(),
                        transaction.getStatus(),
                        transaction.getTransactionEntry(),
                        transaction.getDate()
                ))
                .toList();

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("transactions", transactionList);
        responseData.put("totalItems", transactionPage.getTotalElements());
        responseData.put("currentPage", transactionPage.getNumber());
        responseData.put("totalPages", transactionPage.getTotalPages());

       return GeneralResponseDto.<Map<String, Object>>builder()
               .status("200")
               .message("Transaction List Gotten Successfully")
               .data(responseData)
               .build();
    }

    private GeneralResponseDto<Long> getTotalUsers() {
        Long userCount = userRepository.count();
        GeneralResponseDto<Long> totalNumberOfUser = GeneralResponseDto.<Long>builder()
                .status("200")
                .message("Here is the number of users on CodePay")
                .data(userCount)
                .build();
        return totalNumberOfUser;
    }

    private GeneralResponseDto<BigDecimal> getMonthlyIncome() {
        ZoneId zone =  ZoneId.systemDefault();

        LocalDateTime startOfMonth = LocalDateTime.now(zone).withDayOfMonth(1);
        LocalDateTime endOfMonth =  LocalDateTime.now();

        Instant beginningOfMonth = startOfMonth.atZone(zone).toInstant();
        Instant endingOfMonth = endOfMonth.atZone(zone).toInstant();


        TransactionType type = TransactionType.DEPOSIT;
        TransactionEntry entry = TransactionEntry.CREDITED;

        GeneralResponseDto<BigDecimal> getMonthlyIncomeResponse = GeneralResponseDto.<BigDecimal>builder()
                .status("200")
                .message("This is the Monthly Income Of CodePay")
                .data(transactionRepository.calculateMonthlyIncome(type, entry, beginningOfMonth, endingOfMonth))
                .build();

        return getMonthlyIncomeResponse;
    }

    private GeneralResponseDto<BigDecimal> getMonthlyExpenses() {
        ZoneId zone =  ZoneId.systemDefault();

        LocalDateTime startOfMonth = LocalDateTime.now(zone).withDayOfMonth(1);
        LocalDateTime endOfMonth =  LocalDateTime.now();

        Instant beginningOfMonth = startOfMonth.atZone(zone).toInstant();
        Instant endingOfMonth = endOfMonth.atZone(zone).toInstant();

        List<String> types = List.of("TRANSFER", "WITHDRAWAL", "CODE_TRANSFER");
        TransactionEntry entry = TransactionEntry.DEBITED;

        GeneralResponseDto<BigDecimal> getMonthlyExpensesResponse = GeneralResponseDto.<BigDecimal>builder()
                .status("200")
                .message("This is the Monthly Expenses for CodePay")
                .data(transactionRepository.calculateMonthlyExpenses(types, entry, beginningOfMonth, endingOfMonth))
                .build();
        return getMonthlyExpensesResponse;
    }

    private GeneralResponseDto<BigDecimal> getTotalSystemBalance() {
        BigDecimal totalAmount = walletRepository.getTotalAmount();
        GeneralResponseDto<BigDecimal> getTotalSystemBalanceResponse = GeneralResponseDto.<BigDecimal>builder()
                .status("200")
                .message("Here's the total Amount In CodePay")
                .data(totalAmount)
                .build();
        return getTotalSystemBalanceResponse;
    }

    public GeneralResponseDto<DashBoardStatsDto>  getDashBoardStats() {
        GeneralResponseDto<BigDecimal> monthlyIncome = getMonthlyIncome();
        GeneralResponseDto<BigDecimal> totalSystemBalance = getTotalSystemBalance();
        GeneralResponseDto<Long> totalUsers = getTotalUsers();
        GeneralResponseDto<BigDecimal> monthlyExpenses = getMonthlyExpenses();

        DashBoardStatsDto stats = DashBoardStatsDto.builder()
                .monthlyIncome(monthlyIncome.getData())
                .monthlyExpenses(monthlyExpenses.getData())
                .totalUser(totalUsers.getData())
                .totalSystemBalance(totalSystemBalance.getData())
                .build();

        GeneralResponseDto<DashBoardStatsDto> response = GeneralResponseDto.<DashBoardStatsDto>builder()
                .status("200")
                .message("Dashboard Successfull")
                .data(stats)
                .build();

        return response;
    }

}
