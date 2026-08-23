package com.cityfix.service;

import com.cityfix.dto.LoginRequest;
import com.cityfix.dto.RegisterRequest;
import com.cityfix.entity.Admin;
import com.cityfix.entity.User;
import com.cityfix.exception.BadRequestException;
import com.cityfix.exception.UnauthorizedException;
import com.cityfix.repository.AdminRepository;
import com.cityfix.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(RegisterRequest req) {
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new BadRequestException("Password and Confirm Password do not match.");
        }

        String email = req.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("An account with this email address already exists.");
        }

        User user = new User();
        user.setName(req.getName().trim());
        user.setEmail(email);
        user.setPhone(req.getPhone().trim());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole("CITIZEN");

        return userRepository.save(user);
    }

    public Map<String, Object> login(LoginRequest req, HttpSession session) {
        String email = req.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password."));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password.");
        }

        // Set session
        session.setAttribute("USER_ID", user.getId());
        session.setAttribute("USER_NAME", user.getName());
        session.setAttribute("USER_EMAIL", user.getEmail());
        session.setAttribute("ROLE", user.getRole());

        Map<String, Object> authData = new HashMap<>();
        authData.put("id", user.getId());
        authData.put("name", user.getName());
        authData.put("email", user.getEmail());
        authData.put("role", user.getRole());
        return authData;
    }

    public Map<String, Object> adminLogin(LoginRequest req, HttpSession session) {
        String username = req.getEmail().trim();
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Invalid admin credentials."));

        if (!passwordEncoder.matches(req.getPassword(), admin.getPasswordHash())) {
            throw new UnauthorizedException("Invalid admin credentials.");
        }

        // Set session
        session.setAttribute("USER_ID", admin.getId());
        session.setAttribute("USER_NAME", admin.getUsername());
        session.setAttribute("USER_EMAIL", admin.getUsername() + "@cityfix.gov");
        session.setAttribute("ROLE", "ADMIN");

        Map<String, Object> authData = new HashMap<>();
        authData.put("id", admin.getId());
        authData.put("username", admin.getUsername());
        authData.put("role", "ADMIN");
        return authData;
    }

    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    public Map<String, Object> getCurrentSessionUser(HttpSession session) {
        if (session == null || session.getAttribute("USER_ID") == null) {
            return null;
        }
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", session.getAttribute("USER_ID"));
        userMap.put("name", session.getAttribute("USER_NAME"));
        userMap.put("email", session.getAttribute("USER_EMAIL"));
        userMap.put("role", session.getAttribute("ROLE"));
        return userMap;
    }
}
