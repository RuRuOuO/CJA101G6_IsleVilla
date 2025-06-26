package com.islevilla.jay.operationLog.util;

import com.islevilla.yin.employee.model.Employee;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private LogUtil logUtil;

    // 攔截所有 @PostMapping 但排除 update 方法
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping) && !execution(* *..*update*(..))")
    public void postCreateMethods() {}

    // 攔截所有 @PutMapping
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putMappingMethods() {}

    // 攔截所有Controller的刪除操作
    @Pointcut("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void deleteMappingMethods() {}

    // 攔截 @PostMapping 且方法名稱包含 update
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping) && execution(* *..*update*(..))")
    public void postUpdateMethods() {}

    @AfterReturning("postCreateMethods()")
    public void logCreate(JoinPoint joinPoint) {
        logByType(joinPoint, "新增");
    }

    @AfterReturning("putMappingMethods()")
    public void logUpdate(JoinPoint joinPoint) {
        logByType(joinPoint, "修改");
    }

    @AfterReturning("postUpdateMethods()")
    public void logPostUpdate(JoinPoint joinPoint) {
        logByType(joinPoint, "修改");
    }

    // 刪除操作自動記錄
    @AfterReturning("deleteMappingMethods()")
    public void logDelete(JoinPoint joinPoint) {
        logByType(joinPoint, "刪除");
    }

    // 根據操作類型記錄日誌
    private void logByType(JoinPoint joinPoint, String actionType) {
        // 取得HttpSession
        HttpSession session = getSession();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        System.out.println("[AOP DEBUG] actionType=" + actionType + ", class=" + className + ", method=" + methodName + ", session=" + (session != null) + ", employee=" + (session != null && session.getAttribute("employee") != null));
        if (session == null) return;
        Employee employee = (Employee) session.getAttribute("employee");
        if (employee == null) return;
        // 取得操作目標（類名+方法名）
        String description = actionType + " " + className + "." + methodName;
        logUtil.logOperation(employee, description);
    }

    // 取得HttpSession
    private HttpSession getSession() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            return request.getSession(false);
        }
        return null;
    }
} 