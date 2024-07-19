package com.trading.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trading.domain.VerificationType;
import com.trading.modal.User;
import com.trading.modal.VerificationCode;
import com.trading.service.EmailService;
import com.trading.service.UserService;
import com.trading.service.VerificationCodeService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private VerificationCodeService verificationCodeService;

	@GetMapping("/profile")
	public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception {
		User user = userService.findUserByJwt(jwt);
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	@PatchMapping("/verification/{verificationType}/send-otp")
	public ResponseEntity<String> sendVerificationOtp(@RequestHeader("Authorization") String jwt,
			@PathVariable VerificationType verificationType) throws Exception {

		User user = userService.findUserByJwt(jwt);

		VerificationCode verificationCode = verificationCodeService.getUserVerificationCodeByUser(user.getId());
		if (verificationCode == null) {
			verificationCode = verificationCodeService.sendVerificationCode(user, verificationType);
		}

		if (verificationType.equals(VerificationType.EMAIL)) {
			emailService.sendVerificationOtpEmail(user.getEmail(), verificationCode.getOtp());
		}

		return new ResponseEntity<>("Verification OTP sent successfully...", HttpStatus.OK);
	}

	@PatchMapping("/enable-two-factor/verify-otp/{otp}")
	public ResponseEntity<User> enableTwoFactorAuthentication(@RequestHeader("Authorization") String jwt,
			@PathVariable String otp) throws Exception {
		User user = userService.findUserByJwt(jwt);

		VerificationCode verificationCode = verificationCodeService.getUserVerificationCodeByUser(user.getId());
		String sendTo = verificationCode.getVerificationType().equals(VerificationType.EMAIL)
				? verificationCode.getEmail()
				: verificationCode.getMobile();

		boolean isVerified = verificationCodeService.verifyOtp(otp, verificationCode);

		if (isVerified) {
			User updatedUser = userService.enableTwoFactorAuthentication(
					verificationCode.getVerificationType(), 
					sendTo,
					user);
			verificationCodeService.deleteVerificationCodeById(verificationCode.getId());
			return new ResponseEntity<User>(updatedUser, HttpStatus.OK);
		}
		throw new Exception("Wrong OTP");
	}
}
