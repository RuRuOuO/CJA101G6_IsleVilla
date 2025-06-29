package com.islevilla.jay.operationLog.util;

import com.islevilla.jay.operationLog.model.OperationLogService;
import com.islevilla.yin.employee.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;

/**
 * 操作日誌工具類
 * 提供簡便的方法來記錄系統操作日誌
 */
@Component
public class LogUtil {

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 記錄操作日誌（需要員工物件）
     */
    public void logOperation(Employee employee, String description) {
        if (employee != null) {
            operationLogService.addLog(employee, description);
        }
    }

    /**
     * 記錄操作日誌（從Session獲取員工資訊）
     */
    public void logOperation(HttpSession session, String description) {
        Employee employee = (Employee) session.getAttribute("employee");
        if (employee != null) {
            operationLogService.addLog(employee, description);
        }
    }

    /**
     * 記錄登入日誌
     */
    public void logLogin(Employee employee) {
        logOperation(employee, "登入系統");
    }

    /**
     * 記錄登出日誌
     */
    public void logLogout(Employee employee) {
        logOperation(employee, "登出系統");
    }

    /**
     * 記錄新增操作
     */
    public void logCreate(Employee employee, String entityName, String entityInfo) {
        logOperation(employee, "新增" + entityName + ": " + entityInfo);
    }

    /**
     * 記錄更新操作
     */
    public void logUpdate(Employee employee, String entityName, String entityInfo) {
        logOperation(employee, "更新" + entityName + ": " + entityInfo);
    }

    /**
     * 記錄刪除操作
     */
    public void logDelete(Employee employee, String entityName, String entityInfo) {
        logOperation(employee, "刪除" + entityName + ": " + entityInfo);
    }

    /**
     * 記錄查詢操作
     */
    public void logQuery(Employee employee, String queryType, String queryInfo) {
        logOperation(employee, "查詢" + queryType + ": " + queryInfo);
    }

    /**
     * 記錄檔案操作
     */
    public void logFileOperation(Employee employee, String operation, String fileName) {
        logOperation(employee, operation + "檔案: " + fileName);
    }

    /**
     * 記錄系統操作
     */
    public void logSystemOperation(Employee employee, String operation) {
        logOperation(employee, "系統操作: " + operation);
    }

    /**
     * 記錄錯誤操作
     */
    public void logError(Employee employee, String errorDescription) {
        logOperation(employee, "錯誤: " + errorDescription);
    }
} 