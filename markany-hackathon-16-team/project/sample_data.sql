USE 16team;

-- 직원 샘플 데이터 (100명)
INSERT INTO employees (emp_id, name, role, years_exp, level, available, current_project, available_date, skills, notes) VALUES
('EMP001', '김철수', '개발자', 5, '중급', '가능', NULL, '2024-01-01', '문서보안JSON, Java, Spring', '백엔드 전문'),
('EMP002', '이영희', '개발자', 8, '시니어', '불가능', 'PROJ001', '2024-03-01', '문서보안BLUE, React, JavaScript', '프론트엔드 리더'),
('EMP003', '박민수', '수행인력', 3, '중급', '가능', NULL, '2024-01-15', '개인정보솔루션, 테스팅', '품질관리 담당'),
('EMP004', '최지은', '개발자', 2, '신입', '가능', NULL, '2024-02-01', 'Java, Spring Boot', '신입 개발자'),
('EMP005', '정우진', '개발자', 10, '시니어', '불가능', 'PROJ002', '2024-04-01', '화면보안, C++, Python', '보안 전문가'),
('EMP006', '한소영', '수행인력', 4, '중급', '가능', NULL, '2024-01-20', '출력물보안, 문서화', '기술문서 작성'),
('EMP007', '임대호', '개발자', 6, '중급', '가능', NULL, '2024-02-15', '문서보안JSON, Node.js', '풀스택 개발자'),
('EMP008', '송미라', '개발자', 7, '시니어', '불가능', 'PROJ003', '2024-05-01', '개인정보솔루션, Vue.js', 'UI/UX 전문'),
('EMP009', '오준호', '수행인력', 1, '신입', '가능', NULL, '2024-01-10', '테스팅, QA', '신입 QA'),
('EMP010', '윤서현', '개발자', 9, '시니어', '가능', NULL, '2024-03-15', '화면보안, Angular, TypeScript', '프론트엔드 아키텍트');

-- 추가 직원 데이터 (90명 더)
INSERT INTO employees (emp_id, name, role, years_exp, level, available, skills) VALUES
('EMP011', '강동원', '개발자', 4, '중급', '가능', 'Java, Spring, MySQL'),
('EMP012', '김하늘', '수행인력', 2, '신입', '가능', '테스팅, 문서화'),
('EMP013', '이준기', '개발자', 6, '중급', '불가능', 'Python, Django, PostgreSQL'),
('EMP014', '박보영', '개발자', 3, '중급', '가능', 'React, JavaScript, CSS'),
('EMP015', '조인성', '개발자', 8, '시니어', '불가능', 'C#, .NET, SQL Server'),
('EMP016', '전지현', '수행인력', 5, '중급', '가능', 'PM, 일정관리'),
('EMP017', '현빈', '개발자', 7, '시니어', '가능', 'Go, Docker, Kubernetes'),
('EMP018', '손예진', '개발자', 4, '중급', '가능', 'Vue.js, Nuxt.js'),
('EMP019', '이병헌', '개발자', 10, '시니어', '불가능', 'Java, Spring Boot, Redis'),
('EMP020', '김태희', '수행인력', 3, '중급', '가능', 'UI/UX, Figma');

-- 나머지 80명 (간단하게)
INSERT INTO employees (emp_id, name, role, years_exp, level, available, skills) 
SELECT 
    CONCAT('EMP', LPAD(n + 20, 3, '0')),
    CONCAT('직원', n + 20),
    CASE WHEN n % 3 = 0 THEN '수행인력' ELSE '개발자' END,
    (n % 10) + 1,
    CASE 
        WHEN (n % 10) + 1 <= 2 THEN '신입'
        WHEN (n % 10) + 1 <= 6 THEN '중급'
        ELSE '시니어'
    END,
    CASE WHEN n % 4 = 0 THEN '불가능' ELSE '가능' END,
    CASE 
        WHEN n % 5 = 0 THEN '문서보안JSON, Java'
        WHEN n % 5 = 1 THEN '문서보안BLUE, React'
        WHEN n % 5 = 2 THEN '개인정보솔루션, Python'
        WHEN n % 5 = 3 THEN '화면보안, C++'
        ELSE '출력물보안, JavaScript'
    END
FROM (
    SELECT @row := @row + 1 as n
    FROM (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t1,
         (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7) t2,
         (SELECT @row := 0) r
    LIMIT 80
) numbers;

-- 프로젝트 샘플 데이터 (10개)
INSERT INTO projects (project_id, project_name, type, complexity, total_mm, duration_months, start_date, end_date, status, required_skills, notes) VALUES
('PROJ001', '차세대 문서보안 시스템', '웹 개발', '복잡', 24, 6, '2024-01-01', '2024-06-30', '진행중', '문서보안JSON, Java, Spring', '핵심 보안 프로젝트'),
('PROJ002', '모바일 보안 앱', '모바일 앱', '보통', 18, 4, '2024-02-01', '2024-05-31', '진행중', '화면보안, React Native', '모바일 전용 보안'),
('PROJ003', '개인정보 관리 플랫폼', '웹 개발', '복잡', 30, 8, '2024-01-15', '2024-09-15', '진행중', '개인정보솔루션, Vue.js', '대규모 플랫폼'),
('PROJ004', '출력물 보안 솔루션', '시스템 구축', '단순', 12, 3, '2024-03-01', '2024-05-31', '계획중', '출력물보안, C++', '프린터 보안'),
('PROJ005', 'AI 기반 보안 분석', '데이터 분석', '복잡', 36, 12, '2024-04-01', '2025-03-31', '계획중', 'Python, TensorFlow', 'AI 프로젝트'),
('PROJ006', '클라우드 보안 서비스', '클라우드', '보통', 20, 5, '2024-02-15', '2024-07-15', '진행중', 'AWS, Docker', '클라우드 네이티브'),
('PROJ007', '보안 대시보드', '웹 개발', '단순', 8, 2, '2024-05-01', '2024-06-30', '계획중', 'React, D3.js', '시각화 프로젝트'),
('PROJ008', '레거시 시스템 마이그레이션', '시스템 구축', '복잡', 40, 10, '2023-10-01', '2024-07-31', '진행중', 'Java, Spring, Oracle', '기존 시스템 개선'),
('PROJ009', '보안 API 게이트웨이', '백엔드', '보통', 16, 4, '2024-03-15', '2024-07-15', '계획중', 'Node.js, Express', 'API 보안'),
('PROJ010', '통합 보안 솔루션', '시스템 통합', '복잡', 50, 15, '2024-01-01', '2025-03-31', '진행중', '모든 보안 기술', '통합 프로젝트');
