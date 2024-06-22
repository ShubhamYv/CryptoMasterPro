package com.trading.service;

import com.trading.pojo.request.LoginRequest;
import com.trading.pojo.request.UserRequest;
import com.trading.pojo.response.AuthResponse;

public interface AuthService {
    AuthResponse register(UserRequest request) throws Exception;
    AuthResponse signin(LoginRequest loginRequest) throws Exception;
}
