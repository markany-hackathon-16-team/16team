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
        
        // 한국 이름 목록
        String[] koreanNames = {
            "김민준", "이수진", "박지훈", "최예지", "정동현", "강소영", "윤재호", "임다은", "한준서", "오미지",
            "신창호", "배지은", "송민호", "전예린", "고준영", "류소연", "서지후", "노예진", "김동욱", "이지연",
            "박성훈", "최지우", "정예진", "강민준", "윤소영", "임준호", "한예지", "오지훈", "신다은", "배준서",
            "송지은", "전민호", "고예린", "류준영", "서소연", "노지후", "김예진", "이동욱", "박지연", "최성훈",
            "정지우", "강예진", "윤민준", "임소영", "한준호", "오예지", "신지훈", "배다은", "송준서", "전지은",
            "고민호", "류예린", "서준영", "노소연", "김지후", "이예진", "박동욱", "최지연", "정성훈", "강지우",
            "윤예진", "임민준", "한소영", "오준호", "신예지", "배지훈", "송다은", "전준서", "고지은", "류민호",
            "서예린", "노준영", "김소연", "이지후", "박예진", "최동욱", "정지연", "강성훈", "윤지우", "임예진",
            "한민준", "오소영", "신준호", "배예지", "송지훈", "전다은", "고준서", "류지은", "서민호", "노예린",
            "김준영", "이소연", "박지후", "최예진", "정동욱", "강지연", "윤성훈", "임지우", "한예진", "오민준"
        };
        
        // 수행 50명 생성
        String[] performerSkills = {"문서보안JSON", "문서보안BLUE", "개인정보보안", "화면보안", "출력물보안", "암호화", "접근제어", "로그분석", "보안감사", "취약점분석", "침해대응", "정보보호", "네트워크보안", "시스템보안", "데이터보안"};
        
        for (int i = 1; i <= 50; i++) {
            Employee performer = new Employee();
            performer.setEmpId("P" + String.format("%03d", i));
            performer.setName(koreanNames[random.nextInt(koreanNames.length)]);
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
            developer.setName(koreanNames[random.nextInt(koreanNames.length)]);
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
