package com.trading.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trading.modal.User;
import com.trading.pojo.request.LoginRequest;
import com.trading.pojo.request.UserRequest;
import com.trading.pojo.response.AuthResponse;
import com.trading.repository.UserRepository;
import com.trading.security.JwtProvider;
import com.trading.service.CustomUserDetailsService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> register(@RequestBody UserRequest request) throws Exception {
		User isEmailExist = userRepository.findByEmail(request.getEmail());
		if (null != isEmailExist) {
			throw new Exception("Email is already used with another account");
		}

		User user = new User();
		user.setEmail(request.getEmail());
		user.setFullName(request.getFullName());
		user.setMobile(request.getMobile());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		
		User savedUser = userRepository.save(user);
		

		Authentication authentication = new UsernamePasswordAuthenticationToken(
				user.getEmail(), 
				user.getPassword());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String jwt = JwtProvider.generateToken(authentication);

		AuthResponse authResponse = AuthResponse.builder()
				.jwt(jwt)
				.message("Signup Successful")
				.status(true)
				.build();
		
		return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
	}
	
	
	@PostMapping("/signin")
	public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest loginRequest) throws Exception {
		String username = loginRequest.getEmail();
		String password = loginRequest.getPassword();
		Authentication authentication = authenticate(username, password);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = JwtProvider.generateToken(authentication);

		AuthResponse authResponse = AuthResponse.builder()
				.jwt(jwt)
				.message("SignIn Successful")
				.build();
		return new ResponseEntity<>(authResponse, HttpStatus.OK);
	}
	
	private Authentication authenticate(String username, String password) throws Exception {
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
		if (userDetails == null 
				|| !passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new Exception("Invalid Credential");
		}
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}
}
