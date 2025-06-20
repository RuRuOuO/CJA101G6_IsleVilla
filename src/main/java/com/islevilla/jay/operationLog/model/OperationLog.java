package com.islevilla.jay.operationLog.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "operation_log")
public class OperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operation_log_id", updatable = false)
    private Integer operationLogId;

    @ManyToOne
    @JoinColumn(name = "employee_Id", nullable = false)
    private Employee employee;

    @Column(name = "operation_time", nullable = false)
    private LocalDateTime operationTime;

    @Column(name = "log_description", nullable = false, length = 100)
    private String logDescription;

    public OperationLog() {

    }


    public Integer getOperationLogId() {
        return operationLogId;
    }

    public void setOperationLogId(Integer operationLogId) {
        this.operationLogId = operationLogId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDateTime getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(LocalDateTime operationTime) {
        this.operationTime = operationTime;
    }

    public String getLogDescription() {
        return logDescription;
    }

    public void setLogDescription(String logDescription) {
        this.logDescription = logDescription;
    }
}
