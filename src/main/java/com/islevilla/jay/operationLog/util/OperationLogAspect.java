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

    // 攔截所有 @PostMapping 但排除 update 和 delete 方法（只記錄純新增）
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping) && !execution(* *..*update*(..)) && !execution(* *..*delete*(..))")
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

    // 攔截 @PostMapping 且方法名稱包含 delete（支援用 PostMapping 做刪除）
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping) && execution(* *..*delete*(..))")
    public void postDeleteMethods() {}

    // 攔截 @GetMapping 且方法名稱包含 delete（支援用 GetMapping 做刪除）
    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping) && execution(* *..*delete*(..))")
    public void getDeleteMethods() {}

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

    @AfterReturning("postDeleteMethods()")
    public void logPostDelete(JoinPoint joinPoint) {
        logByType(joinPoint, "刪除");
    }

    @AfterReturning("getDeleteMethods()")
    public void logGetDelete(JoinPoint joinPoint) {
        logByType(joinPoint, "刪除");
    }

    // 根據操作類型記錄日誌
    private void logByType(JoinPoint joinPoint, String actionType) {
        HttpSession session = getSession();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        System.out.println("[AOP DEBUG] actionType=" + actionType + ", class=" + className + ", method=" + methodName + ", session=" + (session != null) + ", employee=" + (session != null && session.getAttribute("employee") != null));
        if (session == null) return;
        Employee employee = (Employee) session.getAttribute("employee");
        if (employee == null) return;
        // 類名轉中文
        String entityZh = getEntityZhName(className) + "資料";
        String description = actionType + entityZh;
        logUtil.logOperation(employee, description);
    }

    // 類別名稱轉中文
    private String getEntityZhName(String className) {
        if (className == null) return "";
        if (className.contains("Department")) return "部門";
        if (className.contains("Coupon")) return "優惠券";
        if (className.contains("Room")) return "房間";
        if (className.contains("RoomType")) return "房型";
        if (className.contains("Employee")) return "員工";
        if (className.contains("ProductOrder")) return "商品訂單";
        if (className.contains("Promotion")) return "促銷";
        if (className.contains("ProductApi")) return "商品";
        if (className.contains("ProductCategory")) return "商品分類";
        // 你可以依需求繼續擴充
        // 預設去掉 Controller 結尾
        if (className.endsWith("Controller")) {
            return className.replace("Controller", "");
        }
        return className;
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