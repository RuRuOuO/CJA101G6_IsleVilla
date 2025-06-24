package com.islevilla.jay.hibernate.util.compositeQuery;

import com.islevilla.jay.productOrder.model.ProductOrder;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HibernateUtil_CompositeQuery_ProductOrder {

    public static Predicate get_aPredicate_For_AnyDB(CriteriaBuilder builder, Root<ProductOrder> root, String columnName, String value) {

        Predicate predicate = null;

        if ("productOrderId".equals(columnName)) // 用於Integer
            predicate = builder.equal(root.get(columnName), Integer.valueOf(value));
        else if ("orderAmount".equals(columnName) || "discountAmount".equals(columnName)) // 用於Double
            predicate = builder.equal(root.get(columnName), Double.valueOf(value));
        else if ("memberId".equals(columnName)) // memberId要對應到member.memberId
            predicate = builder.equal(root.get("member").get("memberId"), Integer.valueOf(value));
        else if ("couponId".equals(columnName)) // 用於Integer
            predicate = builder.equal(root.get(columnName), Integer.valueOf(value));
        else if ("paymentMethod".equals(columnName)) // 用於Byte
            predicate = builder.equal(root.get(columnName), Byte.valueOf(value));
        else if ("orderStatus".equals(columnName)) // 用於Byte
            predicate = builder.equal(root.get(columnName), Byte.valueOf(value));
        // orderTime 交由下方getAllC處理

        return predicate;
    }

    @SuppressWarnings("unchecked")
    public static List<ProductOrder> getAllC(Map<String, String[]> map, Session session) {
        Transaction tx = session.beginTransaction();
        List<ProductOrder> list = null;
        try {
            // 【●創建 CriteriaBuilder】
            CriteriaBuilder builder = session.getCriteriaBuilder();
            // 【●創建 CriteriaQuery】
            CriteriaQuery<ProductOrder> criteriaQuery = builder.createQuery(ProductOrder.class);
            // 【●創建 Root】
            Root<ProductOrder> root = criteriaQuery.from(ProductOrder.class);

            List<Predicate> predicateList = new ArrayList<Predicate>();
            
            Set<String> keys = map.keySet();
            int count = 0;
            for (String key : keys) {
                String value = map.get(key)[0];
                if (value != null && value.trim().length() != 0 && !"action".equals(key)) {
                    if ("orderTime".equals(key)) {
                        // 日期查詢，查詢當天00:00~23:59:59
                        java.time.LocalDate date = java.time.LocalDate.parse(value.trim());
                        java.time.LocalDateTime start = date.atStartOfDay();
                        java.time.LocalDateTime end = date.atTime(23, 59, 59);
                        predicateList.add(builder.between(root.get("orderTime"), start, end));
                    } else {
                        predicateList.add(get_aPredicate_For_AnyDB(builder, root, key, value.trim()));
                    }
                    count++;
                    System.out.println("有送出查詢資料的欄位數count = " + count);
                }
            }
            System.out.println("predicateList.size()="+predicateList.size());
            criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
            criteriaQuery.orderBy(builder.asc(root.get("productOrderId")));
            // 【●最後完成創建 javax.persistence.Query●】
            Query query = session.createQuery(criteriaQuery);
            list = query.getResultList();

            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null)
                tx.rollback();
            throw ex;
        } finally {
            session.close();
        }

        return list;
    }
} 
