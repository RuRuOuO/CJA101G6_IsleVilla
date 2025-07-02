package com.islevilla.chen.roomTypeAvailability.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeRepository;
import com.islevilla.chen.util.exception.BusinessException;

@Service
public class RoomTypeAvailabilityService {
	
	@Autowired
	private RoomTypeAvailabilityRepository roomTypeAvailabilityRepository;
	
	@Autowired
	private RoomTypeRepository roomTypeRepository;
	
	/**
	 * 新增房型可用性記錄
	 */
	@Transactional
	public RoomTypeAvailability addRoomTypeAvailability(RoomTypeAvailability roomTypeAvailability) {
		// 檢查房型是否存在
		Integer roomTypeId = roomTypeAvailability.getRoomTypeAvailabilityId().getRoomTypeId();
		Optional<RoomType> roomType = roomTypeRepository.findById(roomTypeId);
		if (!roomType.isPresent()) {
			throw new BusinessException("房型不存在，ID: " + roomTypeId);
		}
		
		// 檢查是否已存在相同的記錄
		LocalDate availabilityDate = roomTypeAvailability.getRoomTypeAvailabilityId().getRoomTypeAvailabilityDate();
		boolean exists = roomTypeAvailabilityRepository.existsByRoomTypeAvailabilityId_RoomTypeIdAndRoomTypeAvailabilityId_RoomTypeAvailabilityDate(
			roomTypeId, availabilityDate);
		if (exists) {
			throw new BusinessException("該房型在指定日期已有可用性記錄: " + roomTypeId + ", " + availabilityDate);
		}
		
		// 設置房型關聯
		roomTypeAvailability.setRoomType(roomType.get());
		
		return roomTypeAvailabilityRepository.save(roomTypeAvailability);
	}
	
	/**
	 * 更新房型可用性記錄
	 */
	@Transactional
	public RoomTypeAvailability updateRoomTypeAvailability(RoomTypeAvailability roomTypeAvailability) {
		// 檢查記錄是否存在
		RoomTypeAvailabilityId id = roomTypeAvailability.getRoomTypeAvailabilityId();
		Optional<RoomTypeAvailability> existing = roomTypeAvailabilityRepository.findById(id);
		if (!existing.isPresent()) {
			throw new BusinessException("房型可用性記錄不存在: " + id.getRoomTypeId() + ", " + id.getRoomTypeAvailabilityDate());
		}
		
		// 確保房型關聯正確設置
		if (roomTypeAvailability.getRoomType() == null) {
			roomTypeAvailability.setRoomType(existing.get().getRoomType());
		}
		
		return roomTypeAvailabilityRepository.save(roomTypeAvailability);
	}
	
	/**
	 * 根據複合主鍵刪除房型可用性記錄
	 */
	@Transactional
	public void deleteRoomTypeAvailability(Integer roomTypeId, LocalDate roomTypeAvailabilityDate) {
		RoomTypeAvailabilityId id = new RoomTypeAvailabilityId(roomTypeId, roomTypeAvailabilityDate);
		
		if (!roomTypeAvailabilityRepository.existsById(id)) {
			throw new BusinessException("房型可用性記錄不存在: " + roomTypeId + ", " + roomTypeAvailabilityDate);
		}
		
		roomTypeAvailabilityRepository.deleteById(id);
	}
	
	/**
	 * 根據複合主鍵查詢房型可用性記錄
	 */
	@Transactional(readOnly = true)
	public RoomTypeAvailability findById(Integer roomTypeId, LocalDate roomTypeAvailabilityDate) {
		RoomTypeAvailabilityId id = new RoomTypeAvailabilityId(roomTypeId, roomTypeAvailabilityDate);
		Optional<RoomTypeAvailability> optional = roomTypeAvailabilityRepository.findById(id);
		return optional.orElse(null);
	}
	
	/**
	 * 查詢所有房型可用性記錄
	 */
	@Transactional(readOnly = true)
	public List<RoomTypeAvailability> findAll() {
		return roomTypeAvailabilityRepository.findAll();
	}
	
	/**
	 * 根據房型ID查詢所有可用性記錄
	 */
	@Transactional(readOnly = true)
	public List<RoomTypeAvailability> findByRoomTypeId(Integer roomTypeId) {
		List<RoomTypeAvailability> result = roomTypeAvailabilityRepository.findByRoomTypeAvailabilityId_RoomTypeId(roomTypeId);
		if (result.isEmpty()) {
			throw new BusinessException("查無該房型的可用性記錄，房型ID: " + roomTypeId);
		}
		return result;
	}
	
	/**
	 * 根據日期查詢所有房型可用性
	 */
	@Transactional(readOnly = true)
	public List<RoomTypeAvailability> findByAvailabilityDate(LocalDate availabilityDate) {
		List<RoomTypeAvailability> result = roomTypeAvailabilityRepository.findByRoomTypeAvailabilityId_RoomTypeAvailabilityDate(availabilityDate);
		if (result.isEmpty()) {
			throw new BusinessException("查無該日期的房型可用性記錄: " + availabilityDate);
		}
		return result;
	}
	
	/**
	 * 根據房型ID和日期範圍查詢
	 */
	@Transactional(readOnly = true)
	public List<RoomTypeAvailability> findByRoomTypeIdAndDateRange(Integer roomTypeId, LocalDate startDate, LocalDate endDate) {
		if (startDate.isAfter(endDate)) {
			throw new BusinessException("開始日期不能晚於結束日期");
		}
		
		List<RoomTypeAvailability> result = roomTypeAvailabilityRepository.findByRoomTypeIdAndDateRange(roomTypeId, startDate, endDate);
		if (result.isEmpty()) {
			throw new BusinessException("查無符合條件的房型可用性記錄");
		}
		return result;
	}
	
	/**
	 * 查詢特定日期範圍內的所有可用性記錄
	 */
	@Transactional(readOnly = true)
	public List<RoomTypeAvailability> findByDateRange(LocalDate startDate, LocalDate endDate) {
		if (startDate.isAfter(endDate)) {
			throw new BusinessException("開始日期不能晚於結束日期");
		}
		
		return roomTypeAvailabilityRepository.findByDateRange(startDate, endDate);
	}
	
	/**
	 * 查詢可用數量大於指定值的記錄
	 */
	@Transactional(readOnly = true)
	public List<RoomTypeAvailability> findByMinAvailability(Integer minCount) {
		if (minCount < 0) {
			throw new BusinessException("最小可用數量不能小於0");
		}
		
		return roomTypeAvailabilityRepository.findByAvailabilityCountGreaterThan(minCount);
	}
	
	/**
	 * 檢查特定房型在特定日期是否有可用性記錄
	 */
	@Transactional(readOnly = true)
	public boolean existsAvailability(Integer roomTypeId, LocalDate availabilityDate) {
		return roomTypeAvailabilityRepository.existsByRoomTypeAvailabilityId_RoomTypeIdAndRoomTypeAvailabilityId_RoomTypeAvailabilityDate(
			roomTypeId, availabilityDate);
	}
	
	/**
	 * 批量創建房型可用性記錄（用於初始化某個房型的可用性）
	 */
	@Transactional
	public List<RoomTypeAvailability> batchCreateAvailability(Integer roomTypeId, LocalDate startDate, LocalDate endDate, Integer defaultCount) {
		// 檢查房型是否存在
		Optional<RoomType> roomType = roomTypeRepository.findById(roomTypeId);
		if (!roomType.isPresent()) {
			throw new BusinessException("房型不存在，ID: " + roomTypeId);
		}
		
		if (startDate.isAfter(endDate)) {
			throw new BusinessException("開始日期不能晚於結束日期");
		}
		
		List<RoomTypeAvailability> result = new java.util.ArrayList<>();
		LocalDate currentDate = startDate;
		
		while (!currentDate.isAfter(endDate)) {
			// 檢查是否已存在記錄
			if (!existsAvailability(roomTypeId, currentDate)) {
				RoomTypeAvailabilityId id = new RoomTypeAvailabilityId(roomTypeId, currentDate);
				RoomTypeAvailability availability = new RoomTypeAvailability();
				availability.setRoomTypeAvailabilityId(id);
				availability.setRoomType(roomType.get());
				availability.setRoomTypeAvailabilityCount(defaultCount);
				
				result.add(roomTypeAvailabilityRepository.save(availability));
			}
			currentDate = currentDate.plusDays(1);
		}
		
		return result;
	}
}
