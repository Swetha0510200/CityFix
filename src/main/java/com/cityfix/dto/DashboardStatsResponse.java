package com.cityfix.dto;

import java.util.List;
import java.util.Map;

public class DashboardStatsResponse {

    private long totalComplaints;
    private long pendingComplaints;
    private long assignedComplaints;
    private long inProgressComplaints;
    private long resolvedComplaints;
    private long rejectedComplaints;
    private long totalCitizens;
    private Map<String, Long> categoryDistribution;
    private Map<String, Long> statusDistribution;
    private List<ComplaintResponse> recentComplaints;

    public DashboardStatsResponse() {
    }

    public long getTotalComplaints() {
        return totalComplaints;
    }

    public void setTotalComplaints(long totalComplaints) {
        this.totalComplaints = totalComplaints;
    }

    public long getPendingComplaints() {
        return pendingComplaints;
    }

    public void setPendingComplaints(long pendingComplaints) {
        this.pendingComplaints = pendingComplaints;
    }

    public long getAssignedComplaints() {
        return assignedComplaints;
    }

    public void setAssignedComplaints(long assignedComplaints) {
        this.assignedComplaints = assignedComplaints;
    }

    public long getInProgressComplaints() {
        return inProgressComplaints;
    }

    public void setInProgressComplaints(long inProgressComplaints) {
        this.inProgressComplaints = inProgressComplaints;
    }

    public long getResolvedComplaints() {
        return resolvedComplaints;
    }

    public void setResolvedComplaints(long resolvedComplaints) {
        this.resolvedComplaints = resolvedComplaints;
    }

    public long getRejectedComplaints() {
        return rejectedComplaints;
    }

    public void setRejectedComplaints(long rejectedComplaints) {
        this.rejectedComplaints = rejectedComplaints;
    }

    public long getTotalCitizens() {
        return totalCitizens;
    }

    public void setTotalCitizens(long totalCitizens) {
        this.totalCitizens = totalCitizens;
    }

    public Map<String, Long> getCategoryDistribution() {
        return categoryDistribution;
    }

    public void setCategoryDistribution(Map<String, Long> categoryDistribution) {
        this.categoryDistribution = categoryDistribution;
    }

    public Map<String, Long> getStatusDistribution() {
        return statusDistribution;
    }

    public void setStatusDistribution(Map<String, Long> statusDistribution) {
        this.statusDistribution = statusDistribution;
    }

    public List<ComplaintResponse> getRecentComplaints() {
        return recentComplaints;
    }

    public void setRecentComplaints(List<ComplaintResponse> recentComplaints) {
        this.recentComplaints = recentComplaints;
    }
}
