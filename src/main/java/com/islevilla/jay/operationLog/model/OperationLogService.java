package com.islevilla.jay.operationLog.model;

import com.islevilla.yin.employee.model.Employee;
import org.hibernate.SessionFactory;
import com.islevilla.jay.hibernate.util.compositeQuery.HibernateUtil_CompositeQuery_OperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Set;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Service
public class OperationLogService {

    @Autowired
    private OperationLogRepository operationLogRepository;

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * 新增操作日誌
     */
    public OperationLog addOperationLog(OperationLog operationLog) {
        return operationLogRepository.save(operationLog);
    }

    /**
     * 快速新增操作日誌（簡化版）
     */
    public void addLog(Employee employee, String description) {
        OperationLog log = new OperationLog();
        log.setEmployee(employee);
        log.setOperationTime(LocalDateTime.now());
        log.setLogDescription(description);
        operationLogRepository.save(log);
    }

    /**
     * 根據ID獲取操作日誌
     */
    public OperationLog getOperationLogById(Integer operationLogId) {
        Optional<OperationLog> optional = operationLogRepository.findById(operationLogId);
        return optional.orElse(null);
    }

    /**
     * 獲取所有操作日誌
     */
    public List<OperationLog> getAllOperationLogs() {
        return operationLogRepository.findAll();
    }

    /**
     * 分頁獲取操作日誌
     */
    public Page<OperationLog> getOperationLogsWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("operationTime").descending());
        return operationLogRepository.findAll(pageable);
    }

    /**
     * 根據員工ID獲取操作日誌
     */
    public List<OperationLog> getOperationLogsByEmployeeId(Integer employeeId) {
        return operationLogRepository.findByEmployeeEmployeeId(employeeId);
    }

    /**
     * 根據日期範圍獲取操作日誌
     */
    public List<OperationLog> getOperationLogsByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        return operationLogRepository.findByOperationTimeBetween(startDateTime, endDateTime);
    }

    /**
     * 根據關鍵字搜尋操作日誌
     */
    public List<OperationLog> searchOperationLogsByKeyword(String keyword) {
        return operationLogRepository.findByLogDescriptionContaining(keyword);
    }

    /**
     * 獲取今日操作日誌數量
     */
    public long getTodayOperationCount() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        return operationLogRepository.countByOperationTimeBetween(startOfDay, endOfDay);
    }

    /**
     * 獲取總操作日誌數量
     */
    public long getTotalOperationCount() {
        return operationLogRepository.count();
    }

    /**
     * 刪除操作日誌
     */
    public void deleteOperationLog(Integer operationLogId) {
        if (operationLogRepository.existsById(operationLogId)) {
            operationLogRepository.deleteById(operationLogId);
        }
    }

    /**
     * 清理指定日期之前的操作日誌
     */
    @Transactional
    public void cleanupOldLogs(LocalDate beforeDate) {
        LocalDateTime beforeDateTime = beforeDate.atStartOfDay();
        operationLogRepository.deleteByOperationTimeBefore(beforeDateTime);
    }

    /**
     * 獲取員工操作統計
     */
    public List<Object[]> getEmployeeOperationStats() {
        return operationLogRepository.getEmployeeOperationStats();
    }

    public List<OperationLog> getAll(Map<String, String[]> map) {
        return HibernateUtil_CompositeQuery_OperationLog.getAllC(map, sessionFactory.openSession());
    }

    // 複合查詢分頁
    public Page<OperationLog> getAllWithPagination(Map<String, String[]> map, int page, int size) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        List<OperationLog> list = null;
        long total = 0;
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<OperationLog> criteriaQuery = builder.createQuery(OperationLog.class);
            Root<OperationLog> root = criteriaQuery.from(OperationLog.class);

            List<Predicate> predicateList = new ArrayList<>();
            Set<String> keys = map.keySet();
            for (String key : keys) {
                String value = map.get(key)[0];
                if (value != null && value.trim().length() != 0 && !"action".equals(key)) {
                    if ("operationTime".equals(key)) {
                        LocalDate date = LocalDate.parse(value.trim());
                        LocalDateTime start = date.atStartOfDay();
                        LocalDateTime end = date.atTime(23, 59, 59);
                        predicateList.add(builder.between(root.get("operationTime"), start, end));
                    } else {
                        predicateList.add(HibernateUtil_CompositeQuery_OperationLog.get_aPredicate_For_AnyDB(builder, root, key, value.trim()));
                    }
                }
            }
            criteriaQuery.where(predicateList.toArray(new Predicate[0]));
            criteriaQuery.orderBy(builder.desc(root.get("operationLogId")));
            Query query = session.createQuery(criteriaQuery);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            list = query.getResultList();

            // 查詢總數
            CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
            Root<OperationLog> countRoot = countQuery.from(OperationLog.class);
            countQuery.select(builder.count(countRoot));
            List<Predicate> countPredicates = new ArrayList<>();
            for (String key : keys) {
                String value = map.get(key)[0];
                if (value != null && value.trim().length() != 0 && !"action".equals(key)) {
                    if ("operationTime".equals(key)) {
                        LocalDate date = LocalDate.parse(value.trim());
                        LocalDateTime start = date.atStartOfDay();
                        LocalDateTime end = date.atTime(23, 59, 59);
                        countPredicates.add(builder.between(countRoot.get("operationTime"), start, end));
                    } else {
                        countPredicates.add(HibernateUtil_CompositeQuery_OperationLog.get_aPredicate_For_AnyDB(builder, countRoot, key, value.trim()));
                    }
                }
            }
            countQuery.where(countPredicates.toArray(new Predicate[0]));
            total = session.createQuery(countQuery).getSingleResult();
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null) tx.rollback();
            throw ex;
        } finally {
            session.close();
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("operationTime").descending());
        return new org.springframework.data.domain.PageImpl<>(list, pageable, total);
    }
} 