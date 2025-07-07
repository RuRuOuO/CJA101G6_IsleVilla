package com.islevilla.patty.roompromotionprice.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RoomPromotionPriceRepository extends JpaRepository<RoomPromotionPrice, RoomPromotionPriceId> {

	
	@Query("SELECT rpp FROM RoomPromotionPrice rpp " +
	           "JOIN FETCH rpp.roomType " +
	           "JOIN FETCH rpp.promotion")
	    List<RoomPromotionPrice> findAllWithDetails();
	
	@Transactional
	@Modifying
	@Query(value = "delete from roomPromotionPrice where roomPromotionId =?1", nativeQuery = true)
	void deleteByRoomPromotionId(int roomPromotionId);

	//● (自訂)條件查詢
//	@Query(value = "from Promotion where roomPromotionId=?1 and roomPromotionTitle like?2 and promotionStartDate=?3 and promotionEndDate=?4 order by roomPromotionId")
//	List<Promotion> findByOthers(int roompromotionId , String roompromotionTitle , java.sql.Date promotionStartDate, java.sql.Date promotionEndDate);

	@Query("SELECT r FROM RoomPromotionPrice r WHERE r.roomType.roomTypeId = :roomTypeId AND r.promotionStartDate <= :checkin AND r.promotionEndDate >= :checkin")
	List<RoomPromotionPrice> findAllValidPromotionPrices(@Param("roomTypeId") Integer roomTypeId, @Param("checkin") java.time.LocalDate checkin);

	@Query("SELECT rpp FROM RoomPromotionPrice rpp JOIN FETCH rpp.roomType JOIN FETCH rpp.promotion")
	List<RoomPromotionPrice> findAllWithRoomTypeAndPromotion();
	
	@Query("SELECT rpp FROM RoomPromotionPrice rpp JOIN FETCH rpp.roomType JOIN FETCH rpp.promotion")
	List<RoomPromotionPrice> findAllWithJoins();
}