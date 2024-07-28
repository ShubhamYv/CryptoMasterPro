package com.trading.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trading.modal.Order;
import com.trading.modal.PaymentOrder;
import com.trading.modal.User;
import com.trading.modal.Wallet;
import com.trading.modal.WalletTransaction;
import com.trading.service.OrderService;
import com.trading.service.PaymentOrderService;
import com.trading.service.UserService;
import com.trading.service.WalletService;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

	@Autowired
	private WalletService walletService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private PaymentOrderService paymentOrderService;

	@GetMapping()
	public ResponseEntity<Wallet> getUserWallet(@RequestHeader("Authorization") String jwt) throws Exception {
		User user = userService.findUserByJwt(jwt);
		Wallet wallet = walletService.getUserWallet(user);
		return new ResponseEntity<Wallet>(wallet, HttpStatus.ACCEPTED);
	}

	@PutMapping("/{walletId}/transfer")
	public ResponseEntity<Wallet> walletToWalletTransfer(@RequestHeader("Authorization") String jwt,
			@PathVariable Long walletId, @RequestBody WalletTransaction request) throws Exception {
		User senderUser = userService.findUserByJwt(jwt);
		Wallet receiverWallet = walletService.findWalletById(walletId);
		Wallet wallet = walletService.walletToWalletTransfer(senderUser, receiverWallet, request.getAmount());
		return new ResponseEntity<Wallet>(wallet, HttpStatus.ACCEPTED);
	}

	@PutMapping("/order/{orderId}/pay")
	public ResponseEntity<Wallet> payOrderPayment(@RequestHeader("Authorization") String jwt,
			@PathVariable Long orderId) throws Exception {
		User senderUser = userService.findUserByJwt(jwt);
		Order order = orderService.getOrderById(orderId);
		Wallet wallet = walletService.payOrderPayment(order, senderUser);
		return new ResponseEntity<Wallet>(wallet, HttpStatus.ACCEPTED);
	}
	
	@PutMapping("/deposite")
	public ResponseEntity<Wallet> addBalanceToWallet(@RequestHeader("Authorization") String jwt,
			@RequestParam(name = "order_id") Long orderId,
			@RequestParam(name = "payment_id") String paymentId) throws Exception {
		
		User user = userService.findUserByJwt(jwt);
		
		Wallet wallet = walletService.getUserWallet(user);
		
		PaymentOrder order = paymentOrderService.getPaymentOrderById(orderId);
		Boolean status = paymentOrderService.proceedPaymentOrder(order, paymentId);
		
		if (wallet.getBalance() == null) {
			wallet.setBalance(BigDecimal.valueOf(0));
		}
		
		if (status) {
			wallet = walletService.addBalance(wallet, order.getAmount());
		}
		
		return new ResponseEntity<Wallet>(wallet, HttpStatus.ACCEPTED);
	}
}
