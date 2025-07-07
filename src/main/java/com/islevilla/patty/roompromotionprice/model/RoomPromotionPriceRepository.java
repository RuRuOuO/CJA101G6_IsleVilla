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


	@Query("SELECT rpp FROM RoomPromotionPrice rpp JOIN FETCH rpp.roomType JOIN FETCH rpp.promotion")
	List<RoomPromotionPrice> findAllWithRoomTypeAndPromotion();
	
	@Query("SELECT rpp FROM RoomPromotionPrice rpp JOIN FETCH rpp.roomType JOIN FETCH rpp.promotion")
	List<RoomPromotionPrice> findAllWithJoins();

	@Query("SELECT r FROM RoomPromotionPrice r " +
	       "JOIN FETCH r.roomType " +
	       "JOIN FETCH r.promotion " +
	       "WHERE r.roomType.roomTypeId = :roomTypeId " +
	       "AND r.promotion.promotionStartDate <= :checkin " +
	       "AND r.promotion.promotionEndDate >= :checkin")
	List<RoomPromotionPrice> findAllValidPromotionPrices(@Param("roomTypeId") Integer roomTypeId, @Param("checkin") java.time.LocalDate checkin);
}