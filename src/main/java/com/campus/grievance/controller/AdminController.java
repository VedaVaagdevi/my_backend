package com.campus.grievance.controller;

import com.campus.grievance.dto.Dtos.AdminUpdateRequest;
import com.campus.grievance.model.Category;
import com.campus.grievance.model.Complaint;
import com.campus.grievance.model.Status;
import com.campus.grievance.service.ComplaintService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ComplaintService service;

    public AdminController(ComplaintService service) {
        this.service = service;
    }

    @GetMapping("/complaints")
    public List<Complaint> all(@RequestParam(required = false) Status status,
                               @RequestParam(required = false) Category category,
                               @RequestParam(required = false) String q) {
        return service.search(status, category, q);
    }

    @PutMapping("/complaints/{id}")
    public Complaint update(@PathVariable Long id, @RequestBody AdminUpdateRequest r) {
        return service.adminUpdate(id, r);
    }

    @GetMapping("/reports")
    public Map<String, Object> report() {
        return service.report();
    }
}