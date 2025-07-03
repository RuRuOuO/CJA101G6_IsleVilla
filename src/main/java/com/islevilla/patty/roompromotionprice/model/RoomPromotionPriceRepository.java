// https://docs.spring.io/spring-data/jpa/docs/current/reference/html/

package com.islevilla.patty.roompromotionprice.model;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface RoomPromotionPriceRepository extends JpaRepository<RoomPromotionPrice, Integer> {

	@Transactional
	@Modifying
	@Query(value = "delete from promotion where roomPromotionId =?1", nativeQuery = true)
	void deleteByRoomPromotionId(int roomPromotionId);

	//● (自訂)條件查詢
	@Query(value = "from Promotion where roomPromotionId=?1 and roomPromotionTitle like?2 and promotionStartDate=?3 and promotionEndDate=?4 order by roomPromotionId")
	List<RoomPromotionPrice> findByOthers(int roompromotionId , String roompromotionTitle , java.sql.Date promotionStartDate, java.sql.Date promotionEndDate);
	
	
	List<RoomPromotionPrice> findAllValidPromotionPrices(Integer i, LocalDate a);
}