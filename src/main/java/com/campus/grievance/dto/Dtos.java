package com.campus.grievance.dto;

import com.campus.grievance.model.Category;
import com.campus.grievance.model.Status;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Dtos {

    public record RegisterRequest(
            @NotBlank String name,
            @NotBlank @Email String email,
            @Size(min = 6, message = "must be at least 6 characters") String password) {}

    public record ComplaintRequest(
            @NotBlank String title,
            @NotBlank String description,
            @NotNull Category category,
            String department) {}
    
    public record AdminUpdateRequest(Status status, String department, String remark) {}

    public record FeedbackRequest(
            @Min(1) @Max(5) int rating,
            String comment) {}
}