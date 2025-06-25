package com.islevilla.chen.roomTypePhoto.model;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.islevilla.chen.room.model.Room;
import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeRepository;

@Service
public class RoomTypePhotoService {
	@Autowired
	private RoomTypePhotoRepository roomTypePhotoRepository;
	@Autowired
	private RoomTypeRepository roomTypeRepository;
	
	@Transactional
	public RoomTypePhoto addRoomTypePhoto(Integer roomTypeId, Byte[] roomTypePhoto) {
		RoomTypePhoto roomTypephoto = new RoomTypePhoto();

	    // 主鍵不用設，JPA 會自動產生
	    // 設定關聯（需查詢 RoomType 物件再設進去）
	    RoomType roomType = roomTypeRepository.findById(roomTypeId).orElseThrow(() -> new RuntimeException("RoomType not found"));

	    roomTypephoto.setRoomType(roomType);
	    roomTypephoto.setRoomTypePhoto(roomTypePhoto);

        return roomTypePhotoRepository.save(roomTypephoto);
	}
	
	@Transactional
	public RoomTypePhoto updateRoomTypePhoto(RoomTypePhoto roomTypePhoto) {
		return roomTypePhotoRepository.save(roomTypePhoto);
	}
	
	@Transactional
	public void deleteRoomTypePhoto(Integer roomTypePhotoId) {
		roomTypePhotoRepository.deleteById(roomTypePhotoId);
	}
	
	@Transactional(readOnly = true)
	public RoomTypePhoto findById(Integer roomTypePhotoId) {
		Optional<RoomTypePhoto> optional=roomTypePhotoRepository.findById(roomTypePhotoId);
		return optional.orElse(null);
	}
	
	@Transactional(readOnly = true)
	public List<RoomTypePhoto> findAll() {
		return roomTypePhotoRepository.findAll();
	}
	
	@Transactional(readOnly = true)
	public List<RoomType> roomTypeFindPhotos(Integer roomTypeId){
		return roomTypePhotoRepository.findWithPhotos(roomTypeId);
	}
	
	@Transactional(readOnly = true)
	public List<RoomTypePhoto> compoundQuery(Integer roomTypePhotoId, Integer roomTypeId){
	    return roomTypePhotoRepository.search(roomTypePhotoId,roomTypeId);
	}
}
