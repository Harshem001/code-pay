package com.example.CodePay.service;

import com.example.CodePay.dto.*;
import com.example.CodePay.entity.Transaction;
import com.example.CodePay.entity.User;
import com.example.CodePay.enums.Status;
import com.example.CodePay.enums.TransactionEntry;
import com.example.CodePay.enums.TransactionType;
import com.example.CodePay.enums.UserStatus;
import com.example.CodePay.exception.EmailNotFoundException;
import com.example.CodePay.repo.TransactionRepository;
import com.example.CodePay.repo.UserRepository;
import com.example.CodePay.repo.WalletRepository;
import com.example.CodePay.security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    private final Wallet_to_WalletService wallet_to_WalletService;


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
                        user.getUserStatus()
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
                .userStatus(user.getUserStatus())
                .build();

        GeneralResponseDto<RegisterUserResponse> userProfile = GeneralResponseDto.<RegisterUserResponse>builder()
                .status("200")
                .message("User profile")
                .data(response)
                .build();
        return userProfile;
    }

    public ResponseEntity<GeneralResponseDto<String>> deleteUserByEmail(UserPrincipal userPrincipal, DeleteUserRequest request) {
        var user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));

        if (user.getEmail().equals(request.getEmail())) {
            throw new IllegalArgumentException("You cannot delete yourself");
        }

        User userToDelete = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () ->  new EmailNotFoundException());

        userRepository.delete(userToDelete);

        GeneralResponseDto<String> response = GeneralResponseDto.<String>builder()
                .status("200")
                .message("You've Successfully deleted the user with " + request.getEmail())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }


    public GeneralResponseDto<TransactionDto> getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Transaction with id " + id + " not found"));

        TransactionDto transactionDto = TransactionDto.builder()
                .senderName(transaction.getWallet().getUser().getFullName())
                .transactionId(transaction.getId())
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType())
                .entry(transaction.getTransactionEntry())
                .status(transaction.getStatus())
                .date(transaction.getDate())
                .build();
        GeneralResponseDto<TransactionDto> response = GeneralResponseDto.<TransactionDto>builder()
                .status("200")
                .message("Successful")
                .data(transactionDto)
                .build();
        return response;
    }

    public GeneralResponseDto<Map<String, Object>> getListOfTransactions(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactionPage = transactionRepository.findAll(pageable);

        List<TransactionDto> transactionList = transactionPage.getContent()
                .stream()
                .map(transaction -> new TransactionDto(
                        transaction.getWallet().getUser().getFullName(),
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
    public GeneralResponseDto<AccountDetailsDto> getAccountDetails(Long Id) {
        User user = userRepository.findById(Id).orElseThrow(
                ()-> new EntityNotFoundException("User not found"));

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
        AccountDetailsDto details = AccountDetailsDto.builder()
                .accountName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .accountEmail(user.getEmail())
                .userStatus(user.getUserStatus())
                .balance(user.getWallet().getBalance())
                .transactionHistory(transactionHistory)
                .build();
        GeneralResponseDto<AccountDetailsDto> response = GeneralResponseDto.<AccountDetailsDto>builder()
                .status("200")
                .message("Successful")
                .data(details)
                .build();
        return response;

    }
    public GeneralResponseDto<String> changeUserStatus(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User with id " + id + " not found"));
        if (user.getUserStatus() == UserStatus.ACTIVE) {
            user.setUserStatus(UserStatus.INACTIVE);
        }else
            user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        GeneralResponseDto<String> response = GeneralResponseDto.<String>builder()
                .status("200")
                .message("User Status Chnaged Successfully")
                .data("New Status " + user.getUserStatus())
                .build();
        return response;
    }

    // ADMIN DASHBOARD

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
       BigDecimal monthlyExpenses = getMonthlyExpenses().getData();
       BigDecimal monthlyIncome = getMonthlyIncome().getData();

       BigDecimal totalBalance = monthlyIncome.subtract(monthlyExpenses);
       GeneralResponseDto<BigDecimal> getTotalBalanceResponse = GeneralResponseDto.<BigDecimal>builder()
               .status("200")
               .message("Total Balance gotten Successful")
               .data(totalBalance)
               .build();
       return getTotalBalanceResponse;
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


    //FINANCIAL REPORT

    private Long getTotalTransactions() {
        return transactionRepository.count();
    }
    private Long getSuccessfulTransactions() {
        return transactionRepository.countByStatus(Status.SUCCESSFUL);
    }
    private Long getFailedTransactions() {
        return transactionRepository.countByStatus(Status.FAILED);
    }
    private Long getPendingTransactions() {
        return transactionRepository.countByStatus(Status.PENDING);
    }

    private Long getActiveAccount(){
        return userRepository.countByUserStatus(UserStatus.ACTIVE);
    }
    private Long getInactiveAccount(){
        return userRepository.countByUserStatus(UserStatus.INACTIVE);
    }
    private BigDecimal getTotalIncome(){
        return transactionRepository.calculateTotalIncome(
                TransactionType.DEPOSIT,
                TransactionEntry.CREDITED);
    }

    public GeneralResponseDto<FinancialReportDto> getFinancialReport (){
        FinancialReportDto report = FinancialReportDto.builder()
                .totalTransaction(getTotalTransactions())
                .successfulTransaction(getSuccessfulTransactions())
                .pendingTransaction(getPendingTransactions())
                .failedTransaction(getFailedTransactions())
                .totalBalance(getTotalSystemBalance().getData())
                .totalIncome(getTotalIncome())
                .activeAccount(getActiveAccount())
                .inactiveAccount(getInactiveAccount())
                .build();

        return GeneralResponseDto.<FinancialReportDto>builder()
                .status("200")
                .message("Financial Report Gotten Successfully")
                .data(report)
                .build();
    }
}
