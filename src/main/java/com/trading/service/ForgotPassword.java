package com.trading.service;

import com.trading.domain.VerificationType;
import com.trading.modal.ForgotPasswordToken;
import com.trading.modal.User;

public interface ForgotPassword {

	ForgotPasswordToken createToken(User user, String id, String otp, VerificationType verificationType, String sendTo);

	ForgotPasswordToken findById(String id) throws Exception;

	ForgotPasswordToken findByUser(Long userId);

	void deleteToken(ForgotPasswordToken token);
}
