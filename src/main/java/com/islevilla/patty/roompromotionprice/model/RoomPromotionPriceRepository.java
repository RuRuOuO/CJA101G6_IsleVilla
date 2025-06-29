//// https://docs.spring.io/spring-data/jpa/docs/current/reference/html/
//
//package com.islevilla.patty.roompromotionprice.model;
//
//import java.util.List;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.transaction.annotation.Transactional;
//
//public interface RoomPromotionPriceRepository extends JpaRepository<RoomPromotionPrice, Integer> {
//
//	@Transactional
//	@Modifying
//	@Query(value = "delete from emp3 where empno =?1", nativeQuery = true)
//	void deleteByRoomPromotionId(int RoomPromotionId);
//
//	//● (自訂)條件查詢
//	@Query(value = "from RoomPromotionPrice where roomPromotionId=?1 and roomPromotionTitle like?2 and promotionStartDate=?3 and promotionEndDate=?4 order by promotionStartDate")
//	List<RoomPromotionPrice> findByOthers(int roomPromotionId , String roomPromotionTitle , java.sql.Date promotionStartDate, java.sql.Date promotionEndDate);
//}

package com.islevilla.patty.roompromotionprice.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomPromotionPriceRepository extends JpaRepository<RoomPromotionPrice, Integer> {
    // 查詢指定房型、日期區間內的所有有效優惠價
    @Query("SELECT r FROM RoomPromotionPrice r JOIN FETCH r.promotion WHERE r.roomType.roomTypeId = :roomTypeId AND :targetDate BETWEEN r.promotionStartDate AND r.promotionEndDate ORDER BY r.promotionStartDate DESC")
    List<RoomPromotionPrice> findAllValidPromotionPrices(@Param("roomTypeId") Integer roomTypeId, @Param("targetDate") LocalDate targetDate);

    // 查詢指定房型、日期區間內的最新一筆優惠價
    @Query("SELECT r FROM RoomPromotionPrice r WHERE r.roomType.roomTypeId = :roomTypeId AND :targetDate BETWEEN r.promotionStartDate AND r.promotionEndDate ORDER BY r.promotionStartDate DESC")
    Optional<RoomPromotionPrice> findValidPromotionPrice(@Param("roomTypeId") Integer roomTypeId, @Param("targetDate") LocalDate targetDate);
}