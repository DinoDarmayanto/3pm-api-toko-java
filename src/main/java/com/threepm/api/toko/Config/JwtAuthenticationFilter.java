package com.threepm.api.toko.Config;

import com.threepm.api.toko.Util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");

        log.info("[JWT_FILTER] path={}", path);
        log.info("[JWT_FILTER] Authorization header={}", authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("[JWT_FILTER] Missing or invalid Authorization header");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        log.info("[JWT_FILTER] token starts with={}",
                token.length() > 15 ? token.substring(0, 15) : token);

        boolean valid = jwtUtil.validateToken(token);
        log.info("[JWT_FILTER] token valid={}", valid);

        if (valid) {
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);

            log.info("[JWT_FILTER] username={} role={}", username, role);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    List.of(new SimpleGrantedAuthority(role)));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("[JWT_FILTER] Authentication set successfully");
        } else {
            log.error("[JWT_FILTER] Invalid JWT token");
        }

        filterChain.doFilter(request, response);
    }
}