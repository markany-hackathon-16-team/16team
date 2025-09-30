package com.markany.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProjectCompletionService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private ProjectHistoryRepository projectHistoryRepository;
    
    @Autowired
    private VectorStore vectorStore;
    
    public void completeProject(String projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) return;
        
        // 1. 프로젝트 상태를 완료로 변경
        project.setStatus(Project.Status.완료);
        projectRepository.save(project);
        
        // 2. 할당된 직원들의 프로젝트 히스토리 생성
        List<Employee> assignedEmployees = employeeRepository.findAll().stream()
            .filter(emp -> projectId.equals(emp.getCurrentProject()))
            .toList();
        
        for (Employee employee : assignedEmployees) {
            // 프로젝트 히스토리 생성
            ProjectHistory history = new ProjectHistory();
            history.setEmployeeId(employee.getEmpId());
            history.setProjectId(project.getProjectId());
            history.setProjectName(project.getProjectName());
            history.setProjectType(project.getType());
            history.setProjectDescription(project.getDescription());
            history.setRequiredSkills(project.getRequiredSkills());
            history.setComplexity(project.getComplexity() != null ? project.getComplexity().toString() : null);
            history.setRoleInProject(employee.getRole().toString());
            history.setStartDate(project.getStartDate());
            history.setEndDate(project.getEndDate());
            
            projectHistoryRepository.save(history);
            
            // 직원의 현재 프로젝트 해제
            employee.setCurrentProject(null);
            employeeRepository.save(employee);
            
            // 3. 임베딩 업데이트
            updateEmployeeEmbedding(employee);
        }
    }
    
    private void updateEmployeeEmbedding(Employee employee) {
        // 해당 직원의 모든 프로젝트 히스토리 조회
        List<ProjectHistory> projectHistories = projectHistoryRepository.findByEmployeeId(employee.getEmpId());
        
        // 프로젝트 경험을 반영한 임베딩 업데이트
        vectorStore.updateEmployeeVectorWithProjectHistory(employee, projectHistories);
        
        System.out.println("임베딩 업데이트 완료: " + employee.getName() + " (" + employee.getEmpId() + ")");
        System.out.println("총 프로젝트 경험: " + projectHistories.size() + "개");
    }
}