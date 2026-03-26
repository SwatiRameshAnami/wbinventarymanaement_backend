package com.stockflow.inventory.service.impl;

import com.stockflow.inventory.dto.request.LoginRequest;
import com.stockflow.inventory.dto.response.LoginResponse;
import com.stockflow.inventory.dto.response.UserResponse;
import com.stockflow.inventory.entity.User;
import com.stockflow.inventory.repository.UserRepository;
import com.stockflow.inventory.security.JwtService;
import com.stockflow.inventory.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository        userRepository;
    private final JwtService            jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        // Spring Security validates credentials — throws BadCredentialsException on failure
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        User user  = (User) authentication.getPrincipal();
        String jwt = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(jwt)
                .user(UserResponse.from(user))
                .build();
    }
}
