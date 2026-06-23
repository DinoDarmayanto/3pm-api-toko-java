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

        log.info("[LOGIN] Attempt username={}", request.getUsername());

        Users user = usersRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.error("[LOGIN] User not found username={}", request.getUsername());
                    return new BadCredentialsException("Invalid username or password");
                });

        log.info("[LOGIN] User found username={} role={}",
                user.getUsername(),
                user.getRole());

        boolean match = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword());

        log.info("[LOGIN] Password match result={}", match);

        if (!match) {
            log.error("[LOGIN] Invalid password username={}", user.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }

        log.info("[LOGIN] Generate JWT");

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole());

        log.info("[LOGIN] JWT Generated");

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}