package com.trading.pojo.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
	private String jwt;
	private String message;
	private boolean status;
	private boolean isTwoFactorAuthEnabled;
	private String session;
}
