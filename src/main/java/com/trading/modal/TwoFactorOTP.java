package com.trading.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class TwoFactorOTP{

	@Id
	private String id;
	private String otp;

	@JsonProperty(access = Access.WRITE_ONLY)
	@OneToOne
	private User user;

	@JsonProperty(access = Access.WRITE_ONLY)
	private String jwt;
}
