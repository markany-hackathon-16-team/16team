package com.markany.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.Random;

@RestController
public class DataController {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @GetMapping("/init-data")
    public String initData() {
        // 데이터가 이미 있는지 확인
        long employeeCount = employeeRepository.count();
        long projectCount = projectRepository.count();
        
        if (employeeCount > 0 || projectCount > 0) {
            return "데이터가 이미 존재합니다. 초기화를 건너뜁니다.";
        }
        
        Random random = new Random();
        
        // 수행 50명 생성
        String[] performerSkills = {"문서보안JSON", "문서보안BLUE", "개인정보보안", "화면보안", "출력물보안", "암호화", "접근제어", "로그분석", "보안감사", "취약점분석", "침해대응", "정보보호", "네트워크보안", "시스템보안", "데이터보안"};
        
        for (int i = 1; i <= 50; i++) {
            Employee performer = new Employee();
            performer.setEmpId("P" + String.format("%03d", i));
            performer.setName("수행" + i);
            performer.setRole(Employee.Role.수행인력);
            performer.setYearsExp(random.nextInt(10) + 1);
            performer.setLevel(Employee.Level.values()[random.nextInt(Employee.Level.values().length)]);
            performer.setAvailable(Employee.Available.가능);
            
            // 랜덤으로 2~5개 스킬 선택
            int skillCount = random.nextInt(4) + 2; // 2~5개
            StringBuilder skills = new StringBuilder();
            java.util.Set<String> selectedSkills = new java.util.HashSet<>();
            
            while (selectedSkills.size() < skillCount) {
                selectedSkills.add(performerSkills[random.nextInt(performerSkills.length)]);
            }
            
            skills.append(String.join(", ", selectedSkills));
            performer.setSkills(skills.toString());
            employeeRepository.save(performer);
        }
        
        // 개발자 50명 생성
        String[] devSkills = {"Java", "Spring Boot", "Python", "JavaScript", "React", "Vue.js", "MySQL", "PostgreSQL", "Docker", "Kubernetes", "AWS", "Git", "Jenkins", "Redis", "MongoDB"};
        
        for (int i = 1; i <= 50; i++) {
            Employee developer = new Employee();
            developer.setEmpId("D" + String.format("%03d", i));
            developer.setName("개발자" + i);
            developer.setRole(Employee.Role.개발자);
            developer.setYearsExp(random.nextInt(15) + 1);
            developer.setLevel(Employee.Level.values()[random.nextInt(Employee.Level.values().length)]);
            developer.setAvailable(Employee.Available.가능);
            
            // 랜덤으로 2~5개 스킬 선택
            int skillCount = random.nextInt(4) + 2; // 2~5개
            StringBuilder skills = new StringBuilder();
            java.util.Set<String> selectedSkills = new java.util.HashSet<>();
            
            while (selectedSkills.size() < skillCount) {
                selectedSkills.add(devSkills[random.nextInt(devSkills.length)]);
            }
            
            skills.append(String.join(", ", selectedSkills));
            developer.setSkills(skills.toString());
            employeeRepository.save(developer);
        }
        
        // 프로젝트 20개 생성
        String[] projectTypes = {"웹 개발", "모바일 앱", "시스템 구축", "데이터 분석", "AI/ML"};
        Project.Complexity[] complexities = {Project.Complexity.단순, Project.Complexity.보통, Project.Complexity.복잡};
        Project.Status[] statuses = {Project.Status.계획중, Project.Status.진행중, Project.Status.완료};
        
        for (int i = 1; i <= 20; i++) {
            Project project = new Project();
            project.setProjectId("PRJ" + String.format("%03d", i));
            project.setProjectName("프로젝트" + i);
            project.setType(projectTypes[random.nextInt(projectTypes.length)]);
            project.setComplexity(complexities[random.nextInt(complexities.length)]);
            project.setTotalMm(random.nextInt(24) + 6); // 6-30 MM
            project.setDurationMonths(random.nextInt(12) + 3); // 3-15개월
            LocalDate startDate = LocalDate.now().plusDays(random.nextInt(30));
            project.setStartDate(startDate.toString());
            project.setEndDate(startDate.plusMonths(project.getDurationMonths()).toString());
            project.setStatus(statuses[random.nextInt(statuses.length)]);
            project.setRequiredSkills("Java, Spring, 문서보안, 개인정보보안");
            project.setNotes("테스트 프로젝트 " + i);
            projectRepository.save(project);
        }
        
        return "데이터 초기화 완료: 수행 50명, 개발자 50명, 프로젝트 20개 생성";
    }
}
