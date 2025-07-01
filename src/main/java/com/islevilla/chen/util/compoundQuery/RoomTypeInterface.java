package com.islevilla.chen.util.compoundQuery;

import java.util.List;

import com.islevilla.chen.roomType.model.RoomType;

public interface RoomTypeInterface {

	public List<RoomType> searchRoomTypes(Integer roomTypeId, Byte roomTypeSaleStatus) ;

}
