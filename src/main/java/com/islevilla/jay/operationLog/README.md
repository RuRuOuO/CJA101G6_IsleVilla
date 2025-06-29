# 操作日誌系統使用說明

## 概述
本系統提供完整的操作日誌功能，用於記錄和追蹤系統中所有重要操作，包括新增、修改、刪除、查詢等操作。

## 功能特色

### 1. 完整的日誌記錄
- 自動記錄操作時間
- 記錄操作員工資訊
- 詳細的操作描述
- 支援多種操作類型

### 2. 強大的查詢功能
- 按員工篩選
- 按關鍵字搜尋
- 按日期範圍查詢
- 分頁顯示

### 3. 統計分析
- 今日操作統計
- 總操作數統計
- 員工操作排名
- 圖表化顯示

### 4. 系統管理
- 清理舊日誌
- 刪除單筆日誌
- 查看日誌詳情

## 使用方法

### 1. 在Controller中使用日誌記錄

```java
@Autowired
private LogUtil logUtil;

// 記錄新增操作
@PostMapping("/add")
public String addEntity(@ModelAttribute Entity entity, HttpSession session) {
    // 業務邏輯...
    
    // 記錄日誌
    Employee employee = (Employee) session.getAttribute("employee");
    if (employee != null) {
        logUtil.logCreate(employee, "實體名稱", "詳細資訊");
    }
    
    return "redirect:/list";
}

// 記錄更新操作
@PostMapping("/update")
public String updateEntity(@ModelAttribute Entity entity, HttpSession session) {
    // 業務邏輯...
    
    // 記錄日誌
    Employee employee = (Employee) session.getAttribute("employee");
    if (employee != null) {
        logUtil.logUpdate(employee, "實體名稱", "詳細資訊");
    }
    
    return "redirect:/list";
}

// 記錄刪除操作
@GetMapping("/delete")
public String deleteEntity(@RequestParam Integer id, HttpSession session) {
    // 記錄日誌（在刪除前）
    Employee employee = (Employee) session.getAttribute("employee");
    if (employee != null) {
        logUtil.logDelete(employee, "實體名稱", "詳細資訊");
    }
    
    // 刪除邏輯...
    return "redirect:/list";
}
```

### 2. 使用LogUtil工具類

```java
// 基本操作記錄
logUtil.logOperation(employee, "自定義操作描述");

// 登入/登出記錄
logUtil.logLogin(employee);
logUtil.logLogout(employee);

// CRUD操作記錄
logUtil.logCreate(employee, "商品", "商品名稱: " + productName);
logUtil.logUpdate(employee, "優惠券", "代碼: " + couponCode);
logUtil.logDelete(employee, "訂單", "訂單編號: " + orderId);

// 查詢操作記錄
logUtil.logQuery(employee, "客戶資料", "客戶ID: " + customerId);

// 檔案操作記錄
logUtil.logFileOperation(employee, "上傳", fileName);

// 系統操作記錄
logUtil.logSystemOperation(employee, "系統維護");

// 錯誤記錄
logUtil.logError(employee, "資料庫連線失敗");
```

### 3. 從Session獲取員工資訊

```java
// 方法1：直接從Session獲取
Employee employee = (Employee) session.getAttribute("employee");
if (employee != null) {
    logUtil.logOperation(employee, "操作描述");
}

// 方法2：使用LogUtil的便捷方法
logUtil.logOperation(session, "操作描述");
```

## 頁面功能

### 1. 日誌列表頁面 (`/operation-log/list`)
- 顯示所有操作日誌
- 支援搜尋和篩選
- 分頁顯示
- 統計資訊

### 2. 統計分析頁面 (`/operation-log/statistics`)
- 員工操作統計圖表
- 操作分布圓餅圖
- 詳細統計表格

### 3. 日誌詳情頁面 (`/operation-log/detail/{id}`)
- 顯示單筆日誌的完整資訊
- 包含員工詳細資訊

## 資料庫結構

```sql
CREATE TABLE operation_log (
    operation_log_id  INT           AUTO_INCREMENT  PRIMARY KEY,
    employee_id       INT           NOT NULL,
    operation_time    DATETIME      NOT NULL,
    log_description   VARCHAR(100)  NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employee (employee_id)
);
```

## 注意事項

1. **員工登入狀態**：確保員工已登入且Session中有employee物件
2. **日誌描述長度**：log_description欄位限制100字元
3. **效能考量**：大量操作時建議使用非同步記錄
4. **資料清理**：定期清理舊日誌以維持系統效能

## 擴展功能

### 1. 自定義日誌類型
可以在LogUtil中添加更多特定的日誌方法：

```java
public void logLogin(Employee employee, String loginMethod) {
    logOperation(employee, "登入系統 (" + loginMethod + ")");
}

public void logExport(Employee employee, String exportType, int recordCount) {
    logOperation(employee, "匯出" + exportType + "資料，共" + recordCount + "筆");
}
```

### 2. 日誌級別
可以擴展支援不同級別的日誌：

```java
public void logInfo(Employee employee, String description) {
    logOperation(employee, "[INFO] " + description);
}

public void logWarning(Employee employee, String description) {
    logOperation(employee, "[WARNING] " + description);
}

public void logError(Employee employee, String description) {
    logOperation(employee, "[ERROR] " + description);
}
```

## 常見問題

### Q: 如何確保日誌記錄的準確性？
A: 在關鍵操作前後都記錄日誌，並包含足夠的上下文資訊。

### Q: 日誌記錄失敗怎麼辦？
A: 使用try-catch包裝日誌記錄，避免影響主要業務邏輯。

### Q: 如何提高日誌查詢效能？
A: 在operation_time欄位上建立索引，並定期清理舊日誌。 