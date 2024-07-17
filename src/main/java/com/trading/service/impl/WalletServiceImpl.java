package com.trading.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trading.domain.OrderType;
import com.trading.modal.Order;
import com.trading.modal.User;
import com.trading.modal.Wallet;
import com.trading.repository.WalletRepository;
import com.trading.service.WalletService;

@Service
public class WalletServiceImpl implements WalletService {

	@Autowired
	private WalletRepository walletRepository;

	@Override
	public Wallet getUserWallet(User user) {
		Wallet wallet = walletRepository.findByUserId(user.getId());
		if (wallet == null) {
			wallet = new Wallet();
			wallet.setUser(user);
		}
		return wallet;
	}

	@Override
	public Wallet addBalance(Wallet wallet, Long money) {
		BigDecimal balance = wallet.getBalance();
		BigDecimal newBalance = balance.add(BigDecimal.valueOf(money));
		wallet.setBalance(newBalance);
		return walletRepository.save(wallet);
	}

	@Override
	public Wallet findWalletById(Long id) throws Exception {
		return walletRepository.findById(id)
				.orElseThrow(() -> new Exception("Wallet not found with id::" + id));
	}

	@Override
	public Wallet walletToWalletTransfer(User sender, Wallet receiverWallet, Long amount) throws Exception {
		Wallet senderWallet = getUserWallet(sender);
		if (senderWallet.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
			throw new Exception("Insufficient balance");
		}
		BigDecimal senderBalance = senderWallet.getBalance().subtract(BigDecimal.valueOf(amount));
		senderWallet.setBalance(senderBalance);
		walletRepository.save(senderWallet);

		BigDecimal receiverBalance = receiverWallet.getBalance().add(BigDecimal.valueOf(amount));
		receiverWallet.setBalance(receiverBalance);
		walletRepository.save(receiverWallet);
		return senderWallet;
	}

	@Override
	public Wallet payOrderPayment(Order order, User user) throws Exception {
		Wallet userWallet = getUserWallet(user);
		if (order.getOrderType().equals(OrderType.BUY)) {
			BigDecimal newBalance = userWallet.getBalance().subtract(order.getPrice());
			if (newBalance.compareTo(order.getPrice()) < 0) {
				throw new Exception("Insufficient funds for this transaction");
			}
			userWallet.setBalance(newBalance);
		} else {
			BigDecimal newBalance = userWallet.getBalance().add(order.getPrice());
			userWallet.setBalance(newBalance);
		}
		walletRepository.save(userWallet);
		return userWallet;
	}

}
