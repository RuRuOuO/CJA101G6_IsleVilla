package com.islevilla.chen.roomTypePhoto.model;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomTypePhotoService {
	@Autowired
	private RoomTypePhotoRepository roomTypePhotoRepository;
	
	public RoomTypePhoto addRoomTypePhoto(RoomTypePhoto roomTypePhoto) {
		return roomTypePhotoRepository.save(roomTypePhoto);
	}
	
	public RoomTypePhoto updateRoomTypePhoto(RoomTypePhoto roomTypePhoto) {
		return roomTypePhotoRepository.save(roomTypePhoto);
	}
	
	public void deleteRoomTypePhoto(int roomTypePhotoId) {
		roomTypePhotoRepository.deleteById(roomTypePhotoId);
	}
	
	public RoomTypePhoto findById(int roomTypePhotoId) {
		Optional<RoomTypePhoto> optional=roomTypePhotoRepository.findById(roomTypePhotoId);
		return optional.orElse(null);
	}
	
	public List<RoomTypePhoto> findAll() {
		return roomTypePhotoRepository.findAll();
	}
}
