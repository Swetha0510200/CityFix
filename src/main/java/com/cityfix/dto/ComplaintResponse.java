package com.cityfix.dto;

import com.cityfix.entity.Complaint;
import java.time.format.DateTimeFormatter;

public class ComplaintResponse {

    private Long id;
    private String complaintId;
    private String title;
    private String category;
    private String description;
    private String location;
    private Double latitude;
    private Double longitude;
    private String imageFilename;
    private String imageUrl;
    private String priority;
    private String status;
    private String adminRemarks;
    private String createdAt;
    private String updatedAt;

    // Citizen Info
    private Long citizenId;
    private String citizenName;
    private String citizenEmail;
    private String citizenPhone;

    public ComplaintResponse() {
    }

    public static ComplaintResponse fromEntity(Complaint complaint) {
        if (complaint == null) return null;
        ComplaintResponse dto = new ComplaintResponse();
        dto.setId(complaint.getId());
        dto.setComplaintId(complaint.getComplaintId());
        dto.setTitle(complaint.getTitle());
        dto.setCategory(complaint.getCategory());
        dto.setDescription(complaint.getDescription());
        dto.setLocation(complaint.getLocation());
        dto.setLatitude(complaint.getLatitude());
        dto.setLongitude(complaint.getLongitude());
        dto.setImageFilename(complaint.getImageFilename());
        
        if (complaint.getImageFilename() != null && !complaint.getImageFilename().trim().isEmpty()) {
            dto.setImageUrl("/api/complaints/images/" + complaint.getImageFilename());
        } else {
            dto.setImageUrl("/images/placeholder.png");
        }

        dto.setPriority(complaint.getPriority());
        dto.setStatus(complaint.getStatus());
        dto.setAdminRemarks(complaint.getAdminRemarks());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        if (complaint.getCreatedAt() != null) {
            dto.setCreatedAt(complaint.getCreatedAt().format(dtf));
        }
        if (complaint.getUpdatedAt() != null) {
            dto.setUpdatedAt(complaint.getUpdatedAt().format(dtf));
        }

        if (complaint.getUser() != null) {
            dto.setCitizenId(complaint.getUser().getId());
            dto.setCitizenName(complaint.getUser().getName());
            dto.setCitizenEmail(complaint.getUser().getEmail());
            dto.setCitizenPhone(complaint.getUser().getPhone());
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getImageFilename() {
        return imageFilename;
    }

    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdminRemarks() {
        return adminRemarks;
    }

    public void setAdminRemarks(String adminRemarks) {
        this.adminRemarks = adminRemarks;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(Long citizenId) {
        this.citizenId = citizenId;
    }

    public String getCitizenName() {
        return citizenName;
    }

    public void setCitizenName(String citizenName) {
        this.citizenName = citizenName;
    }

    public String getCitizenEmail() {
        return citizenEmail;
    }

    public void setCitizenEmail(String citizenEmail) {
        this.citizenEmail = citizenEmail;
    }

    public String getCitizenPhone() {
        return citizenPhone;
    }

    public void setCitizenPhone(String citizenPhone) {
        this.citizenPhone = citizenPhone;
    }
}
