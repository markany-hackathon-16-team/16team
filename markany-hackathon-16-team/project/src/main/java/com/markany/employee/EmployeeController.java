package com.markany.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.*;

@Controller
@RequestMapping("/employees")
public class EmployeeController {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @GetMapping
    public String list(Model model) {
        List<Employee> employees = employeeRepository.findAll();
        Map<String, String> projectNames = new HashMap<>();
        
        // 프로젝트 ID를 프로젝트명으로 매핑
        List<Project> projects = projectRepository.findAll();
        for (Project project : projects) {
            projectNames.put(project.getProjectId(), project.getProjectName());
        }
        
        model.addAttribute("employees", employees);
        model.addAttribute("projectNames", projectNames);
        return "employee/list";
    }
    
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employee/form";
    }
    
    @PostMapping
    public String create(@ModelAttribute Employee employee, BindingResult result) {
        System.out.println("=== 직원 등록 시도 ===");
        System.out.println("직원 ID: " + employee.getEmpId());
        System.out.println("이름: " + employee.getName());
        System.out.println("역할: " + employee.getRole());
        
        if (employee.getEmpId() == null || employee.getEmpId().trim().isEmpty()) {
            System.out.println("에러: 직원 ID가 비어있음");
            return "employee/form";
        }
        
        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            System.out.println("에러: 이름이 비어있음");
            return "employee/form";
        }
        
        try {
            Employee saved = employeeRepository.save(employee);
            System.out.println("저장 성공: " + saved.getEmpId());
        } catch (Exception e) {
            System.out.println("저장 실패: " + e.getMessage());
            e.printStackTrace();
            return "employee/form";
        }
        
        return "redirect:/employees";
    }
    
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable String id, Model model) {
        Employee employee = employeeRepository.findById(id).orElseThrow();
        model.addAttribute("employee", employee);
        return "employee/form";
    }
    
    @PostMapping("/{id}")
    public String update(@PathVariable String id, @ModelAttribute Employee employee, BindingResult result) {
        employee.setEmpId(id);
        employeeRepository.save(employee);
        return "redirect:/employees";
    }
    
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id) {
        employeeRepository.deleteById(id);
        return "redirect:/employees";
    }
}
