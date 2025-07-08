package com.islevilla.wei.room.model;

import com.islevilla.patty.roompromotionprice.model.RoomPromotionPrice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WeiRoomPromotionPriceRepository extends Repository<RoomPromotionPrice, Integer> {
    @Query("SELECT rpp FROM RoomPromotionPrice rpp JOIN FETCH rpp.roomType WHERE rpp.promotion.roomPromotionId = :promotionId")
    List<RoomPromotionPrice> findByPromotionIdWithRoomType(@Param("promotionId") Integer promotionId);
}