package com.cityfix.service;

import com.cityfix.dto.ProfileUpdateRequest;
import com.cityfix.dto.UserResponse;
import com.cityfix.entity.User;
import com.cityfix.exception.BadRequestException;
import com.cityfix.exception.ResourceNotFoundException;
import com.cityfix.repository.ComplaintRepository;
import com.cityfix.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    public User updateProfile(Long userId, ProfileUpdateRequest req) {
        User user = getUserById(userId);

        user.setName(req.getName().trim());
        user.setPhone(req.getPhone().trim());

        // Password change logic if requested
        if (req.getNewPassword() != null && !req.getNewPassword().trim().isEmpty()) {
            if (req.getCurrentPassword() == null || req.getCurrentPassword().trim().isEmpty()) {
                throw new BadRequestException("Current password is required to change your password.");
            }
            if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPasswordHash())) {
                throw new BadRequestException("Current password does not match.");
            }
            if (req.getNewPassword().length() < 6) {
                throw new BadRequestException("New password must be at least 6 characters long.");
            }
            if (!req.getNewPassword().equals(req.getConfirmPassword())) {
                throw new BadRequestException("New password and confirmation do not match.");
            }
            user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        }

        return userRepository.save(user);
    }

    public List<UserResponse> getAllUsersWithComplaintCounts() {
        List<User> users = userRepository.findAll();
        List<UserResponse> list = new ArrayList<>();
        for (User u : users) {
            long count = complaintRepository.countByUserId(u.getId());
            list.add(UserResponse.fromEntity(u, count));
        }
        return list;
    }
}
