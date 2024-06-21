package com.trading.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.trading.modal.User;
import com.trading.repository.UserRepository;
import com.trading.service.CustomUserDetailsService;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email);
		if (null == user) {
			throw new UsernameNotFoundException(email);
		}

		List<GrantedAuthority> authorityList = new ArrayList<>();
		return new org.springframework.security.core.userdetails.User(
				user.getEmail(), 
				user.getPassword(),
				authorityList);
	}

}
