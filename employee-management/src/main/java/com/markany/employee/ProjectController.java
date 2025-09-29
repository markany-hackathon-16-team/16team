package com.markany.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Controller
@RequestMapping("/projects")
public class ProjectController {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @GetMapping
    public String list(Model model) {
        List<Project> projects = projectRepository.findAll();
        
        // 각 프로젝트별 할당된 인력 정보 추가
        Map<String, List<Employee>> projectDevelopers = new HashMap<>();
        Map<String, List<Employee>> projectPerformers = new HashMap<>();
        
        for (Project project : projects) {
            List<Employee> developers = employeeRepository.findAll().stream()
                .filter(emp -> project.getProjectId().equals(emp.getCurrentProject()) && emp.getRole() == Employee.Role.개발자)
                .toList();
            List<Employee> performers = employeeRepository.findAll().stream()
                .filter(emp -> project.getProjectId().equals(emp.getCurrentProject()) && emp.getRole() == Employee.Role.수행인력)
                .toList();
                
            projectDevelopers.put(project.getProjectId(), developers);
            projectPerformers.put(project.getProjectId(), performers);
        }
        
        model.addAttribute("projects", projects);
        model.addAttribute("projectDevelopers", projectDevelopers);
        model.addAttribute("projectPerformers", projectPerformers);
        return "project/list";
    }
    
    @GetMapping("/{id}")
    public String detail(@PathVariable String id, Model model) {
        Project project = projectRepository.findById(id).orElseThrow();
        
        // 해당 프로젝트에 배정된 직원들 찾기
        List<Employee> assignedEmployees = new ArrayList<>();
        List<Employee> allEmployees = employeeRepository.findAll();
        for (Employee emp : allEmployees) {
            if (id.equals(emp.getCurrentProject())) {
                assignedEmployees.add(emp);
            }
        }
        
        model.addAttribute("project", project);
        model.addAttribute("assignedEmployees", assignedEmployees);
        return "project/detail";
    }
    
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("project", new Project());
        
        // 할당 가능한 개발자와 수행인력 목록 조회
        List<Employee> allDevelopers = employeeRepository.findByRole(Employee.Role.개발자);
        List<Employee> allPerformers = employeeRepository.findByRole(Employee.Role.수행인력);
        
        // 프로젝트 수 제한 확인하여 할당 가능한 인력만 필터링
        List<Employee> availableDevelopers = allDevelopers.stream()
            .filter(emp -> getEmployeeProjectCount(emp.getEmpId()) < 3) // 개발자 최대 3개
            .toList();
            
        List<Employee> availablePerformers = allPerformers.stream()
            .filter(emp -> getEmployeeProjectCount(emp.getEmpId()) < 5) // 수행 최대 5개
            .toList();
        
        model.addAttribute("developers", availableDevelopers);
        model.addAttribute("performers", availablePerformers);
        return "project/form";
    }
    
    private int getEmployeeProjectCount(String empId) {
        Employee emp = employeeRepository.findById(empId).orElse(null);
        if (emp == null || emp.getCurrentProject() == null || emp.getCurrentProject().isEmpty()) {
            return 0;
        }
        // 현재는 단일 프로젝트만 지원하므로 1 반환
        return 1;
    }
    
    @PostMapping
    public String create(@ModelAttribute Project project, 
                        @RequestParam(required = false) List<String> selectedDevelopers,
                        @RequestParam(required = false) List<String> selectedPerformers,
                        Model model) {
        
        // 개발자 프로젝트 수 제한 확인 (최대 3개)
        if (selectedDevelopers != null) {
            for (String empId : selectedDevelopers) {
                if (getEmployeeProjectCount(empId) >= 3) {
                    model.addAttribute("error", "개발자 " + empId + "는 이미 최대 프로젝트 수(3개)에 도달했습니다.");
                    return reloadFormWithData(model, project);
                }
            }
        }
        
        // 수행인력 프로젝트 수 제한 확인 (최대 5개)
        if (selectedPerformers != null) {
            for (String empId : selectedPerformers) {
                if (getEmployeeProjectCount(empId) >= 5) {
                    model.addAttribute("error", "수행인력 " + empId + "는 이미 최대 프로젝트 수(5개)에 도달했습니다.");
                    return reloadFormWithData(model, project);
                }
            }
        }
        
        try {
            Project saved = projectRepository.save(project);
            
            // 선택된 개발자들을 프로젝트에 할당
            if (selectedDevelopers != null) {
                for (String empId : selectedDevelopers) {
                    Employee emp = employeeRepository.findById(empId).orElse(null);
                    if (emp != null) {
                        emp.setCurrentProject(saved.getProjectId());
                        employeeRepository.save(emp);
                    }
                }
            }
            
            // 선택된 수행인력들을 프로젝트에 할당
            if (selectedPerformers != null) {
                for (String empId : selectedPerformers) {
                    Employee emp = employeeRepository.findById(empId).orElse(null);
                    if (emp != null) {
                        emp.setCurrentProject(saved.getProjectId());
                        employeeRepository.save(emp);
                    }
                }
            }
            
        } catch (Exception e) {
            model.addAttribute("error", "프로젝트 저장 중 오류가 발생했습니다.");
            return reloadFormWithData(model, project);
        }
        
        return "redirect:/projects";
    }
    
    private String reloadFormWithData(Model model, Project project) {
        List<Employee> allDevelopers = employeeRepository.findByRole(Employee.Role.개발자);
        List<Employee> allPerformers = employeeRepository.findByRole(Employee.Role.수행인력);
        
        List<Employee> availableDevelopers = allDevelopers.stream()
            .filter(emp -> getEmployeeProjectCount(emp.getEmpId()) < 3)
            .toList();
            
        List<Employee> availablePerformers = allPerformers.stream()
            .filter(emp -> getEmployeeProjectCount(emp.getEmpId()) < 5)
            .toList();
        
        model.addAttribute("developers", availableDevelopers);
        model.addAttribute("performers", availablePerformers);
        model.addAttribute("project", project);
        return "project/form";
    }
    
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable String id, Model model) {
        Project project = projectRepository.findById(id).orElseThrow();
        model.addAttribute("project", project);
        
        // 할당 가능한 개발자와 수행인력 목록 조회 (현재 프로젝트에 할당된 인력은 제외하고 계산)
        List<Employee> allDevelopers = employeeRepository.findByRole(Employee.Role.개발자);
        List<Employee> allPerformers = employeeRepository.findByRole(Employee.Role.수행인력);
        
        List<Employee> availableDevelopers = allDevelopers.stream()
            .filter(emp -> {
                int currentCount = getEmployeeProjectCount(emp.getEmpId());
                // 현재 프로젝트에 이미 할당된 경우는 선택 가능
                if (id.equals(emp.getCurrentProject())) {
                    return true;
                }
                return currentCount < 3;
            })
            .toList();
            
        List<Employee> availablePerformers = allPerformers.stream()
            .filter(emp -> {
                int currentCount = getEmployeeProjectCount(emp.getEmpId());
                // 현재 프로젝트에 이미 할당된 경우는 선택 가능
                if (id.equals(emp.getCurrentProject())) {
                    return true;
                }
                return currentCount < 5;
            })
            .toList();
        
        model.addAttribute("developers", availableDevelopers);
        model.addAttribute("performers", availablePerformers);
        return "project/form";
    }
    
    @PostMapping("/{id}")
    public String update(@PathVariable String id, @ModelAttribute Project project,
                        @RequestParam(required = false) List<String> selectedDevelopers,
                        @RequestParam(required = false) List<String> selectedPerformers,
                        Model model) {
        project.setProjectId(id);
        
        // 기존 할당 해제
        List<Employee> currentAssigned = employeeRepository.findAll().stream()
            .filter(emp -> id.equals(emp.getCurrentProject()))
            .toList();
        
        for (Employee emp : currentAssigned) {
            emp.setCurrentProject(null);
            employeeRepository.save(emp);
        }
        
        // 새로운 할당
        if (selectedDevelopers != null) {
            for (String empId : selectedDevelopers) {
                Employee emp = employeeRepository.findById(empId).orElse(null);
                if (emp != null) {
                    emp.setCurrentProject(id);
                    employeeRepository.save(emp);
                }
            }
        }
        
        if (selectedPerformers != null) {
            for (String empId : selectedPerformers) {
                Employee emp = employeeRepository.findById(empId).orElse(null);
                if (emp != null) {
                    emp.setCurrentProject(id);
                    employeeRepository.save(emp);
                }
            }
        }
        
        projectRepository.save(project);
        return "redirect:/projects";
    }
    
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id) {
        projectRepository.deleteById(id);
        return "redirect:/projects";
    }
}
