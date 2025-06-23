package com.islevilla.chen.roomTypePhoto.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.islevilla.chen.roomType.model.RoomType;

@Repository
@Transactional
public interface RoomTypePhotoRepository extends JpaRepository<RoomTypePhoto, Integer> {

//	@Query("SELECT rt FROM RoomType rt JOIN FETCH rt.photos WHERE rt.roomTypeId = :roomTypeId")
//	RoomType findWithPhotos(@Param("roomTypeId") Integer roomTypeId);
	
}
