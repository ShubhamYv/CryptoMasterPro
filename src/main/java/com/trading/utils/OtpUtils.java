package com.trading.utils;

import java.security.SecureRandom;

public class OtpUtils {

    public static String generateOtp() {
        int otpLength = 6;
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(otpLength);

        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
    }

}
