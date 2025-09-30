package com.markany.employee;

public class EmployeeRecommendation {
    private Employee employee;
    private String reason;
    
    public EmployeeRecommendation(Employee employee, String reason) {
        this.employee = employee;
        this.reason = reason;
    }
    
    public Employee getEmployee() {
        return employee;
    }
    
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}