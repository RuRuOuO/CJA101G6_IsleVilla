package com.islevilla.chen.room.model;

import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit; // 引入 Redis TTL 的時間單位

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate; // 引入 Redis 核心工具
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.islevilla.chen.util.exception.BusinessException;

@Service
public class RoomService {

	// --- Redis 快取相關設定 ---

	/**
	 * 房型統計 DTO 的 Redis 快取 Key 前綴。
	 * 格式: roomtype:stats:{roomTypeId}
	 * 例如: roomtype:stats:1
	 */
	private static final String STATS_CACHE_PREFIX = "roomtype:stats:";

	@Autowired
	@Qualifier("cacheRedisTemplate")
	private RedisTemplate<String, Object> redisTemplate; // 注入 Spring 設定好的 RedisTemplate

	// --- 原有的依賴注入 ---

	@Autowired
	private RoomRepository roomRepository;

	// --- 重構後的統計方法 ---

	/**
	 * 【新增】獲取房型統計資訊，此方法採用「旁路快取」模式，優先從 Redis 讀取。
	 * 這取代了舊的 updateRoomTypeBasicStatistics 的查詢功能。
	 *
	 * @param roomTypeId 房型ID
	 * @return 包含統計資訊的 RoomTypeCount DTO
	 */
	@Transactional(readOnly = true)
	public RoomTypeCount getRoomTypeStatistics(Integer roomTypeId) {
		String cacheKey = STATS_CACHE_PREFIX + roomTypeId;

		// 1. 嘗試從 Redis 快取讀取 DTO
		Object cachedValue = redisTemplate.opsForValue().get(cacheKey);
		if (cachedValue != null) {
			System.out.println(">>> DTO 快取命中! Key: " + cacheKey);
			// 注意：這裡需要確保 RoomTypeCount 類別有正確的無參構造函數，以便反序列化
			return (RoomTypeCount) cachedValue;
		}

		// 2. 快取未命中，呼叫私有方法從資料庫計算，並將結果寫入快取
		System.out.println("--- DTO 快取未命中，查詢資料庫並更新快取... Key: " + cacheKey);
		return calculateAndCacheRoomTypeStatistics(roomTypeId);
	}

	/**
	 * 【重構】原有的計算邏輯，現在被封裝為私有方法。
	 * 負責從資料庫計算統計數據，並將結果存入 Redis。
	 *
	 * @param roomTypeId 房型ID
	 * @return 計算並快取後的 RoomTypeCount DTO
	 */
	private RoomTypeCount calculateAndCacheRoomTypeStatistics(Integer roomTypeId) {
		// 計算該房型的總房間數
		List<RoomTypeCount> totalRooms = roomRepository.countRoomsByRoomTypeId();
		Integer totalRoom = totalRooms.stream()
									.filter(rc -> rc.getRoomTypeIdDTO().equals(roomTypeId))
									.map(RoomTypeCount::getRoomCountDTO)
									.findFirst()
									.orElse(0);

		// 計算無法使用的房間數 (狀態 2:待維修, 4:停用)
		List<RoomTypeCount> totalUnables = roomRepository.countRoomsByRoomTypeIdWithStatus();
		Integer totalUnable = totalUnables.stream()
										.filter(rc -> rc.getRoomTypeIdDTO().equals(roomTypeId))
										.map(RoomTypeCount::getRoomUnableDTO)
										.findFirst()
										.orElse(0);

		// 基本可用數量 = 總數 - 無法使用
		Integer available = totalRoom - totalUnable;

		// 建立統計結果 DTO
		RoomTypeCount statistics = new RoomTypeCount(roomTypeId, totalRoom, totalUnable, available);

		// 將計算結果存入 Redis，設定 24 小時過期
		String cacheKey = STATS_CACHE_PREFIX + roomTypeId;
		redisTemplate.opsForValue().set(cacheKey, statistics, 24, TimeUnit.HOURS);
		System.out.println("--- DTO 計算完畢，寫入 Redis。Key: " + cacheKey);

		return statistics;
	}

	/**
	 * 【新增】清除房型統計 DTO 快取的公用方法。
	 * 當房間狀態變更、新增或刪除時，應呼叫此方法。
	 *
	 * @param roomTypeId 房型ID
	 */
	public void invalidateRoomTypeStatsCache(Integer roomTypeId) {
		if (roomTypeId != null) {
			String cacheKey = STATS_CACHE_PREFIX + roomTypeId;
			redisTemplate.delete(cacheKey);
			System.out.println("!!! 清除 DTO 快取 !!! Key: " + cacheKey);
		}
	}

	// --- 修改後的 CRUD 方法 ---

	@Transactional
	public Room addRoom(Room room) {
		if(roomRepository.findById(room.getRoomId()).isPresent()) {
			throw new BusinessException("房間ID已存在！");
		}
		Room savedRoom = roomRepository.save(room);
		// 新增房間後，清除相關房型的統計快取
		invalidateRoomTypeStatsCache(savedRoom.getRoomTypeId());
		return savedRoom;
	}

	@Transactional
	public Room updateRoom(Room room) {
		// 更新房間後，清除相關房型的統計快取
		Room updatedRoom = roomRepository.save(room);
		invalidateRoomTypeStatsCache(updatedRoom.getRoomTypeId());
		return updatedRoom;
	}

	@Transactional
	public void deleteRoom(Integer roomId) {
		// 刪除前先找到房間，以獲取其房型ID
		findById(roomId).ifPresent(room -> {
			roomRepository.deleteById(roomId);
			// 刪除房間後，清除相關房型的統計快取
			invalidateRoomTypeStatsCache(room.getRoomTypeId());
		});
	}

	// --- 原有的查詢方法 (無需修改) ---

	@Transactional(readOnly = true)
	public Optional<Room> findById(Integer roomId) {
		return roomRepository.findById(roomId);
	}

	@Transactional(readOnly = true)
	public List<Room> findAll() {
		return roomRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Page<Room> findAll(Pageable pageable) {
		return roomRepository.findAll(pageable);
	}

	@Transactional(readOnly = true)
	public List<Room> compoundQuery(Integer roomId, Integer roomTypeId, Byte roomStatus){
		return roomRepository.searchRooms(roomId, roomTypeId, roomStatus);
	}

    //計算房型數量List(key=roomTypeId, value=roomCount)
	// ... 原有的 getRoomCountsByType 和 getRoomCountsByTypeWithStatus 方法可以保留，因為它們是計算的基礎 ...
    @Transactional(readOnly = true)
    public List<RoomTypeCount> getRoomCountsByType() {
        return roomRepository.countRoomsByRoomTypeId();
    }
    //長期不可用房間狀態的房型數量(key=roomTypeId, value=roomCount)
    @Transactional(readOnly = true)
    public List<RoomTypeCount> getRoomCountsByTypeWithStatus() {
        return roomRepository.countRoomsByRoomTypeIdWithStatus();
    }

    /**
     * 【重構】更新所有房型的基本統計。
     * 現在這個方法應該是清除所有相關的快取，讓它們在下次被請求時重新計算。
     */
    @Transactional
    public void updateAllRoomTypeBasicStatistics() {
        List<RoomTypeCount> allRoomTypes = roomRepository.countRoomsByRoomTypeId();
        for (RoomTypeCount roomType : allRoomTypes) {
            // 呼叫快取清除方法
            invalidateRoomTypeStatsCache(roomType.getRoomTypeIdDTO());
        }
		System.out.println("!!! 已觸發所有房型統計 DTO 快取的清除 !!!");
    }
}