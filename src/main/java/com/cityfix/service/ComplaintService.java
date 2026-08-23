package com.cityfix.service;

import com.cityfix.dto.*;
import com.cityfix.entity.Complaint;
import com.cityfix.entity.User;
import com.cityfix.exception.BadRequestException;
import com.cityfix.exception.ResourceNotFoundException;
import com.cityfix.exception.UnauthorizedException;
import com.cityfix.repository.ComplaintRepository;
import com.cityfix.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public synchronized String generateComplaintId() {
        int currentYear = Year.now().getValue();
        long count = complaintRepository.count() + 1;
        return String.format("CF-%d-%04d", currentYear, count);
    }

    public ComplaintResponse createComplaint(Long userId, ComplaintRequest req, MultipartFile imageFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Validate Category
        List<String> validCategories = Arrays.asList(
                "Pothole", "Garbage", "Streetlight", "Water Leakage", 
                "Drainage", "Road Damage", "Traffic Signal", "Public Toilet", 
                "Illegal Dumping", "Other"
        );
        if (!validCategories.contains(req.getCategory())) {
            throw new BadRequestException("Invalid civic issue category: " + req.getCategory());
        }

        // Validate Priority
        String priority = req.getPriority();
        if (priority == null || (!priority.equalsIgnoreCase("Low") && !priority.equalsIgnoreCase("Medium") && !priority.equalsIgnoreCase("High"))) {
            priority = "Medium";
        }

        // Store file if provided
        String imageFilename = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageFilename = fileStorageService.storeFile(imageFile);
        }

        Complaint complaint = new Complaint();
        complaint.setComplaintId(generateComplaintId());
        complaint.setUser(user);
        complaint.setTitle(req.getTitle().trim());
        complaint.setCategory(req.getCategory());
        complaint.setDescription(req.getDescription().trim());
        complaint.setLocation(req.getLocation().trim());
        complaint.setLatitude(req.getLatitude());
        complaint.setLongitude(req.getLongitude());
        complaint.setImageFilename(imageFilename);
        complaint.setPriority(priority);
        complaint.setStatus("Pending");

        Complaint saved = complaintRepository.save(complaint);
        return ComplaintResponse.fromEntity(saved);
    }

    public List<ComplaintResponse> getCitizenComplaints(Long userId, String search, String category, String status) {
        List<Complaint> list = complaintRepository.searchCitizenComplaints(userId, search, category, status);
        return list.stream().map(ComplaintResponse::fromEntity).collect(Collectors.toList());
    }

    public ComplaintResponse getComplaintForUser(String idOrCode, Long userId, String role) {
        Complaint complaint = findComplaintByIdOrCodeEntity(idOrCode);
        if (!"ADMIN".equals(role) && !complaint.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Access Denied: You do not have permission to view another citizen's complaint.");
        }
        return ComplaintResponse.fromEntity(complaint);
    }

    public ComplaintResponse getPublicTrackInfo(String idOrCode) {
        Complaint complaint = findComplaintByIdOrCodeEntity(idOrCode);
        ComplaintResponse resp = ComplaintResponse.fromEntity(complaint);
        // Mask citizen sensitive details for public track view
        if (resp.getCitizenName() != null && resp.getCitizenName().length() > 2) {
            resp.setCitizenName(resp.getCitizenName().charAt(0) + "***" + resp.getCitizenName().charAt(resp.getCitizenName().length() - 1));
        }
        resp.setCitizenEmail(null);
        resp.setCitizenPhone(null);
        return resp;
    }

    private Complaint findComplaintByIdOrCodeEntity(String idOrCode) {
        Complaint complaint;
        try {
            Long numericId = Long.parseLong(idOrCode);
            complaint = complaintRepository.findById(numericId)
                    .orElseGet(() -> complaintRepository.findByComplaintId(idOrCode).orElse(null));
        } catch (NumberFormatException e) {
            complaint = complaintRepository.findByComplaintId(idOrCode).orElse(null);
        }

        if (complaint == null) {
            throw new ResourceNotFoundException("Complaint not found with ID/Code: " + idOrCode);
        }
        return complaint;
    }

    public ComplaintResponse getComplaintByIdOrCode(String idOrCode) {
        Complaint complaint = findComplaintByIdOrCodeEntity(idOrCode);
        return ComplaintResponse.fromEntity(complaint);
    }

    public DashboardStatsResponse getCitizenDashboardStats(Long userId) {
        DashboardStatsResponse stats = new DashboardStatsResponse();
        stats.setTotalComplaints(complaintRepository.countByUserId(userId));
        stats.setPendingComplaints(complaintRepository.countByUserIdAndStatus(userId, "Pending"));
        stats.setAssignedComplaints(complaintRepository.countByUserIdAndStatus(userId, "Assigned"));
        stats.setInProgressComplaints(complaintRepository.countByUserIdAndStatus(userId, "In Progress"));
        stats.setResolvedComplaints(complaintRepository.countByUserIdAndStatus(userId, "Resolved"));
        stats.setRejectedComplaints(complaintRepository.countByUserIdAndStatus(userId, "Rejected"));

        List<Complaint> recent = complaintRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);
        stats.setRecentComplaints(recent.stream().map(ComplaintResponse::fromEntity).collect(Collectors.toList()));

        return stats;
    }

    public Map<String, Object> getPublicStats() {
        Map<String, Object> map = new HashMap<>();
        long total = complaintRepository.count();
        long resolved = complaintRepository.countByStatus("Resolved");
        long inProgress = complaintRepository.countByStatus("In Progress") + complaintRepository.countByStatus("Assigned");
        long citizens = userRepository.count();

        map.put("totalComplaints", total);
        map.put("resolvedComplaints", resolved);
        map.put("inProgressComplaints", inProgress);
        map.put("totalCitizens", citizens);
        return map;
    }

    public ComplaintResponse updateStatus(Long id, StatusUpdateRequest req) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with ID: " + id));

        List<String> validStatuses = Arrays.asList("Pending", "Assigned", "In Progress", "Resolved", "Rejected");
        if (!validStatuses.contains(req.getStatus())) {
            throw new BadRequestException("Invalid status: " + req.getStatus() + ". Valid values: " + validStatuses);
        }

        complaint.setStatus(req.getStatus());
        if (req.getAdminRemarks() != null) {
            complaint.setAdminRemarks(req.getAdminRemarks().trim());
        }

        Complaint updated = complaintRepository.save(complaint);
        return ComplaintResponse.fromEntity(updated);
    }

    public void deleteComplaint(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with ID: " + id));

        if (complaint.getImageFilename() != null) {
            fileStorageService.deleteFile(complaint.getImageFilename());
        }

        complaintRepository.delete(complaint);
    }
}
