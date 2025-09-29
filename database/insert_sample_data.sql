-- 샘플 직원 데이터 삽입

INSERT INTO employees (name, role, level, department, is_available, efficiency, current_project_end_date, skills) VALUES
('김철수', '개발자', '시니어', '개발팀', 1, 1.2, NULL, 'C#,React,SQL'),
('이영희', '개발자', '중급', '개발팀', 1, 1.0, NULL, 'Java,Spring,MySQL'),
('박민수', '개발자', '신입', '개발팀', 0, 0.8, '2024-02-15', 'Python,Django'),
('최지은', '기획자', '시니어', '기획팀', 1, 1.1, NULL, '기획,분석,문서화'),
('정현우', '기획자', '중급', '기획팀', 1, 0.9, NULL, '요구사항분석,UI설계'),
('강소영', '디자이너', '시니어', '디자인팀', 1, 1.0, NULL, 'UI/UX,Figma,Photoshop'),
('윤태호', '디자이너', '중급', '디자인팀', 0, 0.9, '2024-03-01', '웹디자인,모바일디자인'),
('임수진', '개발자', '시니어', '개발팀', 1, 1.3, NULL, 'React,Node.js,MongoDB'),
('조민호', '개발자', '중급', '개발팀', 0, 1.0, '2024-02-07', 'Vue.js,PHP,PostgreSQL'),
('한지원', '기획자', '신입', '기획팀', 1, 0.7, NULL, '기획,리서치'),
('송민정', '개발자', '중급', '개발팀', 1, 0.95, NULL, 'Angular,TypeScript,Firebase'),
('김도현', '디자이너', '신입', '디자인팀', 1, 0.8, NULL, 'Sketch,Adobe XD,일러스트'),
('이수현', '개발자', '시니어', '개발팀', 0, 1.25, '2024-02-20', 'Flutter,Dart,iOS'),
('박준영', '기획자', '중급', '기획팀', 1, 0.95, NULL, 'Agile,Scrum,프로젝트관리'),
('최민아', '개발자', '중급', '개발팀', 1, 1.05, NULL, 'Kotlin,Android,SQLite');

-- 샘플 프로젝트 데이터 삽입
INSERT INTO projects (name, type, man_months, duration_months, complexity, team_experience, status) VALUES
('전자상거래 플랫폼', '웹개발', 24.0, 4.0, '복잡', '중급', 'PLANNING'),
('모바일 뱅킹 앱', '모바일앱', 18.0, 3.0, '복잡', '시니어', 'IN_PROGRESS'),
('사내 ERP 시스템', '시스템개발', 36.0, 6.0, '복잡', '중급', 'PLANNING'),
('회사 홈페이지 리뉴얼', '웹개발', 8.0, 2.0, '단순', '중급', 'COMPLETED'),
('IoT 관제 시스템', '시스템개발', 30.0, 5.0, '복잡', '시니어', 'PLANNING');