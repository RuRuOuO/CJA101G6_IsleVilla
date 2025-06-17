package com.islevilla.chen.roomTypePhoto.model;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomTypePhotoService {
	@Autowired
	private RoomTypePhotoRepository roomTypePhotoRepository;
	
	public RoomTypePhotoVO addRoom(RoomTypePhotoVO roomTypePhotoVO) {
		return roomTypePhotoRepository.save(roomTypePhotoVO);
	}
	
	public RoomTypePhotoVO updateRoom(RoomTypePhotoVO roomTypePhotoVO) {
		return roomTypePhotoRepository.save(roomTypePhotoVO);
	}
	
	public void deleteRoom(int roomTypePhotoId) {
		roomTypePhotoRepository.deleteById(roomTypePhotoId);
	}
	
	public RoomTypePhotoVO findById(int roomTypePhotoId) {
		Optional<RoomTypePhotoVO> optional=roomTypePhotoRepository.findById(roomTypePhotoId);
		return optional.orElse(null);
	}
	
	public List<RoomTypePhotoVO> findAll() {
		return roomTypePhotoRepository.findAll();
	}
}
