package com.trading.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trading.domain.VerificationType;
import com.trading.modal.ForgotPasswordToken;
import com.trading.modal.TwoFactorOTP;
import com.trading.modal.User;
import com.trading.pojo.request.ForgotPasswordTokenRequest;
import com.trading.pojo.request.LoginRequest;
import com.trading.pojo.request.ResetPasswordRequest;
import com.trading.pojo.request.UserRequest;
import com.trading.pojo.response.AuthResponse;
import com.trading.service.AuthService;
import com.trading.service.EmailService;
import com.trading.service.ForgotPassword;
import com.trading.service.TwoFactorOTPService;
import com.trading.service.UserService;
import com.trading.utils.OtpUtils;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	@Autowired
	private TwoFactorOTPService twoFactorOTPService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ForgotPassword forgotPassword;
	
	@Autowired
	private EmailService emailService;

	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> register(@RequestBody UserRequest request) {
		try {
			AuthResponse response = authService.register(request);
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (Exception e) {
			AuthResponse authResponse = AuthResponse.builder()
					.message(e.getMessage())
					.status(false)
					.build();
			return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/signin")
	public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest loginRequest) {
		try {
			AuthResponse response = authService.signin(loginRequest);
			HttpStatus status = response.isTwoFactorAuthEnabled() 
							? HttpStatus.ACCEPTED 
							: HttpStatus.OK;
			return new ResponseEntity<>(response, status);
		} catch (Exception e) {
			AuthResponse authResponse = AuthResponse.builder()
					.message(e.getMessage())
					.status(false)
					.build();
			return new ResponseEntity<>(authResponse, HttpStatus.UNAUTHORIZED);
		}
	}

	@PostMapping("/two-factor/otp/{otp}")
	public ResponseEntity<AuthResponse> verifySignInOTP(@PathVariable String otp, @RequestParam String id)
			throws Exception {
		TwoFactorOTP twoFactorOTP = twoFactorOTPService.findById(id);
		if (twoFactorOTPService.verifyTwoFactorOTP(twoFactorOTP, otp)) {
			AuthResponse response = AuthResponse.builder()
					.message("Two factor auth verifies")
					.isTwoFactorAuthEnabled(true)
					.jwt(twoFactorOTP.getJwt())
					.build();
			return new ResponseEntity<AuthResponse>(response, HttpStatus.OK);
		}
		throw new Exception("Invalid OTP");
	}

	@PostMapping("/users/reset-password/send-otp")
	public ResponseEntity<AuthResponse> sendForgotPasswordOtp(@RequestBody ForgotPasswordTokenRequest request) throws Exception {
	
		User user = userService.findUserByEmial(request.getSendTo());
		String otp = OtpUtils.generateOtp();
		String id = UUID.randomUUID().toString();
		
		ForgotPasswordToken token = forgotPassword.findByUser(user.getId());
		
		if(token== null) {
			token = forgotPassword.createToken(user, id, otp, 
					request.getVerificationType(),
					request.getSendTo());
		}
		
		if (request.getVerificationType().equals(VerificationType.EMAIL)) {
			emailService.sendVerificationOtpEmail(
					user.getEmail(), 
					token.getOtp());
		}
		
		AuthResponse response = AuthResponse.builder()
				.session(token.getId())
				.message("Forgot password OTP sent successfully...")
				.build();
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PatchMapping("/users/reset-password/verify-otp")
	public ResponseEntity<String> verifyResetPasswordOtp(@RequestParam String id, 
			@RequestBody ResetPasswordRequest request) throws Exception {

		ForgotPasswordToken forgotPasswordToken = forgotPassword.findById(id);
		boolean isOtpVerified = forgotPasswordToken.getOtp().equals(request.getOtp());
		if (isOtpVerified) {
			userService.updatePassword(forgotPasswordToken.getUser(), request.getPassword());
			return new ResponseEntity<>("Password updated successfully.", HttpStatus.ACCEPTED);
		}
		throw new Exception("Wrong OTP");
	}
}
