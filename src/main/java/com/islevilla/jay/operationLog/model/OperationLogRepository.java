package com.islevilla.jay.operationLog.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Integer> {

    /**
     * 根據員工ID查詢操作日誌
     */
    List<OperationLog> findByEmployeeEmployeeId(Integer employeeId);

    /**
     * 根據時間範圍查詢操作日誌
     */
    List<OperationLog> findByOperationTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根據關鍵字搜尋操作描述
     */
    List<OperationLog> findByLogDescriptionContaining(String keyword);

    /**
     * 統計指定時間範圍內的操作數量
     */
    long countByOperationTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 刪除指定時間之前的操作日誌
     */
    void deleteByOperationTimeBefore(LocalDateTime beforeTime);

    /**
     * 獲取員工操作統計（員工姓名和操作次數）
     */
    @Query("SELECT ol.employee.employeeName, COUNT(ol) FROM OperationLog ol GROUP BY ol.employee.employeeName ORDER BY COUNT(ol) DESC")
    List<Object[]> getEmployeeOperationStats();

    /**
     * 根據員工ID和時間範圍查詢操作日誌
     */
    @Query("SELECT ol FROM OperationLog ol WHERE ol.employee.employeeId = :employeeId AND ol.operationTime BETWEEN :startTime AND :endTime ORDER BY ol.operationTime DESC")
    List<OperationLog> findByEmployeeIdAndTimeRange(@Param("employeeId") Integer employeeId, 
                                                   @Param("startTime") LocalDateTime startTime, 
                                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 獲取最新的N筆操作日誌
     */
    @Query("SELECT ol FROM OperationLog ol ORDER BY ol.operationTime DESC")
    List<OperationLog> findTopNByOrderByOperationTimeDesc(int limit);
} 