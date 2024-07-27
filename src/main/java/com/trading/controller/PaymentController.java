package com.trading.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trading.domain.PaymentMethod;
import com.trading.modal.PaymentOrder;
import com.trading.modal.User;
import com.trading.pojo.response.PaymentResponse;
import com.trading.service.PaymentOrderService;
import com.trading.service.UserService;

@RestController
@RequestMapping("/api")
public class PaymentController {

	@Autowired
	private UserService userService;

	@Autowired
	private PaymentOrderService paymentOrderService;

	@PostMapping("/payment/{paymentMethod}/amount/{amount}")
	public ResponseEntity<PaymentResponse> paymentHandler(@PathVariable PaymentMethod paymentMethod,
			@PathVariable Long amount, @RequestHeader("Authorization") String jwt) throws Exception {

		User user = userService.findUserByJwt(jwt);

		PaymentResponse paymentResponse;

		PaymentOrder paymentOrder = paymentOrderService.createOrder(user, amount, paymentMethod);

		if (paymentMethod.equals(PaymentMethod.RAZORPAY)) {
			paymentResponse = paymentOrderService.createRazorpayPaymentLink(user, amount);
		}

		else {
			paymentResponse = paymentOrderService.createStripePaymentLink(user, amount, paymentOrder.getId());
		}

		return new ResponseEntity<PaymentResponse>(paymentResponse, HttpStatus.CREATED);
	}
}
