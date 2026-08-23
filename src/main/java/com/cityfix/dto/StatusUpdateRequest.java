package com.cityfix.dto;

import jakarta.validation.constraints.NotBlank;

public class StatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status;

    private String adminRemarks;

    public StatusUpdateRequest() {
    }

    public StatusUpdateRequest(String status, String adminRemarks) {
        this.status = status;
        this.adminRemarks = adminRemarks;
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
}
