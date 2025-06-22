package com.islevilla.chen.room.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface RoomRepository extends JpaRepository<Room, Integer>{
   
    // 根據房型ID查詢房間
    List<Room> findByRoomTypeId(Integer roomTypeId);
    
    // 根據房間狀態查詢房間
    List<Room> findByRoomStatus(Byte roomStatus);
    
    // 根據房型ID和房間狀態查詢房間
    List<Room> findByRoomTypeIdAndRoomStatus(Integer roomTypeId, Byte roomStatus);

}
