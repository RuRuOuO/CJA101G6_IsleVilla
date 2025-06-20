package com.islevilla.wei.room.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomRVDetailService {
    @Autowired
    private RoomRVDetailRepository roomRVDetailRepository;
    @Autowired
    private RoomRVOrderService roomRVOrderService;

    // 依照訂單編號查詢出訂單明細
    public List<RoomRVDetail> getDetailsByRoomRVOrderId(Integer roomReservationId) {
        RoomRVOrder roomRVOrder = roomRVOrderService.getById(roomReservationId);
        if (roomRVOrder == null) {
            return List.of();
        }
        return roomRVDetailRepository.findByRoomRVOrder(roomRVOrder);
    }
}