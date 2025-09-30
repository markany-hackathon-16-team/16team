package com.markany.employee;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

@Service
public class BedrockService {
    
    @Value("${bedrock.api.key}")
    private String apiKey;
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    public BedrockService() {
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }
    
    public List<EmployeeRecommendation> recommendEmployees(Project project, List<Employee> availableEmployees) {
        try {
            String prompt = buildPrompt(project, availableEmployees);
            String response = callBedrockAPI(prompt);
            return parseRecommendations(response, availableEmployees);
        } catch (Exception e) {
            // Fallback: 기본 추천 로직
            return getDefaultRecommendations(project, availableEmployees);
        }
    }
    
    private String buildPrompt(Project project, List<Employee> employees) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("프로젝트 정보:\n");
        prompt.append("- 프로젝트명: ").append(project.getProjectName()).append("\n");
        prompt.append("- 유형: ").append(project.getType()).append("\n");
        prompt.append("- 복잡도: ").append(project.getComplexity()).append("\n");
        prompt.append("- 필요 스킬: ").append(project.getRequiredSkills()).append("\n");
        prompt.append("- 총 MM: ").append(project.getTotalMm()).append("\n\n");
        
        prompt.append("가용 인력 정보:\n");
        for (Employee emp : employees) {
            prompt.append("- ").append(emp.getName())
                  .append(" (").append(emp.getEmpId()).append("): ")
                  .append("역할=").append(emp.getRole())
                  .append(", 레벨=").append(emp.getLevel())
                  .append(", 경력=").append(emp.getYearsExp()).append("년")
                  .append(", 스킬=").append(emp.getSkills()).append("\n");
        }
        
        prompt.append("\n위 프로젝트에 가장 적합한 인력 3명을 추천해주세요. ");
        prompt.append("각 인력의 ID와 추천 이유를 한 줄로 요약해서 '직원ID: 추천이유' 형식으로 응답해주세요.");
        
        return prompt.toString();
    }
    
    private String callBedrockAPI(String prompt) {
        // Bedrock API 호출 (실제 구현에서는 AWS SDK 사용)
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("prompt", prompt);
        requestBody.put("max_tokens", 500);
        
        try {
            return webClient.post()
                .uri("https://bedrock.amazonaws.com/invoke")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        } catch (Exception e) {
            throw new RuntimeException("Bedrock API 호출 실패", e);
        }
    }
    
    private List<EmployeeRecommendation> parseRecommendations(String response, List<Employee> availableEmployees) {
        // AI 응답에서 직원 ID와 추천 이유 추출
        List<EmployeeRecommendation> recommendations = new ArrayList<>();
        
        for (Employee emp : availableEmployees) {
            if (response.contains(emp.getEmpId()) && recommendations.size() < 3) {
                String reason = extractReason(response, emp.getEmpId());
                recommendations.add(new EmployeeRecommendation(emp, reason));
            }
        }
        
        return recommendations.isEmpty() ? getDefaultRecommendations(null, availableEmployees) : recommendations;
    }
    
    private List<EmployeeRecommendation> getDefaultRecommendations(Project project, List<Employee> availableEmployees) {
        // 기본 추천 로직: 경력과 레벨 기준으로 정렬
        return availableEmployees.stream()
            .sorted((e1, e2) -> {
                int levelCompare = compareLevel(e2.getLevel(), e1.getLevel());
                if (levelCompare != 0) return levelCompare;
                return Integer.compare(e2.getYearsExp() != null ? e2.getYearsExp() : 0, 
                                     e1.getYearsExp() != null ? e1.getYearsExp() : 0);
            })
            .limit(3)
            .map(emp -> new EmployeeRecommendation(emp, generateDefaultReason(emp)))
            .toList();
    }
    
    private String extractReason(String response, String empId) {
        // AI 응답에서 해당 직원의 추천 이유 추출 (간단한 구현)
        String[] lines = response.split("\n");
        for (String line : lines) {
            if (line.contains(empId)) {
                // 추천 이유가 포함된 라인에서 이유 부분 추출
                int reasonStart = line.indexOf(":");
                if (reasonStart > 0 && reasonStart < line.length() - 1) {
                    return line.substring(reasonStart + 1).trim();
                }
            }
        }
        return "AI 분석 결과 프로젝트에 적합한 인력으로 판단됨";
    }
    
    private String generateDefaultReason(Employee emp) {
        StringBuilder reason = new StringBuilder();
        reason.append(emp.getLevel()).append(" 레벨의 ");
        reason.append(emp.getYearsExp() != null ? emp.getYearsExp() + "년 경력" : "경력");
        reason.append(" ").append(emp.getRole());
        if (emp.getSkills() != null && !emp.getSkills().trim().isEmpty()) {
            reason.append(", ").append(emp.getSkills()).append(" 스킬 보유");
        }
        return reason.toString();
    }
    
    private int compareLevel(Employee.Level level1, Employee.Level level2) {
        Map<Employee.Level, Integer> levelOrder = Map.of(
            Employee.Level.시니어, 3,
            Employee.Level.중급, 2,
            Employee.Level.신입, 1
        );
        return Integer.compare(levelOrder.get(level1), levelOrder.get(level2));
    }
}