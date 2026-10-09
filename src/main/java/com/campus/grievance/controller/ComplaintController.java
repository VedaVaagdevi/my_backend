package com.campus.grievance.controller;

import com.campus.grievance.dto.Dtos.ComplaintRequest;
import org.springframework.security.core.Authentication;
import com.campus.grievance.dto.Dtos.FeedbackRequest;
import com.campus.grievance.model.Complaint;
import com.campus.grievance.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService service;

    public ComplaintController(ComplaintService service) {
        this.service = service;
    }

    @PostMapping
    public Complaint create(Principal p, @Valid @RequestBody ComplaintRequest r) {
        return service.create(p.getName(), r);
    }

    @GetMapping("/my")
    public List<Complaint> mine(Principal p) {
        return service.mine(p.getName());
    }

    @GetMapping("/track/{code}")
    public Complaint track(Authentication auth, @PathVariable String code) {
        boolean admin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return service.track(code, auth.getName(), admin);
    }

    @PostMapping("/{id}/feedback")
    public Complaint feedback(Principal p, @PathVariable Long id, @Valid @RequestBody FeedbackRequest r) {
        return service.feedback(p.getName(), id, r);
    }
}