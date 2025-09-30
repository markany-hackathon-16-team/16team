package com.markany.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RAGService {
    
    @Autowired
    private VectorStore vectorStore;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    public List<EmployeeRecommendation> recommendWithRAG(Project project, List<Employee> availableEmployees) {
        int recommendCount = calculateRecommendCount(project.getTotalMm());
        
        // 1. RAG 검색: 프로젝트 요구사항과 유사한 직원들 검색 (설명 포함)
        List<Employee> similarEmployees = vectorStore.searchSimilarEmployees(
            project.getRequiredSkills(),
            project.getDescription(),
            availableEmployees, 
            Math.min(recommendCount * 2, availableEmployees.size()) // 더 많이 검색해서 다양성 확보
        );
        
        // 2. 검색된 직원들의 컨텍스트 정보 수집
        String contextInfo = buildContextFromSimilarEmployees(similarEmployees, project);
        
        // 3. 최종 추천 로직 (경력 우선 + RAG 점수 조합)
        List<EmployeeRecommendation> recommendations = new ArrayList<>();
        
        for (Employee emp : similarEmployees) {
            if (recommendations.size() >= recommendCount) break;
            
            String reason = generateRAGBasedReason(emp, project, contextInfo);
            recommendations.add(new EmployeeRecommendation(emp, reason));
        }
        
        // 4. 부족한 경우 기본 추천으로 보완
        if (recommendations.size() < recommendCount) {
            List<Employee> remaining = new ArrayList<>(availableEmployees);
            remaining.removeAll(similarEmployees);
            
            remaining.stream()
                .sorted((e1, e2) -> {
                    int exp1 = e1.getYearsExp() != null ? e1.getYearsExp() : 0;
                    int exp2 = e2.getYearsExp() != null ? e2.getYearsExp() : 0;
                    return Integer.compare(exp2, exp1);
                })
                .limit(recommendCount - recommendations.size())
                .forEach(emp -> {
                    String reason = "[보완 추천] " + generateDefaultReason(emp);
                    recommendations.add(new EmployeeRecommendation(emp, reason));
                });
        }
        
        return recommendations;
    }
    
    private String buildContextFromSimilarEmployees(List<Employee> similarEmployees, Project project) {
        StringBuilder context = new StringBuilder();
        context.append("유사한 스킬을 가진 인력들의 특성:\n");
        
        // 공통 스킬 분석
        Map<String, Integer> skillCount = new HashMap<>();
        for (Employee emp : similarEmployees) {
            if (emp.getSkills() != null) {
                String[] skills = emp.getSkills().split(",");
                for (String skill : skills) {
                    String normalizedSkill = skill.trim();
                    skillCount.put(normalizedSkill, skillCount.getOrDefault(normalizedSkill, 0) + 1);
                }
            }
        }
        
        // 가장 많이 보유한 스킬 상위 3개
        skillCount.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(3)
            .forEach(entry -> 
                context.append("- ").append(entry.getKey())
                       .append(" (").append(entry.getValue()).append("명 보유)\n")
            );
        
        // 평균 경력 계산
        double avgExperience = similarEmployees.stream()
            .mapToInt(emp -> emp.getYearsExp() != null ? emp.getYearsExp() : 0)
            .average()
            .orElse(0.0);
        
        context.append("- 평균 경력: ").append(String.format("%.1f", avgExperience)).append("년\n");
        
        return context.toString();
    }

    private String generateRAGBasedReason(Employee emp, Project project, String contextInfo) {
        StringBuilder reason = new StringBuilder();
        
        // 경력 우선 언급
        reason.append(emp.getYearsExp() != null ? emp.getYearsExp() + "년 경력" : "경력");
        reason.append(", ").append(emp.getLevel()).append(" 레벨 ").append(emp.getRole());
        
        // 스킬 매칭 분석
        if (emp.getSkills() != null && project.getRequiredSkills() != null) {
            String[] empSkills = emp.getSkills().toLowerCase().split(",");
            String[] reqSkills = project.getRequiredSkills().toLowerCase().split(",");
            
            List<String> matchedSkills = new ArrayList<>();
            for (String reqSkill : reqSkills) {
                String reqTrimmed = reqSkill.trim();
                for (String empSkill : empSkills) {
                    if (empSkill.trim().contains(reqTrimmed) || reqTrimmed.contains(empSkill.trim())) {
                        matchedSkills.add(reqTrimmed);
                        break;
                    }
                }
            }
            
            if (!matchedSkills.isEmpty()) {
                reason.append(", ").append(String.join(", ", matchedSkills)).append(" 스킬 매칭");
            }
        }
        
        return reason.toString();
    }
    
    private String generateDefaultReason(Employee emp) {
        StringBuilder reason = new StringBuilder();
        reason.append(emp.getYearsExp() != null ? emp.getYearsExp() + "년 경력" : "경력");
        reason.append(", ").append(emp.getLevel()).append(" 레벨 ").append(emp.getRole());
        if (emp.getSkills() != null && !emp.getSkills().trim().isEmpty()) {
            reason.append(", ").append(emp.getSkills()).append(" 스킬 보유");
        }
        return reason.toString();
    }
    
    private int calculateRecommendCount(Integer totalMm) {
        if (totalMm == null || totalMm <= 0) return 3;
        return Math.max(1, Math.min(10, totalMm));
    }
}