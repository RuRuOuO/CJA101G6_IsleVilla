package com.islevilla.chen.room.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.islevilla.chen.util.compoundQuery.RoomInterface;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer>,RoomInterface{
	//計算每個房型數量
	 @Query("SELECT new com.islevilla.chen.room.model.RoomTypeCount(r.roomTypeId, CAST(COUNT(r) AS integer)) " +
	           "FROM Room r " +
	           "GROUP BY r.roomTypeId")
	 public List<RoomTypeCount> countRoomsByRoomTypeId();
	 
	 
	 //長期不可用房間狀態的房型數量  //0:空房 1:入住中 2:待維修 3:待清潔 4：停用
	 @Query("SELECT new com.islevilla.chen.room.model.RoomTypeCount(r.roomTypeId, CAST(COUNT(r) AS integer)) " +
		       "FROM Room r " +
		       "WHERE r.roomStatus IN (2, 4) " +
		       "GROUP BY r.roomTypeId")
	 public List<RoomTypeCount> countRoomsByRoomTypeIdWithStatus();
}
