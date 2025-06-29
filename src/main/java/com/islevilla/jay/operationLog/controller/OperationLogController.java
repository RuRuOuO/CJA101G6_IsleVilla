package com.islevilla.jay.operationLog.controller;

import com.islevilla.jay.operationLog.model.OperationLog;
import com.islevilla.jay.operationLog.model.OperationLogService;
import com.islevilla.yin.employee.model.Employee;
import com.islevilla.yin.employee.model.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/backend/operation-log")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private EmployeeService employeeService;

    /**
     * 顯示操作日誌列表（一次查詢所有資料，供DataTables前端分頁排序）
     */
    @GetMapping("/list")
    public String listOperationLogs(Model model) {
        List<OperationLog> operationLogs = operationLogService.getAllOperationLogs();
        List<Employee> employees = employeeService.getAllEmployees();
        long todayCount = operationLogService.getTodayOperationCount();
        long totalCount = operationLogService.getTotalOperationCount();

        model.addAttribute("operationLogs", operationLogs);
        model.addAttribute("employees", employees);
        model.addAttribute("todayCount", todayCount);
        model.addAttribute("totalCount", totalCount);

        return "back-end/operation-log/list";
    }

    /**
     * 顯示操作日誌詳情
     */
    @GetMapping("/detail/{id}")
    public String showOperationLogDetail(@PathVariable Integer id, Model model) {
        OperationLog operationLog = operationLogService.getOperationLogById(id);
        if (operationLog == null) {
            return "redirect:/backend/operation-log/list";
        }
        model.addAttribute("operationLog", operationLog);
        return "back-end/operation-log/detail";
    }

    /**
     * 刪除操作日誌
     */
    @PostMapping("/delete/{id}")
    public String deleteOperationLog(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            operationLogService.deleteOperationLog(id);
            redirectAttributes.addFlashAttribute("success", "操作日誌已成功刪除");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "刪除操作日誌時發生錯誤");
        }
        return "redirect:/backend/operation-log/list";
    }

    /**
     * 清理舊日誌
     */
    @PostMapping("/cleanup")
    public String cleanupOldLogs(@RequestParam String beforeDate, RedirectAttributes redirectAttributes) {
        try {
            LocalDate before = LocalDate.parse(beforeDate);
            operationLogService.cleanupOldLogs(before);
            redirectAttributes.addFlashAttribute("success", "已成功清理 " + beforeDate + " 之前的操作日誌");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "清理操作日誌時發生錯誤");
        }
        return "redirect:/backend/operation-log/list";
    }

    /**
     * 手動新增操作日誌（用於測試）
     */
    @PostMapping("/add")
    public String addOperationLog(@RequestParam Integer employeeId, 
                                 @RequestParam String description,
                                 RedirectAttributes redirectAttributes) {
        try {
            Employee employee = employeeService.getEmployeeById(employeeId);
            if (employee != null) {
                operationLogService.addLog(employee, description);
                redirectAttributes.addFlashAttribute("success", "操作日誌已成功新增");
            } else {
                redirectAttributes.addFlashAttribute("error", "找不到指定的員工");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "新增操作日誌時發生錯誤");
        }
        return "redirect:/backend/operation-log/list";
    }
} 