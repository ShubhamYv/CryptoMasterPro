package com.trading.pojo.request;

import com.trading.domain.VerificationType;

import lombok.Data;

@Data
public class ForgotPasswordTokenRequest {

	private String sendTo;
	private VerificationType verificationType;
}
