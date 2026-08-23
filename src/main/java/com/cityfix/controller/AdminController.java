package com.cityfix.controller;

import com.cityfix.dto.ApiResponse;
import com.cityfix.dto.ComplaintResponse;
import com.cityfix.dto.DashboardStatsResponse;
import com.cityfix.dto.StatusUpdateRequest;
import com.cityfix.dto.UserResponse;
import com.cityfix.service.AdminService;
import com.cityfix.service.ComplaintService;
import com.cityfix.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard-stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        DashboardStatsResponse stats = adminService.getAdminDashboardStats();
        return ResponseEntity.ok(ApiResponse.ok("Admin dashboard statistics retrieved.", stats));
    }

    @GetMapping("/complaints")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getAllComplaints(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "priority", required = false) String priority) {

        List<ComplaintResponse> list = adminService.searchComplaints(search, category, status, priority);
        return ResponseEntity.ok(ApiResponse.ok("Complaints retrieved successfully.", list));
    }

    @GetMapping("/complaints/{id}")
    public ResponseEntity<ApiResponse<ComplaintResponse>> getComplaint(@PathVariable("id") String id) {
        ComplaintResponse response = complaintService.getComplaintByIdOrCode(id);
        return ResponseEntity.ok(ApiResponse.ok("Complaint details retrieved.", response));
    }

    @PutMapping("/complaints/{id}/status")
    public ResponseEntity<ApiResponse<ComplaintResponse>> updateComplaintStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody StatusUpdateRequest req) {

        ComplaintResponse updated = complaintService.updateStatus(id, req);
        return ResponseEntity.ok(ApiResponse.ok("Complaint status updated successfully.", updated));
    }

    @DeleteMapping("/complaints/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComplaint(@PathVariable("id") Long id) {
        complaintService.deleteComplaint(id);
        return ResponseEntity.ok(ApiResponse.ok("Complaint deleted successfully."));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getRegisteredUsers() {
        List<UserResponse> users = userService.getAllUsersWithComplaintCounts();
        return ResponseEntity.ok(ApiResponse.ok("Citizens list retrieved.", users));
    }
}
