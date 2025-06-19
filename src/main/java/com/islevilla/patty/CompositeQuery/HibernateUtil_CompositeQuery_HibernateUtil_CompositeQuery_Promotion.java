/*
 *  1. 萬用複合查詢-可由客戶端隨意增減任何想查詢的欄位
 *  2. 為了避免影響效能:
 *        所以動態產生萬用SQL的部份,本範例無意採用MetaData的方式,也只針對個別的Table自行視需要而個別製作之
 * */

package com.islevilla.patty.CompositeQuery;

import org.hibernate.Session;
import org.hibernate.Transaction;

//import hibernate.util.HibernateUtil;
import java.util.*;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery; //Hibernate 5.2 開始 取代原 org.hibernate.Criteria 介面
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.Query; //Hibernate 5 開始 取代原 org.hibernate.Query 介面

import com.islevilla.patty.model.EmpVO;
import com.dept.model.DeptVO;

public class HibernateUtil_CompositeQuery_Emp3 {

	public static Predicate get_aPredicate_For_AnyDB(CriteriaBuilder builder, Root<Promotion> root, String columnName, String value) {

		Predicate predicate = null;

		if ("empno".equals(columnName)) // 用於Integer
			predicate = builder.equal(root.get(columnName), Integer.valueOf(value));
		else if ("sal".equals(columnName) || "comm".equals(columnName)) // 用於Double
			predicate = builder.equal(root.get(columnName), Double.valueOf(value));
		else if ("ename".equals(columnName) || "job".equals(columnName)) // 用於varchar
			predicate = builder.like(root.get(columnName), "%" + value + "%");
		else if ("hiredate".equals(columnName)) // 用於date
			predicate = builder.equal(root.get(columnName), java.sql.Date.valueOf(value));
		else if ("deptno".equals(columnName)) {
			DeptVO deptVO = new DeptVO();
			deptVO.setDeptno(Integer.valueOf(value));
			predicate = builder.equal(root.get("deptVO"), deptVO);
		}

		return predicate;
	}

	@SuppressWarnings("unchecked")
	public static List<EmpVO> getAllC(Map<String, String[]> map, Session session) {

//		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		List<EmpVO> list = null;
		try {
			// 【●創建 CriteriaBuilder】
			CriteriaBuilder builder = session.getCriteriaBuilder();
			// 【●創建 CriteriaQuery】
			CriteriaQuery<EmpVO> criteriaQuery = builder.createQuery(EmpVO.class);
			// 【●創建 Root】
			Root<EmpVO> root = criteriaQuery.from(EmpVO.class);

			List<Predicate> predicateList = new ArrayList<Predicate>();
			
			Set<String> keys = map.keySet();
			int count = 0;
			for (String key : keys) {
				String value = map.get(key)[0];
				if (value != null && value.trim().length() != 0 && !"action".equals(key)) {
					count++;
					predicateList.add(get_aPredicate_For_AnyDB(builder, root, key, value.trim()));
					System.out.println("有送出查詢資料的欄位數count = " + count);
				}
			}
			System.out.println("predicateList.size()="+predicateList.size());
			criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
			criteriaQuery.orderBy(builder.asc(root.get("empno")));
			// 【●最後完成創建 javax.persistence.Query●】
			Query query = session.createQuery(criteriaQuery); //javax.persistence.Query; //Hibernate 5 開始 取代原 org.hibernate.Query 介面
			list = query.getResultList();

			tx.commit();
		} catch (RuntimeException ex) {
			if (tx != null)
				tx.rollback();
			throw ex; // System.out.println(ex.getMessage());
		} finally {
			session.close();
			// HibernateUtil.getSessionFactory().close();
		}

		return list;
	}
}
