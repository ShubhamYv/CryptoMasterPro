package com.trading.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trading.domain.VerificationType;
import com.trading.modal.User;
import com.trading.modal.VerificationCode;
import com.trading.repository.VerificationCodeRepository;
import com.trading.service.VerificationCodeService;
import com.trading.utils.OtpUtils;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

	@Autowired
	private VerificationCodeRepository verificationCodeRepository;

	@Override
	public VerificationCode sendVerificationCode(User user, VerificationType verificationType) {
		VerificationCode newVerificationCode = new VerificationCode();
		newVerificationCode.setOtp(OtpUtils.generateOtp());
		newVerificationCode.setVerificationType(verificationType);
		newVerificationCode.setUser(user);
		return verificationCodeRepository.save(newVerificationCode);
	}

	@Override
	public VerificationCode getVerificationCodeById(Long id) throws Exception {
		return verificationCodeRepository.findById(id)
				.orElseThrow(() -> new Exception("Verification code not found"));
	}

	@Override
	public VerificationCode getUserVerificationCodeByUser(Long userId) {
		return verificationCodeRepository.findByUserId(userId);
	}

	@Override
	public void deleteVerificationCodeById(Long id) throws Exception {
		VerificationCode verificationCode = getVerificationCodeById(id);
		verificationCodeRepository.delete(verificationCode);
	}

	@Override
	public boolean verifyOtp(String otp, VerificationCode verificationCode) {
		return verificationCode.getOtp().equals(otp);
	}

}
