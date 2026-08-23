package com.cityfix.service;

import com.cityfix.dto.ComplaintResponse;
import com.cityfix.dto.DashboardStatsResponse;
import com.cityfix.entity.Complaint;
import com.cityfix.repository.ComplaintRepository;
import com.cityfix.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    public DashboardStatsResponse getAdminDashboardStats() {
        DashboardStatsResponse stats = new DashboardStatsResponse();
        stats.setTotalComplaints(complaintRepository.count());
        stats.setPendingComplaints(complaintRepository.countByStatus("Pending"));
        stats.setAssignedComplaints(complaintRepository.countByStatus("Assigned"));
        stats.setInProgressComplaints(complaintRepository.countByStatus("In Progress"));
        stats.setResolvedComplaints(complaintRepository.countByStatus("Resolved"));
        stats.setRejectedComplaints(complaintRepository.countByStatus("Rejected"));
        stats.setTotalCitizens(userRepository.count());

        // Category breakdown
        Map<String, Long> categoryMap = new HashMap<>();
        List<Object[]> categoryCounts = complaintRepository.countComplaintsByCategory();
        for (Object[] row : categoryCounts) {
            categoryMap.put((String) row[0], (Long) row[1]);
        }
        stats.setCategoryDistribution(categoryMap);

        // Status breakdown
        Map<String, Long> statusMap = new HashMap<>();
        List<Object[]> statusCounts = complaintRepository.countComplaintsByStatus();
        for (Object[] row : statusCounts) {
            statusMap.put((String) row[0], (Long) row[1]);
        }
        stats.setStatusDistribution(statusMap);

        // Recent complaints
        List<Complaint> recent = complaintRepository.findTop5ByOrderByCreatedAtDesc();
        stats.setRecentComplaints(recent.stream().map(ComplaintResponse::fromEntity).collect(Collectors.toList()));

        return stats;
    }

    public List<ComplaintResponse> searchComplaints(String search, String category, String status, String priority) {
        List<Complaint> list = complaintRepository.searchAdminComplaints(search, category, status, priority);
        return list.stream().map(ComplaintResponse::fromEntity).collect(Collectors.toList());
    }
}
