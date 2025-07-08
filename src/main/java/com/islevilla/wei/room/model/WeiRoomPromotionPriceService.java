package com.islevilla.wei.room.model;

import com.islevilla.patty.roompromotionprice.model.RoomPromotionPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeiRoomPromotionPriceService {
    @Autowired
    private WeiRoomPromotionPriceRepository repository;

    public List<RoomPromotionPrice> getByPromotionId(Integer promotionId) {
        return repository.findByPromotionIdWithRoomType(promotionId);
    }
}