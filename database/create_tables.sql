-- 프로젝트 인력 배정 시스템 DB 생성 스크립트

-- 직원 테이블 생성
CREATE TABLE employees (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(50) NOT NULL,
    role NVARCHAR(20) NOT NULL,
    level NVARCHAR(10) NOT NULL,
    department NVARCHAR(30) NOT NULL,
    is_available BIT NOT NULL DEFAULT 1,
    efficiency DECIMAL(3,2) NOT NULL DEFAULT 1.0,
    current_project_end_date DATE NULL,
    skills NVARCHAR(200) NULL,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);

-- 프로젝트 테이블 생성
CREATE TABLE projects (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) NOT NULL,
    type NVARCHAR(20) NOT NULL,
    man_months DECIMAL(5,2) NOT NULL,
    duration_months DECIMAL(4,2) NOT NULL,
    complexity NVARCHAR(10) NOT NULL,
    team_experience NVARCHAR(10) NOT NULL,
    status NVARCHAR(20) DEFAULT 'PLANNING',
    created_at DATETIME2 DEFAULT GETDATE()
);

-- 프로젝트 배정 테이블 생성
CREATE TABLE project_assignments (
    id INT PRIMARY KEY IDENTITY(1,1),
    project_id INT NOT NULL,
    employee_id INT NOT NULL,
    role NVARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    allocation_percentage DECIMAL(3,0) DEFAULT 100,
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

-- 인덱스 생성
CREATE INDEX IX_employees_role ON employees(role);
CREATE INDEX IX_employees_available ON employees(is_available);
CREATE INDEX IX_projects_status ON projects(status);
CREATE INDEX IX_assignments_project ON project_assignments(project_id);
CREATE INDEX IX_assignments_employee ON project_assignments(employee_id);