package com.trading.service;

import com.razorpay.RazorpayException;
import com.trading.domain.PaymentMethod;
import com.trading.modal.PaymentOrder;
import com.trading.modal.User;
import com.trading.pojo.response.PaymentResponse;

public interface PaymentOrderService {

	PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod);
	
	PaymentOrder getPaymentOrderById(Long paymentOrderId) throws Exception;
	
	Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentOrderId) throws RazorpayException;
	
	PaymentResponse createRazorpayPaymentLink(User user, Long amount) throws RazorpayException;
	
	PaymentResponse createStripePaymentLink(User user, Long amount, Long orderId);
}
