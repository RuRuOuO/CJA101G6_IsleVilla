package com.islevilla.chen.roomTypePhoto.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.islevilla.chen.util.compoundQuery.RoomTypePhotoInterface;

@Repository
public interface RoomTypePhotoRepository extends JpaRepository<RoomTypePhoto, Integer>, RoomTypePhotoInterface {

	// 根據房型ID查詢所有照片，按顯示順序排序
	@Query("SELECT rtp FROM RoomTypePhoto rtp WHERE rtp.roomType.roomTypeId = :roomTypeId ORDER BY rtp.displayOrder ASC")
	public List<RoomTypePhoto> findWithPhotos(@Param("roomTypeId") Integer roomTypeId);
	
	//複合查詢
	public List<RoomTypePhoto> search(Integer roomTypePhotoId, Integer roomTypeId);

	// 刪除特定房型的所有照片
    void deleteByRoomTypeRoomTypeId(Integer roomTypeId);

    // 查詢最大顯示順序，方便之後新增圖片增加順序，若該房型還沒有圖片，就預設為 0。
    @Query("SELECT COALESCE(MAX(rtp.displayOrder), 0) FROM RoomTypePhoto rtp WHERE rtp.roomType.roomTypeId = :roomTypeId")
    public Integer findMaxDisplayOrderByRoomTypeId(@Param("roomTypeId") Integer roomTypeId);
}
