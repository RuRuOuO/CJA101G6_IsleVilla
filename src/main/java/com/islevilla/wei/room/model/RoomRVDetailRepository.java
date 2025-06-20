package com.islevilla.wei.room.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface RoomRVDetailRepository extends JpaRepository<RoomRVDetail, Integer> {
    // 傳進整個RoomRVOrder entity
    List<RoomRVDetail> findByRoomRVOrder(RoomRVOrder roomRVOrder);
}
