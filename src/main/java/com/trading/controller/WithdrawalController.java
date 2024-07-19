package com.trading.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.trading.modal.User;
import com.trading.modal.Wallet;
import com.trading.modal.Withdrawal;
import com.trading.service.UserService;
import com.trading.service.WalletService;
import com.trading.service.WithdrawalService;

@RestController
public class WithdrawalController {

    @Autowired
    private WithdrawalService withdrawalService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @PostMapping("/api/withdrawal/{amount}")
    public ResponseEntity<Withdrawal> withdrawalRequest(@PathVariable Long amount, 
    		@RequestHeader("Authorization") String jwt) throws Exception {
    	
        User user = userService.findUserByJwt(jwt);
        Wallet wallet = walletService.getUserWallet(user);
        Withdrawal withdrawal = withdrawalService.requestWithdrawal(amount, user);
        walletService.addBalance(wallet, -withdrawal.getAmount());
        return new ResponseEntity<>(withdrawal, HttpStatus.CREATED);
    }

    @PatchMapping("/api/admin/withdrawal/{id}/proceed/{accept}")
    public ResponseEntity<Withdrawal> proceedWithdrawal(@PathVariable Long id, 
    		@PathVariable boolean accept, 
    		@RequestHeader("Authorization") String jwt) throws Exception {
    	
        userService.findUserByJwt(jwt);
        Withdrawal withdrawal = withdrawalService.proceedWithWithdrawal(id, accept);
        if (!accept) {
            Wallet wallet = walletService.getUserWallet(withdrawal.getUser());
            walletService.addBalance(wallet, withdrawal.getAmount());
        }
        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

    @GetMapping("/api/withdrawal")
    public ResponseEntity<List<Withdrawal>> getWithdrawalHistory(
    		@RequestHeader("Authorization") String jwt) throws Exception {
    	
        User user = userService.findUserByJwt(jwt);
        List<Withdrawal> withdrawalHistory = withdrawalService.getUsersWithdrawalHistory(user);
        return new ResponseEntity<>(withdrawalHistory, HttpStatus.OK);
    }

    @GetMapping("/api/admin/withdrawal")
    public ResponseEntity<List<Withdrawal>> getAllWithdrawalRequests(
    		@RequestHeader("Authorization") String jwt) throws Exception {
    	
        userService.findUserByJwt(jwt);
        List<Withdrawal> allWithdrawalRequests = withdrawalService.getAllWithdrawalRequests();
        return new ResponseEntity<>(allWithdrawalRequests, HttpStatus.OK);
    }
}
