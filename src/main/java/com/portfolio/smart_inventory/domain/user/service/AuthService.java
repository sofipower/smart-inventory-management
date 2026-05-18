package com.portfolio.smart_inventory.domain.user.service;

import com.portfolio.smart_inventory.domain.user.dto.LoginRequest;
import com.portfolio.smart_inventory.domain.user.dto.SignupRequest;
import com.portfolio.smart_inventory.domain.user.dto.TokenResponse;
import com.portfolio.smart_inventory.domain.user.entity.User;
import com.portfolio.smart_inventory.domain.user.entity.UserRole;
import com.portfolio.smart_inventory.domain.user.repository.UserRepository;
import com.portfolio.smart_inventory.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .build();

        userRepository.save(user);
        log.info("[Auth] 회원가입 완료 - 사용자: {}", request.getUsername());
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());
        log.info("[Auth] 로그인 성공 - 사용자: {}", user.getUsername());

        return TokenResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationMs())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}