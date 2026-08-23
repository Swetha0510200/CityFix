package com.cityfix.dto;

import com.cityfix.entity.User;
import java.time.format.DateTimeFormatter;

public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String createdAt;
    private long complaintCount;

    public UserResponse() {
    }

    public static UserResponse fromEntity(User user, long complaintCount) {
        if (user == null) return null;
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setComplaintCount(complaintCount);
        if (user.getCreatedAt() != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
            dto.setCreatedAt(user.getCreatedAt().format(dtf));
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public long getComplaintCount() {
        return complaintCount;
    }

    public void setComplaintCount(long complaintCount) {
        this.complaintCount = complaintCount;
    }
}
