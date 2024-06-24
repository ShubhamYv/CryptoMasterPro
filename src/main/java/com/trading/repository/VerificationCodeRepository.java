package com.trading.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trading.modal.VerificationCode;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

	VerificationCode findByUserId(Long userId);
}
