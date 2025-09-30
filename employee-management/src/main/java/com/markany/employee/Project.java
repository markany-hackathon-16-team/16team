package com.markany.employee;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
public class Project {
    
    @Id
    @Column(name = "project_id")
    private String projectId;
    
    @NotBlank
    @Column(name = "project_name")
    private String projectName;
    
    private String type;
    
    @Enumerated(EnumType.STRING)
    private Complexity complexity;
    
    @Column(name = "total_mm")
    private Integer totalMm;
    
    @Column(name = "duration_months")
    private Integer durationMonths;
    
    @Column(name = "start_date")
    private String startDate;
    
    @Column(name = "end_date")
    private String endDate;
    
    @Enumerated(EnumType.STRING)
    private Status status = Status.계획중;
    
    @Column(name = "required_skills")
    private String requiredSkills;
    
    private String description;
    
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum Complexity {
        단순, 보통, 복잡
    }
    
    public enum Status {
        계획중, 진행중, 완료
    }
    
    // Getters and Setters
    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }
    
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Complexity getComplexity() { return complexity; }
    public void setComplexity(Complexity complexity) { this.complexity = complexity; }
    
    public Integer getTotalMm() { return totalMm; }
    public void setTotalMm(Integer totalMm) { this.totalMm = totalMm; }
    
    public Integer getDurationMonths() { return durationMonths; }
    public void setDurationMonths(Integer durationMonths) { this.durationMonths = durationMonths; }
    
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    
    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
