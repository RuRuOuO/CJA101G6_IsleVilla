package com.islevilla.chen.roomTypeAvailability.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeRepository;
import com.islevilla.chen.util.exception.BusinessException;
import com.islevilla.wei.room.model.RoomRVOrderRepository;

@Service
public class RoomTypeAvailabilityService {
	
	@Autowired
	private RoomTypeAvailabilityRepository roomTypeAvailabilityRepository;
	
	@Autowired
	private RoomTypeRepository roomTypeRepository;
	
	@Autowired
	private RoomRVOrderRepository roomRVOrderRepository;
	
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
	
	// ==================== 庫存計算相關方法 ====================
	
	/**
	 * 計算特定房型在特定日期的實際可用數量
	 * @param roomTypeId 房型ID
	 * @param targetDate 目標日期
	 * @return 實際可用數量
	 */
	@Transactional(readOnly = true)
	public Integer calculateAvailableRooms(Integer roomTypeId, LocalDate targetDate) {
		// 1. 獲取房型總數量
		Optional<RoomType> roomTypeOpt = roomTypeRepository.findById(roomTypeId);
		if (!roomTypeOpt.isPresent()) {
			throw new BusinessException("房型不存在，ID: " + roomTypeId);
		}
		
		Integer totalRooms = roomTypeOpt.get().getRoomTypeQuantity();
		
		// 2. 計算該日期被預訂的數量
		Long reservedCount = roomRVOrderRepository.countReservedRoomsOnDate(roomTypeId, targetDate);
		
		// 3. 計算可用數量
		Integer availableRooms = totalRooms - reservedCount.intValue();
		
		// 除錯資訊
		System.out.println("=== 庫存計算除錯 ===");
		System.out.println("房型ID: " + roomTypeId + ", 日期: " + targetDate);
		System.out.println("房型總數量: " + totalRooms);
		System.out.println("已預訂數量: " + reservedCount);
		System.out.println("計算結果: " + availableRooms);
		System.out.println("==================");
		
		return Math.max(0, availableRooms); // 確保不會是負數
	}
	
	/**
	 * 批量計算所有房型在特定日期的可用數量
	 * @param targetDate 目標日期
	 * @return Map<房型ID, 可用數量>
	 */
	@Transactional(readOnly = true)
	public Map<Integer, Integer> calculateAllRoomTypesAvailability(LocalDate targetDate) {
		Map<Integer, Integer> availabilityMap = new HashMap<>();
		
		// 1. 獲取所有房型
		List<RoomType> allRoomTypes = roomTypeRepository.findAll();
		
		// 2. 獲取該日期所有房型的預訂數量
		List<Object[]> reservedCounts = roomRVOrderRepository.countReservedRoomsByTypeOnDate(targetDate);
		Map<Integer, Long> reservedMap = new HashMap<>();
		for (Object[] result : reservedCounts) {
			Integer roomTypeId = (Integer) result[0];
			Long count = (Long) result[1];
			reservedMap.put(roomTypeId, count);
		}
		
		// 3. 計算每個房型的可用數量
		for (RoomType roomType : allRoomTypes) {
			Integer roomTypeId = roomType.getRoomTypeId();
			Integer totalRooms = roomType.getRoomTypeQuantity();
			Long reservedCount = reservedMap.getOrDefault(roomTypeId, 0L);
			Integer availableRooms = totalRooms - reservedCount.intValue();
			
			availabilityMap.put(roomTypeId, Math.max(0, availableRooms));
		}
		
		return availabilityMap;
	}
	
	/**
	 * 自動同步房型可用性記錄 (根據實際庫存計算)
	 * @param roomTypeId 房型ID
	 * @param targetDate 目標日期
	 */
	@Transactional
	public RoomTypeAvailability syncRoomTypeAvailability(Integer roomTypeId, LocalDate targetDate) {
		// 計算實際可用數量
		Integer availableCount = calculateAvailableRooms(roomTypeId, targetDate);
		
		// 檢查是否已存在記錄
		RoomTypeAvailabilityId id = new RoomTypeAvailabilityId(roomTypeId, targetDate);
		Optional<RoomTypeAvailability> existingOpt = roomTypeAvailabilityRepository.findById(id);
		
		RoomTypeAvailability availability;
		if (existingOpt.isPresent()) {
			// 更新現有記錄
			availability = existingOpt.get();
			availability.setRoomTypeAvailabilityCount(availableCount);
		} else {
			// 創建新記錄
			Optional<RoomType> roomType = roomTypeRepository.findById(roomTypeId);
			if (!roomType.isPresent()) {
				throw new BusinessException("房型不存在，ID: " + roomTypeId);
			}
			
			availability = new RoomTypeAvailability();
			availability.setRoomTypeAvailabilityId(id);
			availability.setRoomType(roomType.get());
			availability.setRoomTypeAvailabilityCount(availableCount);
		}
		
		return roomTypeAvailabilityRepository.save(availability);
	}
	
	/**
	 * 批量同步指定日期範圍內的房型可用性
	 * @param roomTypeId 房型ID
	 * @param startDate 開始日期
	 * @param endDate 結束日期
	 */
	@Transactional
	public List<RoomTypeAvailability> batchSyncAvailability(Integer roomTypeId, LocalDate startDate, LocalDate endDate) {
		if (startDate.isAfter(endDate)) {
			throw new BusinessException("開始日期不能晚於結束日期");
		}
		
		List<RoomTypeAvailability> result = new java.util.ArrayList<>();
		LocalDate currentDate = startDate;
		
		while (!currentDate.isAfter(endDate)) {
			RoomTypeAvailability synced = syncRoomTypeAvailability(roomTypeId, currentDate);
			result.add(synced);
			currentDate = currentDate.plusDays(1);
		}
		
		return result;
	}
	
	/**
	 * 檢查特定房型在特定日期是否有足夠庫存
	 * @param roomTypeId 房型ID
	 * @param targetDate 目標日期
	 * @param requiredCount 需求數量
	 * @return 是否有足夠庫存
	 */
	@Transactional(readOnly = true)
	public boolean hasAvailableRooms(Integer roomTypeId, LocalDate targetDate, Integer requiredCount) {
		Integer availableCount = calculateAvailableRooms(roomTypeId, targetDate);
		return availableCount >= requiredCount;
	}
	
	/**
	 * 檢查特定房型在日期範圍內是否有足夠庫存
	 * @param roomTypeId 房型ID
	 * @param startDate 開始日期 (包含)
	 * @param endDate 結束日期 (不包含，因為退房當天不佔用)
	 * @param requiredCount 需求數量
	 * @return 是否每天都有足夠庫存
	 */
	@Transactional(readOnly = true)
	public boolean hasAvailableRoomsInRange(Integer roomTypeId, LocalDate startDate, LocalDate endDate, Integer requiredCount) {
		LocalDate currentDate = startDate;
		
		while (currentDate.isBefore(endDate)) {
			if (!hasAvailableRooms(roomTypeId, currentDate, requiredCount)) {
				return false;
			}
			currentDate = currentDate.plusDays(1);
		}
		
		return true;
	}
	
	// ==================== 分頁查詢方法 ====================
	
	/**
	 * 分頁查詢所有房型可用性記錄
	 */
	@Transactional(readOnly = true)
	public Page<RoomTypeAvailability> findAll(Pageable pageable) {
		return roomTypeAvailabilityRepository.findAll(pageable);
	}
	
	/**
	 * 複合查詢：支援房型ID、特定日期、日期範圍的組合查詢 (分頁)
	 */
	@Transactional(readOnly = true)
	public Page<RoomTypeAvailability> findByComplexQuery(
			Integer roomTypeId, 
			LocalDate specificDate, 
			LocalDate startDate, 
			LocalDate endDate, 
			Pageable pageable) {
		
		// 驗證日期範圍
		if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
			throw new BusinessException("開始日期不能晚於結束日期");
		}
		
		// 如果有特定日期，則忽略日期範圍
		if (specificDate != null) {
			startDate = null;
			endDate = null;
		}
		
		return roomTypeAvailabilityRepository.findByComplexQuery(
			roomTypeId, specificDate, startDate, endDate, pageable);
	}
	
	/**
	 * 複合查詢：支援房型ID、特定日期、日期範圍的組合查詢 (不分頁，給DataTable使用)
	 */
	@Transactional(readOnly = true)
	public List<RoomTypeAvailability> findByComplexQueryAll(
			Integer roomTypeId, 
			LocalDate specificDate, 
			LocalDate startDate, 
			LocalDate endDate) {
		
		// 驗證日期範圍
		if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
			throw new BusinessException("開始日期不能晚於結束日期");
		}
		
		// 如果有特定日期，則忽略日期範圍
		if (specificDate != null) {
			startDate = null;
			endDate = null;
		}
		
		return roomTypeAvailabilityRepository.findByComplexQueryAll(
			roomTypeId, specificDate, startDate, endDate);
	}
	
	/**
	 * 根據房型ID分頁查詢
	 */
	@Transactional(readOnly = true)
	public Page<RoomTypeAvailability> findByRoomTypeIdPaged(Integer roomTypeId, Pageable pageable) {
		return roomTypeAvailabilityRepository.findByRoomTypeAvailabilityId_RoomTypeId(roomTypeId, pageable);
	}
	
	/**
	 * 根據日期分頁查詢
	 */
	@Transactional(readOnly = true)
	public Page<RoomTypeAvailability> findByAvailabilityDatePaged(LocalDate availabilityDate, Pageable pageable) {
		return roomTypeAvailabilityRepository.findByRoomTypeAvailabilityId_RoomTypeAvailabilityDate(availabilityDate, pageable);
	}
	
	/**
	 * 根據日期範圍分頁查詢
	 */
	@Transactional(readOnly = true)
	public Page<RoomTypeAvailability> findByDateRangePaged(LocalDate startDate, LocalDate endDate, Pageable pageable) {
		if (startDate.isAfter(endDate)) {
			throw new BusinessException("開始日期不能晚於結束日期");
		}
		return roomTypeAvailabilityRepository.findByDateRange(startDate, endDate, pageable);
	}
	
	/**
	 * 訂房送出後調整空房庫存
	 */
	@Transactional
	public void decreaseAvailability(Integer roomTypeId, LocalDate date, int count) {
		Optional<RoomTypeAvailability> opt = roomTypeAvailabilityRepository
			.findByRoomTypeAvailabilityId_RoomTypeIdAndRoomTypeAvailabilityId_RoomTypeAvailabilityDate(roomTypeId, date);
		if (opt.isPresent()) {
			RoomTypeAvailability availability = opt.get();
			int newCount = availability.getRoomTypeAvailabilityCount() - count;
			if (newCount < 0) newCount = 0;
			availability.setRoomTypeAvailabilityCount(newCount);
			roomTypeAvailabilityRepository.save(availability);
		} else {
			throw new RuntimeException("查無庫存資料: 房型ID=" + roomTypeId + ", 日期=" + date);
		}
	}
}
