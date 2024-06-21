package com.trading.modal;

import com.trading.domain.VerificationType;

import lombok.Data;

@Data
public class TwoFactorAuth {
	private boolean isEnabled = false;
	private VerificationType sendTo;
}
