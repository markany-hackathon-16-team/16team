package com.markany.employee;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
public class Employee {
    
    @Id
    @Column(name = "emp_id")
    private String empId;
    
    @NotBlank
    private String name;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Column(name = "years_exp")
    private Integer yearsExp;
    
    @Enumerated(EnumType.STRING)
    private Level level;
    
    @Enumerated(EnumType.STRING)
    private Available available = Available.가능;
    
    @Column(name = "current_project")
    private String currentProject;
    
    @Column(name = "available_date")
    private String availableDate;
    
    private String skills;
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum Role {
        개발자, 수행인력
    }
    
    public enum Level {
        신입, 중급, 시니어
    }
    
    public enum Available {
        가능, 불가능
    }
    
    // Getters and Setters
    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    
    public Integer getYearsExp() { return yearsExp; }
    public void setYearsExp(Integer yearsExp) { this.yearsExp = yearsExp; }
    
    public Level getLevel() { return level; }
    public void setLevel(Level level) { this.level = level; }
    
    public Available getAvailable() { return available; }
    public void setAvailable(Available available) { this.available = available; }
    
    public String getCurrentProject() { return currentProject; }
    public void setCurrentProject(String currentProject) { this.currentProject = currentProject; }
    
    public String getAvailableDate() { return availableDate; }
    public void setAvailableDate(String availableDate) { this.availableDate = availableDate; }
    
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
