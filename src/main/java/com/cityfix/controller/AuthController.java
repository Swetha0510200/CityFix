package com.cityfix.controller;

import com.cityfix.dto.ApiResponse;
import com.cityfix.dto.LoginRequest;
import com.cityfix.dto.RegisterRequest;
import com.cityfix.entity.User;
import com.cityfix.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@Valid @RequestBody RegisterRequest req) {
        User user = authService.register(req);
        return ResponseEntity.ok(ApiResponse.ok("Registration successful! You can now log in.", Map.of("id", user.getId(), "email", user.getEmail())));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@Valid @RequestBody LoginRequest req, HttpSession session) {
        Map<String, Object> authData = authService.login(req, session);
        return ResponseEntity.ok(ApiResponse.ok("Login successful!", authData));
    }

    @PostMapping("/admin-login")
    public ResponseEntity<ApiResponse<Object>> adminLogin(@Valid @RequestBody LoginRequest req, HttpSession session) {
        Map<String, Object> authData = authService.adminLogin(req, session);
        return ResponseEntity.ok(ApiResponse.ok("Admin login successful!", authData));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully."));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Object>> getCurrentUser(HttpSession session) {
        Map<String, Object> user = authService.getCurrentSessionUser(session);
        if (user == null) {
            return ResponseEntity.ok(ApiResponse.ok("No active session.", null));
        }
        return ResponseEntity.ok(ApiResponse.ok("Active session found.", user));
    }
}
