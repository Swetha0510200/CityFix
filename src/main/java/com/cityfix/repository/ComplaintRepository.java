package com.cityfix.repository;

import com.cityfix.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    Optional<Complaint> findByComplaintId(String complaintId);

    List<Complaint> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Complaint> findAllByOrderByCreatedAtDesc();

    List<Complaint> findTop5ByOrderByCreatedAtDesc();

    List<Complaint> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);

    long countByStatus(String status);

    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, String status);

    @Query("SELECT c.category, COUNT(c) FROM Complaint c GROUP BY c.category")
    List<Object[]> countComplaintsByCategory();

    @Query("SELECT c.status, COUNT(c) FROM Complaint c GROUP BY c.status")
    List<Object[]> countComplaintsByStatus();

    @Query("SELECT c FROM Complaint c WHERE c.user.id = :userId " +
           "AND (:category IS NULL OR :category = '' OR c.category = :category) " +
           "AND (:status IS NULL OR :status = '' OR c.status = :status) " +
           "AND (:search IS NULL OR :search = '' OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(c.complaintId) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(c.location) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY c.createdAt DESC")
    List<Complaint> searchCitizenComplaints(
            @Param("userId") Long userId,
            @Param("search") String search,
            @Param("category") String category,
            @Param("status") String status);

    @Query("SELECT c FROM Complaint c WHERE " +
           "(:category IS NULL OR :category = '' OR c.category = :category) " +
           "AND (:status IS NULL OR :status = '' OR c.status = :status) " +
           "AND (:priority IS NULL OR :priority = '' OR c.priority = :priority) " +
           "AND (:search IS NULL OR :search = '' OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(c.complaintId) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(c.location) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(c.user.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(c.user.email) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY c.createdAt DESC")
    List<Complaint> searchAdminComplaints(
            @Param("search") String search,
            @Param("category") String category,
            @Param("status") String status,
            @Param("priority") String priority);
}
