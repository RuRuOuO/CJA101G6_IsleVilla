package com.islevilla.chen.roomTypePhoto.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface RoomTypePhotoRepository extends JpaRepository<RoomTypePhotoVO, Integer> {

//	@Modifying
//	@Query(value="SELECT rt.room_type_id, rt.room_type_name, rtp.room_type_photo_id, rtp.room_type_photo FROM room_type_photo rtp JOIN room_type rt ON rt.room_type_id=rtp.room_type_id")
//	public RoomTypePhotoVO findWithRoomTypeName(Integer roomTypeId);
	
}
