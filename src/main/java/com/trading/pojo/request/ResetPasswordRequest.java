package com.trading.pojo.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {

	private String otp;
	private String password;
}
