package com.trading.service;

import com.trading.domain.VerificationType;
import com.trading.modal.User;
import com.trading.modal.VerificationCode;

public interface VerificationCodeService {

	VerificationCode sendVerificationCode(User user, VerificationType verificationType);

	VerificationCode getVerificationCodeById(Long id) throws Exception;

	VerificationCode getUserVerificationCodeByUser(Long userId);

	void deleteVerificationCodeById(Long id) throws Exception;
	
	boolean verifyOtp(String otp, VerificationCode verificationCode);
}
