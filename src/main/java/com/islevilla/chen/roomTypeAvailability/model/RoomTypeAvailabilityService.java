package com.islevilla.chen.roomTypeAvailability.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit; // 引入 Redis TTL (Time-To-Live) 的時間單位

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate; // 引入 Redis 核心工具
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.islevilla.chen.room.model.RoomTypeCount;
import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeRepository;
import com.islevilla.chen.roomType.model.RoomTypeService;
import com.islevilla.chen.util.exception.BusinessException;
import com.islevilla.wei.room.model.RoomRVOrderRepository;

@Service
public class RoomTypeAvailabilityService {

	// --- Redis 快取相關設定 ---

	/**
	 * Redis 快取 Key 的前綴。
	 * 用於區分不同業務的快取，方便管理。
	 * 格式: availability:roomtype:{roomTypeId}:{date}
	 * 例如: availability:roomtype:1:2024-07-07
	 */
	private static final String AVAILABILITY_CACHE_PREFIX = "availability:roomtype:";

	@Autowired
	private RedisTemplate<String, Object> redisTemplate; // 注入 Spring 設定好的 RedisTemplate

	// --- 原有的依賴注入 ---

	@Autowired
	private RoomTypeAvailabilityRepository roomTypeAvailabilityRepository;

	@Autowired
	private RoomTypeService roomTypeService;

	@Autowired
	private RoomTypeRepository roomTypeRepository;

	@Autowired
	private RoomRVOrderRepository roomRVOrderRepository;


	// --- 快取核心方法 ---

	/**
	 * 【優化後】計算特定房型在特定日期的實際可用數量。
	 *  此方法現在採用「旁路快取」(Cache-Aside) 模式。
	 *
	 * @param roomTypeId 房型ID
	 * @param targetDate 目標日期
	 * @return 實際可用數量
	 */
	@Transactional(readOnly = true)
	public Integer calculateAvailableRooms(Integer roomTypeId, LocalDate targetDate) {
		// 1. 產生 Redis 快取的 Key
		String cacheKey = AVAILABILITY_CACHE_PREFIX + roomTypeId + ":" + targetDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

		// 2. 嘗試從 Redis 讀取資料
		Object cachedValue = redisTemplate.opsForValue().get(cacheKey);

		if (cachedValue != null) {
			// 2a. 快取命中 (Cache Hit): 如果 Redis 有資料，直接回傳
			System.out.println(">>> Redis 快取命中! Key: " + cacheKey);
			return (Integer) cachedValue;
		}

		// 2b. 快取未命中 (Cache Miss): 如果 Redis 沒資料，則執行以下資料庫查詢邏輯
		System.out.println("--- Redis 快取未命中，查詢資料庫... Key: " + cacheKey);

		// 3. 執行原有的資料庫查詢邏輯
		// 3.1. 獲取房型總數量
		Optional<RoomType> roomTypeOpt = roomTypeRepository.findById(roomTypeId);
		if (!roomTypeOpt.isPresent()) {
			throw new BusinessException("房型不存在，ID: " + roomTypeId);
		}
		Integer totalRooms = roomTypeOpt.get().getRoomTypeQuantity();

		// 3.2. 計算該日期被預訂的數量
		Long reservedCount = roomRVOrderRepository.countReservedRoomsOnDate(roomTypeId, targetDate);

		// 3.3. 計算可用數量
		Integer availableRooms = totalRooms - reservedCount.intValue();
		Integer finalAvailableRooms = Math.max(0, availableRooms); // 確保不會是負數

		// 4. 將計算結果存入 Redis，並設定過期時間 (TTL)
		//    例如，設定為 1 小時後自動過期，避免因意外情況導致資料永遠不一致
		redisTemplate.opsForValue().set(cacheKey, finalAvailableRooms, 1, TimeUnit.HOURS);

		System.out.println("--- 資料庫查詢完畢，結果寫入 Redis。Value: " + finalAvailableRooms);

		return finalAvailableRooms;
	}

	/**
	 * 【新增】清除庫存快取的方法。
	 *  當訂單成立、取消，或房間狀態變更時，需要呼叫此方法來刪除舊的快取，
	 *  以確保下次查詢能獲取最新的資料。
	 *
	 * @param roomTypeId 房型ID
	 * @param date       日期
	 */
	public void invalidateAvailabilityCache(Integer roomTypeId, LocalDate date) {
		String cacheKey = AVAILABILITY_CACHE_PREFIX + roomTypeId + ":" + date.format(DateTimeFormatter.ISO_LOCAL_DATE);
		redisTemplate.delete(cacheKey);
		System.out.println("!!! 清除 Redis 快取 !!! Key: " + cacheKey);
	}


	// --- 以下為您原有的方法，現在它們將會間接或直接地從快取優化中受益 ---
	// --- 大部分方法無需修改，因為它們最終都呼叫了已被優化的 calculateAvailableRooms ---

	/**
	 * 檢查特定房型在特定日期是否有足夠庫存。
	 * 此方法現在會自動使用 `calculateAvailableRooms` 中的快取邏輯。
	 */
	@Transactional(readOnly = true)
	public boolean hasAvailableRooms(Integer roomTypeId, LocalDate targetDate, Integer requiredCount) {
		Integer availableCount = calculateAvailableRooms(roomTypeId, targetDate);
		return availableCount >= requiredCount;
	}

	/**
	 * 檢查特定房型在日期範圍內是否有足夠庫存。
	 * 此方法現在會自動使用 `calculateAvailableRooms` 中的快取邏輯。
	 */
	@Transactional(readOnly = true)
	public boolean hasAvailableRoomsInRange(Integer roomTypeId, LocalDate startDate, LocalDate endDate, Integer requiredCount) {
		LocalDate currentDate = startDate;
		while (currentDate.isBefore(endDate)) {
			if (!hasAvailableRooms(roomTypeId, currentDate, requiredCount)) {
				// 只要有一天庫存不足，就立刻回傳 false
				System.out.println("庫存不足! 房型ID: " + roomTypeId + ", 日期: " + currentDate + ", 需求: " + requiredCount);
				return false;
			}
			currentDate = currentDate.plusDays(1);
		}
		return true;
	}

	/**
	 * 訂房送出後調整空房庫存。
	 * 【重要】此方法現在除了更新資料庫，還需要清除快取。
	 */
	@Transactional
	public void decreaseAvailability(Integer roomTypeId, LocalDate date, int count) {
		// 原有的資料庫操作
		Optional<RoomTypeAvailability> opt = roomTypeAvailabilityRepository
			.findByRoomTypeAvailabilityId_RoomTypeIdAndRoomTypeAvailabilityId_RoomTypeAvailabilityDate(roomTypeId, date);
		if (opt.isPresent()) {
			RoomTypeAvailability availability = opt.get();
			int newCount = availability.getRoomTypeAvailabilityCount() - count;
			if (newCount < 0) newCount = 0;
			availability.setRoomTypeAvailabilityCount(newCount);
			roomTypeAvailabilityRepository.save(availability);

			// 【新增】清除對應日期的快取
			invalidateAvailabilityCache(roomTypeId, date);

		} else {
			// 注意：如果您的業務流程是依賴這個預先計算的表格，這裡可能會出錯。
			// 更好的做法可能是直接依賴 calculateAvailableRooms 的計算結果。
			// 但為保持最小改動，暫時保留原邏輯並加上快取清除。
			System.err.println("警告：在 decreaseAvailability 中查無預存的庫存資料，但仍會嘗試清除快取。房型ID=" + roomTypeId + ", 日期=" + date);
			invalidateAvailabilityCache(roomTypeId, date);
			// throw new RuntimeException("查無庫存資料: 房型ID=" + roomTypeId + ", 日期=" + date);
		}
	}

	/**
	 * 自動同步房型可用性記錄 (根據實際庫存計算)。
	 * 此方法現在會自動使用 `calculateAvailableRooms` 中的快取邏輯。
	 */
	@Transactional
	public RoomTypeAvailability syncRoomTypeAvailability(Integer roomTypeId, LocalDate targetDate) {
		Integer availableCount = calculateAvailableRooms(roomTypeId, targetDate);
		RoomTypeAvailabilityId id = new RoomTypeAvailabilityId(roomTypeId, targetDate);
		Optional<RoomTypeAvailability> existingOpt = roomTypeAvailabilityRepository.findById(id);
		RoomTypeAvailability availability;
		if (existingOpt.isPresent()) {
			availability = existingOpt.get();
			availability.setRoomTypeAvailabilityCount(availableCount);
		} else {
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

	// ... (保留您其他的原有方法，例如 findAll, findByComplexQuery 等，它們不受直接影響)
    // ... 以下是為了保持完整性而複製過來的其他方法 ...

    @Transactional(readOnly = true)
	public List<RoomTypeAvailability> findAllByYearMonth(int year,int month) {
		List<RoomTypeAvailability> result = roomTypeAvailabilityRepository.findAllByYearMonth(year,month);
		if (result.isEmpty()) {
			throw new BusinessException("查無當月記錄");
		}
		return result;
	}

	@Transactional(readOnly = true)
	public List<RoomTypeAvailability> findByRoomTypeIdAndYearMonth(Integer roomTypeId, int year, int month){
		return roomTypeAvailabilityRepository.findByRoomTypeIdAndYearMonth(roomTypeId, year, month);
	}

	@Transactional
	public void recalculateAvailability(RoomTypeCount roomTypeCount) {
		LocalDate startDate = LocalDate.now();
		LocalDate endDate = startDate.plusMonths(3);
		LocalDate date = startDate;
		while (!date.isAfter(endDate)) {
			RoomTypeAvailability daliyRoomTypeAvailability = new RoomTypeAvailability();
			RoomTypeAvailabilityId roomTypeAvailabilityId = new RoomTypeAvailabilityId();
			RoomType roomType = roomTypeService.findById(roomTypeCount.getRoomTypeIdDTO());
			if (roomType != null) {
				roomTypeAvailabilityId.setRoomTypeId(roomTypeCount.getRoomTypeIdDTO());
				roomTypeAvailabilityId.setRoomTypeAvailabilityDate(date);
				daliyRoomTypeAvailability.setRoomTypeAvailabilityId(roomTypeAvailabilityId);
				daliyRoomTypeAvailability.setRoomType(roomType);
                daliyRoomTypeAvailability.setRoomTypeAvailabilityCount(roomTypeCount.getRoomAvailableDTO());
				roomTypeAvailabilityRepository.save(daliyRoomTypeAvailability);

                // 【新增】重新計算後，也應該清除快取
                invalidateAvailabilityCache(roomTypeCount.getRoomTypeIdDTO(), date);
			}
			date = date.plusDays(1);
		}
	}

	@Transactional(readOnly = true)
	public Map<Integer, Integer> calculateAllRoomTypesAvailability(LocalDate targetDate) {
		Map<Integer, Integer> availabilityMap = new HashMap<>();
		List<RoomType> allRoomTypes = roomTypeRepository.findAll();
		for (RoomType roomType : allRoomTypes) {
            // 這裡會自動呼叫到我們優化過的 calculateAvailableRooms 方法
			availabilityMap.put(roomType.getRoomTypeId(), calculateAvailableRooms(roomType.getRoomTypeId(), targetDate));
		}
		return availabilityMap;
	}

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

	@Transactional(readOnly = true)
	public Page<RoomTypeAvailability> findAll(Pageable pageable) {
		return roomTypeAvailabilityRepository.findAll(pageable);
	}

	@Transactional(readOnly = true)
	public Page<RoomTypeAvailability> findByComplexQuery(
			Integer roomTypeId,
			LocalDate specificDate,
			LocalDate startDate,
			LocalDate endDate,
			Pageable pageable) {
		if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
			throw new BusinessException("開始日期不能晚於結束日期");
		}
		if (specificDate != null) {
			startDate = null;
			endDate = null;
		}
		return roomTypeAvailabilityRepository.findByComplexQuery(
			roomTypeId, specificDate, startDate, endDate, pageable);
	}

	@Transactional(readOnly = true)
	public List<RoomTypeAvailability> findByComplexQueryAll(
			Integer roomTypeId,
			LocalDate specificDate,
			LocalDate startDate,
			LocalDate endDate) {
		if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
			throw new BusinessException("開始日期不能晚於結束日期");
		}
		if (specificDate != null) {
			startDate = null;
			endDate = null;
		}
		return roomTypeAvailabilityRepository.findByComplexQueryAll(
			roomTypeId, specificDate, startDate, endDate);
	}

	@Transactional(readOnly = true)
	public Page<RoomTypeAvailability> findByRoomTypeIdPaged(Integer roomTypeId, Pageable pageable) {
		return roomTypeAvailabilityRepository.findByRoomTypeAvailabilityId_RoomTypeId(roomTypeId, pageable);
	}

	@Transactional(readOnly = true)
	public Page<RoomTypeAvailability> findByAvailabilityDatePaged(LocalDate availabilityDate, Pageable pageable) {
		return roomTypeAvailabilityRepository.findByRoomTypeAvailabilityId_RoomTypeAvailabilityDate(availabilityDate, pageable);
	}

	@Transactional(readOnly = true)
	public Page<RoomTypeAvailability> findByDateRangePaged(LocalDate startDate, LocalDate endDate, Pageable pageable) {
		if (startDate.isAfter(endDate)) {
			throw new BusinessException("開始日期不能晚於結束日期");
		}
		return roomTypeAvailabilityRepository.findByDateRange(startDate, endDate, pageable);
	}

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

	 @Transactional(readOnly = true)
	 public List<RoomTypeAvailability> findByDateRange(LocalDate startDate, LocalDate endDate) {
	  if (startDate.isAfter(endDate)) {
	   throw new BusinessException("開始日期不能晚於結束日期");
	  }
	  return roomTypeAvailabilityRepository.findByDateRange(startDate, endDate);
	 }

	 @Transactional(readOnly = true)
	 public List<RoomTypeAvailability> findByRoomTypeId(Integer roomTypeId) {
	  List<RoomTypeAvailability> result = roomTypeAvailabilityRepository.findByRoomTypeAvailabilityId_RoomTypeId(roomTypeId);
	  if (result.isEmpty()) {
	   throw new BusinessException("查無該房型的可用性記錄，房型ID: " + roomTypeId);
	  }
	  return result;
	 }

	 @Transactional(readOnly = true)
	 public List<RoomTypeAvailability> findAll() {
	  return roomTypeAvailabilityRepository.findAll();
	 }
}