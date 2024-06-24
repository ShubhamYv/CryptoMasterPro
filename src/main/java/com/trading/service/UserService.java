package com.trading.service;

import com.trading.domain.VerificationType;
import com.trading.modal.User;

public interface UserService {

	User findUserByJwt(String jwt) throws Exception;

	User findUserByEmial(String emial) throws Exception;

	User findUserById(Long id) throws Exception;

	User findUserByEmailAndPassword(String email, String password) throws Exception;

	User enableTwoFactorAuthentication(VerificationType verificationType, String sendTo, User user);

	User updatePassword(User user, String newPassword);
}
