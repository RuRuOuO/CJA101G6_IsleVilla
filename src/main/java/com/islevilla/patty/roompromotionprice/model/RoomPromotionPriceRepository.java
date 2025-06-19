// https://docs.spring.io/spring-data/jpa/docs/current/reference/html/

package com.emp.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface EmpRepository extends JpaRepository<EmpVO, Integer> {

	@Transactional
	@Modifying
	@Query(value = "delete from emp3 where empno =?1", nativeQuery = true)
	void deleteByEmpno(int empno);

	//● (自訂)條件查詢
	@Query(value = "from EmpVO where empno=?1 and ename like?2 and hiredate=?3 order by empno")
	List<EmpVO> findByOthers(int empno , String ename , java.sql.Date hiredate);
}