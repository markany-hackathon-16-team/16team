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
    
    @Autowired
    private ProjectHistoryRepository projectHistoryRepository;
    
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
        
        // 프로젝트 히스토리 30개 생성 (완료된 프로젝트 경험)
        String[] historyDescriptions = {
            "기업용 문서보안 솔루션 개발 프로젝트로서 JSON 기반 암호화 모듈과 BLUE DRM 기술을 활용하여 민감한 문서의 접근제어 및 권한관리 기능을 구현",
            "개인정보보호법 준수를 위한 데이터 마스킹 및 익명화 시스템 구축 프로젝트로 대용량 데이터베이스에서 개인정보를 자동 탐지하고 암호화하는 배치 처리 시스템 개발",
            "온프레미스 레거시 시스템을 AWS 클라우드로 마이그레이션하는 프로젝트로 Docker 컨테이너화와 Kubernetes 오케스트레이션을 통한 마이크로서비스 아키텍처 전환",
            "React Native 기반 모바일 쇼핑 애플리케이션 개발 프로젝트로 실시간 결제 시스템과 상품 추천 알고리즘 구현",
            "기업 네트워크 보안 모니터링 시스템 구축 프로젝트로 침입탐지시스템 구현과 실시간 위협 탐지 알고리즘 개발",
            "Python 기반 머신러닝을 활용한 비즈니스 인텔리전스 대시보드 개발 프로젝트로 빅데이터 분석과 예측 모델링 구현",
            "블록체인 기술을 활용한 공급망 추적 시스템 개발 프로젝트로 스마트 컨트랙트 구현과 분산원장 기반 제품 이력 관리"
        };
        
        for (int i = 1; i <= 30; i++) {
            ProjectHistory history = new ProjectHistory();
            history.setEmployeeId(i <= 15 ? "P" + String.format("%03d", random.nextInt(50) + 1) : "D" + String.format("%03d", random.nextInt(50) + 1));
            history.setProjectId("HIST" + String.format("%03d", i));
            history.setProjectName("완료된프로젝트" + i);
            history.setProjectType(projectTypes[random.nextInt(projectTypes.length)]);
            history.setProjectDescription(historyDescriptions[random.nextInt(historyDescriptions.length)]);
            history.setRequiredSkills(i <= 15 ? 
                performerSkills[random.nextInt(performerSkills.length)] + ", " + performerSkills[random.nextInt(performerSkills.length)] :
                devSkills[random.nextInt(devSkills.length)] + ", " + devSkills[random.nextInt(devSkills.length)]);
            history.setComplexity(complexities[random.nextInt(complexities.length)].toString());
            history.setRoleInProject(i <= 15 ? "수행인력" : "개발자");
            LocalDate historyStart = LocalDate.now().minusMonths(random.nextInt(24) + 1);
            history.setStartDate(historyStart.toString());
            history.setEndDate(historyStart.plusMonths(random.nextInt(6) + 3).toString());
            projectHistoryRepository.save(history);
        }
        
        return "데이터 초기화 완료: 수행 50명, 개발자 50명, 프로젝트 20개, 프로젝트 히스토리 30개 생성";
    }
}
