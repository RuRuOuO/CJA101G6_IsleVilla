package com.islevilla.chen.util.compoundQuery;

import java.util.List;

import com.islevilla.chen.room.model.Room;

public interface RoomInterface {
	List<Room> searchRooms(Integer roomId, Integer roomTypeId, Byte roomStatus) ;
}
