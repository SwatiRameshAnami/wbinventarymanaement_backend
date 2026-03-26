package com.stockflow.inventory.service;

import com.stockflow.inventory.dto.request.LoginRequest;
import com.stockflow.inventory.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
