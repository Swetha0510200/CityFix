package com.cityfix.controller;

import com.cityfix.dto.ApiResponse;
import com.cityfix.dto.ComplaintRequest;
import com.cityfix.dto.ComplaintResponse;
import com.cityfix.dto.DashboardStatsResponse;
import com.cityfix.exception.UnauthorizedException;
import com.cityfix.service.ComplaintService;
import com.cityfix.service.FileStorageService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponse<ComplaintResponse>> createComplaint(
            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam("location") String location,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam(value = "priority", defaultValue = "Medium") String priority,
            @RequestParam(value = "image", required = false) MultipartFile image,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) {
            throw new UnauthorizedException("You must be logged in to submit a complaint.");
        }

        ComplaintRequest req = new ComplaintRequest();
        req.setTitle(title);
        req.setCategory(category);
        req.setDescription(description);
        req.setLocation(location);
        req.setLatitude(latitude);
        req.setLongitude(longitude);
        req.setPriority(priority);

        ComplaintResponse created = complaintService.createComplaint(userId, req, image);
        return ResponseEntity.ok(ApiResponse.ok("Civic issue reported successfully with ID: " + created.getComplaintId(), created));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getMyComplaints(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "status", required = false) String status,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) {
            throw new UnauthorizedException("Please log in to view your complaints.");
        }

        List<ComplaintResponse> list = complaintService.getCitizenComplaints(userId, search, category, status);
        return ResponseEntity.ok(ApiResponse.ok("Complaints retrieved successfully.", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ComplaintResponse>> getComplaintDetails(@PathVariable("id") String id, HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        String role = (String) session.getAttribute("ROLE");
        if (userId == null) {
            throw new UnauthorizedException("Please log in to view complaint details.");
        }
        ComplaintResponse response = complaintService.getComplaintForUser(id, userId, role);
        return ResponseEntity.ok(ApiResponse.ok("Complaint details retrieved.", response));
    }

    @GetMapping("/track/{code}")
    public ResponseEntity<ApiResponse<ComplaintResponse>> trackComplaintPublic(@PathVariable("code") String code) {
        ComplaintResponse response = complaintService.getPublicTrackInfo(code);
        return ResponseEntity.ok(ApiResponse.ok("Tracking details retrieved successfully.", response));
    }

    @GetMapping("/public/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPublicStats() {
        Map<String, Object> stats = complaintService.getPublicStats();
        return ResponseEntity.ok(ApiResponse.ok("Public stats retrieved.", stats));
    }

    @GetMapping("/citizen/dashboard-stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getCitizenDashboardStats(HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) {
            throw new UnauthorizedException("Please log in to view dashboard statistics.");
        }

        DashboardStatsResponse stats = complaintService.getCitizenDashboardStats(userId);
        return ResponseEntity.ok(ApiResponse.ok("Dashboard stats retrieved.", stats));
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        Resource resource = fileStorageService.loadFileAsResource(filename);
        if (resource == null || !resource.exists()) {
            resource = new ClassPathResource("static/images/placeholder.png");
        }

        String contentType = "image/jpeg";
        try {
            if (resource.getFile().exists()) {
                contentType = Files.probeContentType(resource.getFile().toPath());
            }
        } catch (Exception ignored) {
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
