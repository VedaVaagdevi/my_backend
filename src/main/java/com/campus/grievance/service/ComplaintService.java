package com.campus.grievance.service;

import com.campus.grievance.dto.Dtos.*;
import com.campus.grievance.model.*;
import com.campus.grievance.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ComplaintService {
    private final ComplaintRepository complaints;
    private final UserRepository users;

    public ComplaintService(ComplaintRepository complaints, UserRepository users) {
        this.complaints = complaints; this.users = users;
    }

    private User user(String email) {
        return users.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    @Transactional
    public Complaint create(String email, ComplaintRequest r) {
        Complaint c = new Complaint();
        c.setTitle(r.title()); c.setDescription(r.description()); c.setCategory(r.category());
        c.setCategory(r.category());
        if (r.department() != null && !r.department().isBlank()) {
            c.setDepartment(r.department());
        }
        c.setUser(user(email));
        c = complaints.save(c);
        c.setCode(String.format("CMP-%06d", c.getId()));
        return c;
    }

    public List<Complaint> mine(String email) { return complaints.findByUserOrderByCreatedAtDesc(user(email)); }

    public Complaint track(String code, String email, boolean admin) {
        Complaint c = complaints.findByCode(code.trim().toUpperCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint ID not found"));
        if (!admin && !c.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint ID not found");
        }
        return c;
    }


    @Transactional
    public Complaint feedback(String email, Long id, FeedbackRequest r) {
        Complaint c = complaints.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!c.getUser().getEmail().equals(email)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if (c.getStatus() != Status.RESOLVED) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feedback allowed only after resolution");
        c.setRating(r.rating()); c.setFeedback(r.comment());
        return c;
    }

    public List<Complaint> search(Status s, Category cat, String q) {
        return complaints.search(s, cat, q == null ? "" : q.trim());
    }

    @Transactional
    public Complaint adminUpdate(Long id, AdminUpdateRequest r) {
        Complaint c = complaints.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Status old = c.getStatus();
        if (r.status() != null) {
            c.setStatus(r.status());
        }
        if (r.department() != null && !r.department().isBlank()) {
            c.setDepartment(r.department());
            if (c.getStatus() == Status.SUBMITTED) {
                c.setStatus(Status.ASSIGNED);
            }
        }
        LocalDateTime now = LocalDateTime.now();
        if (c.getStatus() != old) {
            switch (c.getStatus()) {
                case ASSIGNED -> c.setAssignedAt(now);
                case IN_PROGRESS -> c.setInProgressAt(now);
                case RESOLVED -> c.setResolvedAt(now);
                default -> { }
            }
            if (c.getStatus() != Status.RESOLVED) {
                c.setResolvedAt(null);
            }
        }
        c.setAdminRemark(r.remark());
        c.setUpdatedAt(now);
        return c;
    }

    public Map<String, Object> report() {
        return Map.of("byStatus", toMap(complaints.countByStatus()),
                      "byCategory", toMap(complaints.countByCategory()),
                      "total", complaints.count());
    }

    private Map<String, Long> toMap(List<Object[]> rows) {
        Map<String, Long> m = new LinkedHashMap<>();
        for (Object[] r : rows) m.put(String.valueOf(r[0]), (Long) r[1]);
        return m;
    }
}