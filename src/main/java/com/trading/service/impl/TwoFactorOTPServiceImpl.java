package com.trading.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trading.modal.TwoFactorOTP;
import com.trading.modal.User;
import com.trading.repository.TwoFactorOTPRepository;
import com.trading.service.TwoFactorOTPService;

@Service
public class TwoFactorOTPServiceImpl implements TwoFactorOTPService {

	@Autowired
	private TwoFactorOTPRepository twoFactorOTPRepository;

	@Override
	public TwoFactorOTP createTwoFactorOTP(User user, String otp, String jwt) {
		String id = UUID.randomUUID().toString();

		TwoFactorOTP twoFactorOTP = new TwoFactorOTP();
		twoFactorOTP.setId(id);
		twoFactorOTP.setOtp(otp);
		twoFactorOTP.setJwt(jwt);
		twoFactorOTP.setUser(user);

		return twoFactorOTPRepository.save(twoFactorOTP);
	}

	@Override
	public TwoFactorOTP findByUser(Long userId) {
		return twoFactorOTPRepository.findByUserId(userId);
	}

	@Override
	public TwoFactorOTP findById(String id) {
		return twoFactorOTPRepository.findById(id).orElse(null);
	}

	@Override
	public boolean verifyTwoFactorOTP(TwoFactorOTP twoFactorOTP, String otp) {
		return twoFactorOTP.getOtp().equals(otp);
	}

	@Override
	public void deleteTwoFactorOTP(TwoFactorOTP twoFactorOTP) {
		twoFactorOTPRepository.delete(twoFactorOTP);
	}

}
