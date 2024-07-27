package com.trading.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trading.modal.PaymentOrder;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long>{

}
