package com.markany.employee;

import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class VectorStore {
    
    private final Map<String, Map<String, Double>> employeeVectors = new HashMap<>();
    private final Map<String, Set<String>> skillSynonyms = new HashMap<>();
    
    public VectorStore() {
        initializeSkillSynonyms();
    }
    
    private void initializeSkillSynonyms() {
        // 개발 관련 스킬 동의어 그룹
        addSkillGroup("Java", Arrays.asList("Java", "자바", "JVM", "Spring", "Spring Boot"));
        addSkillGroup("JavaScript", Arrays.asList("JavaScript", "JS", "Node.js", "React", "Vue.js"));
        addSkillGroup("Python", Arrays.asList("Python", "파이썬", "Django", "Flask"));
        addSkillGroup("Database", Arrays.asList("MySQL", "PostgreSQL", "Oracle", "MongoDB", "Redis"));
        addSkillGroup("Cloud", Arrays.asList("AWS", "Azure", "GCP", "Docker", "Kubernetes"));
        addSkillGroup("DevOps", Arrays.asList("Git", "Jenkins", "CI/CD", "Docker", "Kubernetes"));
        
        // 보안 관련 스킬 동의어 그룹
        addSkillGroup("문서보안", Arrays.asList("문서보안JSON", "문서보안BLUE", "DRM", "문서암호화"));
        addSkillGroup("개인정보보안", Arrays.asList("개인정보보안", "개인정보보호", "GDPR", "개인정보처리"));
        addSkillGroup("시스템보안", Arrays.asList("시스템보안", "서버보안", "OS보안", "접근제어"));
        addSkillGroup("네트워크보안", Arrays.asList("네트워크보안", "방화벽", "VPN", "침입탐지"));
        addSkillGroup("보안감사", Arrays.asList("보안감사", "취약점분석", "보안점검", "컴플라이언스"));
    }
    
    private void addSkillGroup(String mainSkill, List<String> synonyms) {
        Set<String> skillSet = new HashSet<>(synonyms);
        for (String skill : synonyms) {
            skillSynonyms.put(skill.toLowerCase(), skillSet);
        }
    }
    
    public void indexEmployee(Employee employee) {
        Map<String, Double> vector = createEmployeeVector(employee);
        employeeVectors.put(employee.getEmpId(), vector);
    }

    private Map<String, Double> createEmployeeVector(Employee employee) {
        Map<String, Double> vector = new HashMap<>();
        
        // 스킬 벡터화
        if (employee.getSkills() != null) {
            String[] skills = employee.getSkills().split(",");
            for (String skill : skills) {
                String normalizedSkill = skill.trim().toLowerCase();
                vector.put(normalizedSkill, 1.0);
                
                // 동의어 스킬도 추가 (가중치 0.8)
                Set<String> synonyms = skillSynonyms.get(normalizedSkill);
                if (synonyms != null) {
                    for (String synonym : synonyms) {
                        if (!synonym.toLowerCase().equals(normalizedSkill)) {
                            vector.put(synonym.toLowerCase(), 0.8);
                        }
                    }
                }
            }
        }
        
        // 경력 가중치 추가
        int experience = employee.getYearsExp() != null ? employee.getYearsExp() : 0;
        vector.put("experience_weight", experience / 15.0); // 정규화
        
        // 레벨 가중치 추가
        double levelWeight = switch (employee.getLevel()) {
            case 시니어 -> 1.0;
            case 중급 -> 0.7;
            case 신입 -> 0.4;
        };
        vector.put("level_weight", levelWeight);
        
        return vector;
    }
    
    public void updateEmployeeVectorWithProjectHistory(Employee employee, List<ProjectHistory> projectHistories) {
        Map<String, Double> vector = createEmployeeVector(employee);
        
        // 프로젝트 경험 가중치 추가
        Map<String, Integer> projectTypeCount = new HashMap<>();
        Map<String, Integer> skillExperienceCount = new HashMap<>();
        
        for (ProjectHistory history : projectHistories) {
            // 프로젝트 유형 경험 추가
            if (history.getProjectType() != null) {
                String projectType = history.getProjectType().toLowerCase();
                projectTypeCount.put(projectType, projectTypeCount.getOrDefault(projectType, 0) + 1);
            }
            
            // 프로젝트에서 사용한 스킬 경험 추가
            if (history.getRequiredSkills() != null) {
                String[] skills = history.getRequiredSkills().split(",");
                for (String skill : skills) {
                    String normalizedSkill = skill.trim().toLowerCase();
                    skillExperienceCount.put(normalizedSkill, skillExperienceCount.getOrDefault(normalizedSkill, 0) + 1);
                }
            }
            
            // 프로젝트 설명에서 의미있는 키워드만 추출
            if (history.getProjectDescription() != null) {
                extractMeaningfulKeywords(history.getProjectDescription()).forEach(keyword -> 
                    skillExperienceCount.put("desc_" + keyword, skillExperienceCount.getOrDefault("desc_" + keyword, 0) + 1)
                );
            }
        }
        
        // 프로젝트 유형 경험 벡터에 추가
        for (Map.Entry<String, Integer> entry : projectTypeCount.entrySet()) {
            double experienceWeight = Math.min(entry.getValue() * 0.2, 1.0); // 최대 1.0
            vector.put("project_type_" + entry.getKey(), experienceWeight);
        }
        
        // 스킬 경험 가중치 강화
        for (Map.Entry<String, Integer> entry : skillExperienceCount.entrySet()) {
            String skill = entry.getKey();
            double experienceBoost = Math.min(entry.getValue() * 0.3, 0.5); // 최대 0.5 추가
            double currentWeight = vector.getOrDefault(skill, 0.0);
            vector.put(skill, currentWeight + experienceBoost);
        }
        
        employeeVectors.put(employee.getEmpId(), vector);
    }
    
    public List<Employee> searchSimilarEmployees(String requiredSkills, List<Employee> availableEmployees, int topK) {
        return searchSimilarEmployees(requiredSkills, null, availableEmployees, topK);
    }
    
    public List<Employee> searchSimilarEmployees(String requiredSkills, String projectDescription, List<Employee> availableEmployees, int topK) {
        Map<String, Double> queryVector = createQueryVector(requiredSkills, projectDescription);
        
        List<EmployeeScore> scores = new ArrayList<>();
        
        for (Employee employee : availableEmployees) {
            if (!employeeVectors.containsKey(employee.getEmpId())) {
                indexEmployee(employee);
            }
            
            Map<String, Double> empVector = employeeVectors.get(employee.getEmpId());
            double similarity = calculateCosineSimilarity(queryVector, empVector);
            scores.add(new EmployeeScore(employee, similarity));
        }
        
        return scores.stream()
                .sorted((a, b) -> Double.compare(b.score, a.score))
                .limit(topK)
                .map(es -> es.employee)
                .collect(Collectors.toList());
    }
    
    private Map<String, Double> createQueryVector(String requiredSkills, String projectDescription) {
        Map<String, Double> vector = new HashMap<>();
        
        if (requiredSkills != null) {
            String[] skills = requiredSkills.split(",");
            for (String skill : skills) {
                String normalizedSkill = skill.trim().toLowerCase();
                vector.put(normalizedSkill, 1.0);
                
                // 동의어 스킬도 추가
                Set<String> synonyms = skillSynonyms.get(normalizedSkill);
                if (synonyms != null) {
                    for (String synonym : synonyms) {
                        if (!synonym.toLowerCase().equals(normalizedSkill)) {
                            vector.put(synonym.toLowerCase(), 0.9);
                        }
                    }
                }
            }
        }
        
        // 프로젝트 설명에서 의미있는 키워드만 추출
        if (projectDescription != null) {
            extractMeaningfulKeywords(projectDescription).forEach(keyword -> 
                vector.put("desc_" + keyword, 0.7)
            );
        }
        
        return vector;
    }
    
    private double calculateCosineSimilarity(Map<String, Double> vector1, Map<String, Double> vector2) {
        Set<String> commonKeys = new HashSet<>(vector1.keySet());
        commonKeys.retainAll(vector2.keySet());
        
        if (commonKeys.isEmpty()) {
            return 0.0;
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        Set<String> allKeys = new HashSet<>(vector1.keySet());
        allKeys.addAll(vector2.keySet());
        
        for (String key : allKeys) {
            double val1 = vector1.getOrDefault(key, 0.0);
            double val2 = vector2.getOrDefault(key, 0.0);
            
            dotProduct += val1 * val2;
            norm1 += val1 * val1;
            norm2 += val2 * val2;
        }
        
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    private Set<String> extractMeaningfulKeywords(String text) {
        Set<String> stopWords = Set.of("시스템", "개발", "구축", "운영", "관리", "서비스", "프로젝트", "업무", 
                                      "진행", "수행", "수정", "개선", "및", "또는", "그리고", "이를", "통해", "위해");
        
        return Arrays.stream(text.toLowerCase().split("\\s+"))
                .map(word -> word.replaceAll("[^a-zA-Z가-힣0-9]", "").trim())
                .filter(word -> word.length() > 1 && !stopWords.contains(word))
                .limit(10) // 최대 10개 키워드만 사용
                .collect(Collectors.toSet());
    }
    
    private static class EmployeeScore {
        final Employee employee;
        final double score;
        
        EmployeeScore(Employee employee, double score) {
            this.employee = employee;
            this.score = score;
        }
    }
}