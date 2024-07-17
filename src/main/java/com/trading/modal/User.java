package com.trading.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.trading.domain.UserRole;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String fullName;
	private String email;
	private String mobile;

	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;

	private UserRole role = UserRole.ROLE_CUSTOMER;

	@Embedded
	private TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
}
