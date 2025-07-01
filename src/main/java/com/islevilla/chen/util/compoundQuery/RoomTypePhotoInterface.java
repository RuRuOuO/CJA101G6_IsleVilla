package com.islevilla.chen.util.compoundQuery;

import java.util.List;

import com.islevilla.chen.roomTypePhoto.model.RoomTypePhoto;


public interface RoomTypePhotoInterface {
	List<RoomTypePhoto> search(Integer roomTypePhotoId, Integer roomTypeId) ;
}
