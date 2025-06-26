package com.islevilla.jay.hibernate.util.compositeQuery;

import com.islevilla.jay.operationLog.model.OperationLog;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HibernateUtil_CompositeQuery_OperationLog {

    public static Predicate get_aPredicate_For_AnyDB(CriteriaBuilder builder, Root<OperationLog> root, String columnName, String value) {
        Predicate predicate = null;
        if ("operationLogId".equals(columnName))
            predicate = builder.equal(root.get(columnName), Integer.valueOf(value));
        else if ("employeeId".equals(columnName))
            predicate = builder.equal(root.get("employee").get("employeeId"), Integer.valueOf(value));
        else if ("logDescription".equals(columnName))
            predicate = builder.like(root.get(columnName), "%" + value + "%");
        // operationTime 交由下方 getAllC 處理
        return predicate;
    }

    @SuppressWarnings("unchecked")
    public static List<OperationLog> getAllC(Map<String, String[]> map, Session session) {
        Transaction tx = session.beginTransaction();
        List<OperationLog> list = null;
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
                        predicateList.add(get_aPredicate_For_AnyDB(builder, root, key, value.trim()));
                    }
                }
            }
            criteriaQuery.where(predicateList.toArray(new Predicate[0]));
            criteriaQuery.orderBy(builder.desc(root.get("operationLogId")));
            Query query = session.createQuery(criteriaQuery);
            list = query.getResultList();
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null) tx.rollback();
            throw ex;
        } finally {
            session.close();
        }
        return list;
    }
} 