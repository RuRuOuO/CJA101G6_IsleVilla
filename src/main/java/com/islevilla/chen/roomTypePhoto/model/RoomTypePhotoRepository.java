package com.islevilla.chen.roomTypePhoto.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.util.compoundQuery.RoomTypePhotoInterface;

@Repository
public interface RoomTypePhotoRepository extends JpaRepository<RoomTypePhoto, Integer>, RoomTypePhotoInterface {

	//使用房型查詢圖片
	@Query("SELECT rt FROM RoomType rt JOIN FETCH rt.roomTypePhotos WHERE rt.roomTypeId = :roomTypeId")
	public List<RoomType> findWithPhotos(@Param("roomTypeId") Integer roomTypeId);
	
	public List<RoomTypePhoto> search(Integer roomTypePhotoId, Integer roomTypeId);

}
