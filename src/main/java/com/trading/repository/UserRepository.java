package com.trading.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trading.modal.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);
	User findUserByEmailAndPassword(String email, String password);
}
