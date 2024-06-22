package com.trading.controller;

import com.trading.modal.TwoFactorOTP;
import com.trading.pojo.request.LoginRequest;
import com.trading.pojo.request.UserRequest;
import com.trading.pojo.response.AuthResponse;
import com.trading.service.AuthService;
import com.trading.service.TwoFactorOTPService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthService authService;
	
	@Autowired
	TwoFactorOTPService twoFactorOTPService;

	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> register(@RequestBody UserRequest request) {
		try {
			AuthResponse response = authService.register(request);
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (Exception e) {
			AuthResponse authResponse = AuthResponse.builder().message(e.getMessage()).status(false).build();
			return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/signin")
	public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest loginRequest) {
		try {
			AuthResponse response = authService.signin(loginRequest);
			HttpStatus status = response.isTwoFactorAuthEnabled() ? HttpStatus.ACCEPTED : HttpStatus.OK;
			return new ResponseEntity<>(response, status);
		} catch (Exception e) {
			AuthResponse authResponse = AuthResponse.builder().message(e.getMessage()).status(false).build();
			return new ResponseEntity<>(authResponse, HttpStatus.UNAUTHORIZED);
		}
	}

	public ResponseEntity<AuthResponse> verifySignInOTP(@PathVariable String otp, @RequestParam String id) throws Exception {
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
}
