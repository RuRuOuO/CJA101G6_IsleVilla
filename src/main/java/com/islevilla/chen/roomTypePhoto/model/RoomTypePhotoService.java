package com.islevilla.chen.roomTypePhoto.model;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomTypePhotoService {
	@Autowired
	private RoomTypePhotoRepository roomTypePhotoRepository;
	@Autowired
	RoomTypePhoto roomTypephoto = new RoomTypePhoto();
	
	public RoomTypePhoto addRoomTypePhoto(Integer roomTypeId, Byte[] roomTypePhoto) {
        return roomTypePhotoRepository.save(roomTypephoto);
	}
	
	public RoomTypePhoto updateRoomTypePhoto(RoomTypePhoto roomTypePhoto) {
		return roomTypePhotoRepository.save(roomTypePhoto);
	}
	
	public void deleteRoomTypePhoto(Integer roomTypePhotoId) {
		roomTypePhotoRepository.deleteById(roomTypePhotoId);
	}
	
	public RoomTypePhoto findById(Integer roomTypePhotoId) {
		Optional<RoomTypePhoto> optional=roomTypePhotoRepository.findById(roomTypePhotoId);
		return optional.orElse(null);
	}
	
	public List<RoomTypePhoto> findAll() {
		return roomTypePhotoRepository.findAll();
	}
}
