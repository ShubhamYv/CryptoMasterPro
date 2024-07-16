package com.trading.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trading.domain.VerificationType;
import com.trading.modal.ForgotPasswordToken;
import com.trading.modal.User;
import com.trading.repository.ForgotPasswordRepository;
import com.trading.service.ForgotPassword;

@Service
public class ForgotPasswordImpl implements ForgotPassword {

	@Autowired
	private ForgotPasswordRepository forgotPasswordRepository;

	@Override
	public ForgotPasswordToken createToken(User user, String id, String otp, VerificationType verificationType,
			String sendTo) {
		ForgotPasswordToken token = new ForgotPasswordToken();
		token.setId(id);
		token.setUser(user);
		token.setSendTo(sendTo);
		token.setVerificationType(verificationType);
		token.setOtp(otp);
		return forgotPasswordRepository.save(token);
	}

	@Override
	public ForgotPasswordToken findById(String id) throws Exception {
		return forgotPasswordRepository.findById(id).orElseThrow(() -> new Exception("Id not found"));
	}

	@Override
	public ForgotPasswordToken findByUser(Long userId) {
		return forgotPasswordRepository.findByUserId(userId);
	}

	@Override
	public void deleteToken(ForgotPasswordToken token) {
		forgotPasswordRepository.delete(token);
	}

}
