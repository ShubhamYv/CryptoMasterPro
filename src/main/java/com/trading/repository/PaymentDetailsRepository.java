package com.trading.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trading.modal.PaymentDetails;

public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails, Long> {

	PaymentDetails findByUserId(Long userId);
}
