// https://docs.spring.io/spring-data/jpa/docs/current/reference/html/

package com.islevilla.patty.promotion.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

	@Transactional
	@Modifying
	@Query(value = "delete from emp3 where empno =?1", nativeQuery = true)
	void deleteByPromotionId(int promotionId);

	//● (自訂)條件查詢
//	@Query(value = "from Promotion where roomPromotionId=?1 and roomPromotionTitle like?2 and promotionStartDate=?3 and promotionEndDate=?4 order by roomPromotionId")
//	List<Promotion> findByOthers(int roompromotionId , String roompromotionTitle , java.sql.Date promotionStartDate, java.sql.Date promotionEndDate);
}