package com.threepm.api.toko.Service.impl;

import com.threepm.api.toko.Model.Entity.Users;
import com.threepm.api.toko.Repository.UsersRepository;
import com.threepm.api.toko.Model.Request.LoginRequest;
import com.threepm.api.toko.Model.Response.LoginResponse;
import com.threepm.api.toko.Service.AuthService;
import com.threepm.api.toko.Util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("[LOGIN] Attempt login username={}", request.getUsername());

        Users user = usersRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        log.info("[LOGIN] Success username={}", user.getUsername());

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}