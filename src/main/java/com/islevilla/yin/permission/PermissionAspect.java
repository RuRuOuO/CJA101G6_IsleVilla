package com.islevilla.yin.permission;

import com.islevilla.yin.permission.model.PermissionRepository;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private PermissionRepository permissionRepository;

    // 定義切面，針對被@PermissionCheck標註的方法
    @Pointcut("@annotation(permissionCheck)")
    public void permissionPointcut(PermissionCheck permissionCheck) {}

    // 在方法執行前進行權限檢查
    @Before("permissionPointcut(permissionCheck)")
    public void checkPermission(PermissionCheck permissionCheck) throws Exception {
        String[] requiredPermissions = permissionCheck.value();  // 獲取註解中的所有權限名稱

        // 假設用戶的所有權限名稱存儲在某個服務中（例如 UserService）
        List<String> currentUserPermissions = getCurrentUserPermissions();  // 從用戶會話或資料庫獲取用戶的所有權限

        // 檢查用戶是否擁有所需的任意一個權限
        boolean hasPermission = Arrays.stream(requiredPermissions)
                .anyMatch(currentUserPermissions::contains);

        if (!hasPermission) {
            throw new Exception("權限不足，無法執行該操作");
        }
    }

    // 模擬獲取當前用戶所有權限的方法（這需要依據你的需求實現）
    private List<String> getCurrentUserPermissions() {
        // 假設當前用戶權限存儲在 session 或其他地方
        return List.of("op_r", "cs_r");  // 這裡返回一個預設權限列表，你應該根據實際情況調整
    }
}
