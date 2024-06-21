package com.trading.pojo.request;

import lombok.Data;

@Data
public class LoginRequest {
	private String email;
	private String password;
}
