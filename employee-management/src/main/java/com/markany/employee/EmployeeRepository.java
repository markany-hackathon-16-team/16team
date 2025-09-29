package com.markany.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    List<Employee> findByRole(Employee.Role role);
    long countByCurrentProjectIsNotNull();
    
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.currentProject = :projectId")
    long countByCurrentProject(@Param("projectId") String projectId);
}
