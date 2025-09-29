package com.markany.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @GetMapping("/")
    public String dashboard(Model model) {
        long totalEmployees = employeeRepository.count();
        long totalProjects = projectRepository.count();
        
        // 실제 가용 인력: 프로젝트에 할당되지 않은 인원
        long availableEmployees = employeeRepository.findAll().stream()
            .mapToLong(emp -> {
                // 가용 상태이면서 현재 프로젝트가 없는 경우
                boolean isAvailable = "가능".equals(emp.getAvailable().toString());
                boolean hasNoProject = emp.getCurrentProject() == null || emp.getCurrentProject().trim().isEmpty();
                return (isAvailable && hasNoProject) ? 1 : 0;
            })
            .sum();
            
        // 프로젝트에 할당된 인원 수
        long assignedEmployees = employeeRepository.findAll().stream()
            .mapToLong(emp -> {
                boolean hasProject = emp.getCurrentProject() != null && !emp.getCurrentProject().trim().isEmpty();
                return hasProject ? 1 : 0;
            })
            .sum();
        
        long activeProjects = projectRepository.findAll().stream()
            .mapToLong(proj -> "진행중".equals(proj.getStatus().toString()) ? 1 : 0)
            .sum();
        
        model.addAttribute("totalEmployees", totalEmployees);
        model.addAttribute("totalProjects", totalProjects);
        model.addAttribute("availableEmployees", availableEmployees);
        model.addAttribute("assignedEmployees", assignedEmployees);
        model.addAttribute("activeProjects", activeProjects);
        model.addAttribute("recentProjects", projectRepository.findAll());
        
        return "dashboard";
    }
}
