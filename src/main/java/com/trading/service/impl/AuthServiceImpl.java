package com.trading.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.trading.modal.TwoFactorOTP;
import com.trading.modal.User;
import com.trading.pojo.request.LoginRequest;
import com.trading.pojo.request.UserRequest;
import com.trading.pojo.response.AuthResponse;
import com.trading.repository.UserRepository;
import com.trading.security.JwtProvider;
import com.trading.service.AuthService;
import com.trading.service.CustomUserDetailsService;
import com.trading.service.EmailService;
import com.trading.service.TwoFactorOTPService;
import com.trading.utils.OtpUtils;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TwoFactorOTPService twoFactorOTPService;
    
    @Autowired
    private EmailService emailService;

    public AuthResponse register(UserRequest request) throws Exception {
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

        Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getEmail(),
                savedUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = JwtProvider.generateToken(authentication);

        return AuthResponse.builder()
                .jwt(jwt)
                .message("Signup Successful")
                .status(true)
                .build();
    }

    public AuthResponse signin(LoginRequest loginRequest) throws Exception {
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = JwtProvider.generateToken(authentication);

        User user = userRepository.findByEmail(username);

        if (user.getTwoFactorAuth().isEnabled()) {
            String otp = OtpUtils.generateOtp();
            
            TwoFactorOTP oldTwoFactorOtp = twoFactorOTPService.findByUser(user.getId());
            if (oldTwoFactorOtp != null) {
                twoFactorOTPService.deleteTwoFactorOTP(oldTwoFactorOtp);
            }
            
            TwoFactorOTP newTwoFactorOTP = twoFactorOTPService.createTwoFactorOTP(user, otp, jwt);
            emailService.sendVerificationOtpEmail(username, otp);
            
            return AuthResponse.builder()
                    .message("Two factor auth is enabled.")
                    .isTwoFactorAuthEnabled(true)
                    .session(newTwoFactorOTP.getId())
                    .build();
        }

        return AuthResponse.builder()
                .jwt(jwt)
                .message("SignIn Successful")
                .build();
    }

    private Authentication authenticate(String username, String password) throws Exception {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        if (userDetails == null || !passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new Exception("Invalid Credential");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
