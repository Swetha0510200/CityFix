package com.cityfix.controller;

import com.cityfix.dto.ApiResponse;
import com.cityfix.dto.ProfileUpdateRequest;
import com.cityfix.dto.UserResponse;
import com.cityfix.entity.User;
import com.cityfix.exception.UnauthorizedException;
import com.cityfix.repository.ComplaintRepository;
import com.cityfix.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private ComplaintRepository complaintRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) {
            throw new UnauthorizedException("Please log in to view profile.");
        }

        User user = userService.getUserById(userId);
        long count = complaintRepository.countByUserId(userId);
        return ResponseEntity.ok(ApiResponse.ok("Profile retrieved.", UserResponse.fromEntity(user, count)));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest req,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) {
            throw new UnauthorizedException("Please log in to update profile.");
        }

        User updatedUser = userService.updateProfile(userId, req);
        session.setAttribute("USER_NAME", updatedUser.getName());

        long count = complaintRepository.countByUserId(userId);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully.", UserResponse.fromEntity(updatedUser, count)));
    }
}
