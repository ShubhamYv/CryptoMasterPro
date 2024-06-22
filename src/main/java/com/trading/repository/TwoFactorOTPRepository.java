package com.trading.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trading.modal.TwoFactorOTP;

public interface TwoFactorOTPRepository extends JpaRepository<TwoFactorOTP, String> {

	TwoFactorOTP findByUserId(Long userId);

}
