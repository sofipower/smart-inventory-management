package com.portfolio.smart_inventory.domain.user.controller;
// 로그인, 로그아웃, 토큰 재발급 API
import com.portfolio.smart_inventory.domain.user.dto.LoginRequest;
import com.portfolio.smart_inventory.domain.user.dto.SignupRequest;
import com.portfolio.smart_inventory.domain.user.dto.TokenResponse;
import com.portfolio.smart_inventory.domain.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
