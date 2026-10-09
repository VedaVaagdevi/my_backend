package com.campus.grievance.repository;

import com.campus.grievance.model.Category;
import com.campus.grievance.model.Complaint;
import com.campus.grievance.model.Status;
import com.campus.grievance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByUserOrderByCreatedAtDesc(User user);

    Optional<Complaint> findByCode(String code);

    @Query("SELECT c FROM Complaint c WHERE (:status IS NULL OR c.status = :status) "
         + "AND (:category IS NULL OR c.category = :category) "
         + "AND (c.title LIKE CONCAT('%', :q, '%') OR c.code LIKE CONCAT('%', :q, '%')) "
         + "ORDER BY c.createdAt DESC")
    List<Complaint> search(@Param("status") Status status,
                           @Param("category") Category category,
                           @Param("q") String q);

    @Query("SELECT c.status, COUNT(c) FROM Complaint c GROUP BY c.status")
    List<Object[]> countByStatus();

    @Query("SELECT c.category, COUNT(c) FROM Complaint c GROUP BY c.category")
    List<Object[]> countByCategory();
}