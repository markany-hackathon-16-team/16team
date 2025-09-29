# 데이터베이스 설정

## SQL Server 설정

### 1. 테이블 생성
```sql
-- create_tables.sql 실행
sqlcmd -S localhost -d ProjectAllocation -i create_tables.sql
```

### 2. 샘플 데이터 삽입
```sql
-- insert_sample_data.sql 실행
sqlcmd -S localhost -d ProjectAllocation -i insert_sample_data.sql
```

## 연결 문자열 예시

```csharp
"Server=localhost;Database=ProjectAllocation;Trusted_Connection=true;"
```

## JSON 데이터 활용

`employees.json` 파일은 SQL Server 없이도 테스트할 수 있는 JSON 형태의 데이터입니다.

## 테이블 구조

### employees 테이블
- `id`: 직원 고유 ID
- `name`: 직원 이름
- `role`: 역할 (개발자, 기획자, 디자이너)
- `level`: 경력 수준 (신입, 중급, 시니어)
- `department`: 소속 부서
- `is_available`: 현재 가용 여부
- `efficiency`: 업무 효율성 (0.7 ~ 1.3)
- `current_project_end_date`: 현재 프로젝트 종료일
- `skills`: 보유 기술/스킬

### projects 테이블
- 프로젝트 정보 저장

### project_assignments 테이블
- 프로젝트별 인력 배정 이력