package com.trading.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trading.modal.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long>{
	
	Wallet findByUserId(Long userId);
}
