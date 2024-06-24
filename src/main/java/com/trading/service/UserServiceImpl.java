package com.trading.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trading.domain.VerificationType;
import com.trading.modal.TwoFactorAuth;
import com.trading.modal.User;
import com.trading.repository.UserRepository;
import com.trading.security.JwtProvider;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public User findUserByJwt(String jwt) throws Exception {
		String email = JwtProvider.getEmailFromJwtToken(jwt);
		User user = userRepository.findByEmail(email);

		if (user == null) {
			throw new Exception("User not found");
		}

		return user;
	}

	@Override
	public User findUserByEmial(String emial) throws Exception {
		User user = userRepository.findByEmail(emial);
		if (user == null) {
			throw new Exception("User not found");
		}
		return user;
	}

	@Override
	public User findUserById(Long id) throws Exception {
		return userRepository.findById(id)
				.orElseThrow(() -> new Exception("User not found with id :" + id));
	}

	@Override
	public User findUserByEmailAndPassword(String email, String password) throws Exception {
		User user = userRepository.findUserByEmailAndPassword(email, password);

		if (user == null) {
			throw new Exception("User not found with email: " + email);
		}
		return user;
	}

	@Override
	public User enableTwoFactorAuthentication(VerificationType verificationType, String sendTo, User user) {
		TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
		twoFactorAuth.setEnabled(true);
		twoFactorAuth.setSendTo(verificationType);
		user.setTwoFactorAuth(twoFactorAuth);
		return userRepository.save(user);
	}

	@Override
	public User updatePassword(User user, String newPassword) {
		user.setPassword(newPassword);
		return userRepository.save(user);
	}

}
