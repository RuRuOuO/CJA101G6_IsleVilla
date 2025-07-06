package com.islevilla.wei.room.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomRVDetailService {
    @Autowired
    private RoomRVDetailRepository roomRVDetailRepository;

    // 查詢全部
    public List<RoomRVDetail> findAll() {
        return roomRVDetailRepository.findAll();
    }

    // 依照訂單編號查詢出訂單明細
    public List<RoomRVDetail> getDetailsByRoomRVOrderId(Integer roomReservationId) {
        return roomRVDetailRepository.findByRoomRVOrderId(roomReservationId);
    }

    public Integer getGuestCountByRoomRVOrder(RoomRVOrder roomRVOrder) {
        return roomRVDetailRepository.countGuestByRoomRVOrder(roomRVOrder);
    }

    public void updateRoomRVDetail(RoomRVDetail roomRVDetail) {
        roomRVDetailRepository.save(roomRVDetail);
    }
}