package com.trading.service;

import com.trading.modal.PaymentDetails;
import com.trading.modal.User;

public interface PaymentDetailsService {

	PaymentDetails addPaymentDetails(String accountNumber, String accountHolderName, 
			String ifsc, String bankName, User user);
	
	PaymentDetails getUsersPaymentDetails(User user);
}
