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
import org.springframework.data.repository.query.Param;

import com.islevilla.chen.room.model.RoomTypeCount;
import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeRepository;
import com.islevilla.chen.roomType.model.RoomTypeService;
import com.islevilla.chen.util.exception.BusinessException;
import com.islevilla.wei.room.model.RoomRVOrderRepository;

@Service
public class RoomTypeAvailabilityService {
	
	@Autowired
	private RoomTypeAvailabilityRepository roomTypeAvailabilityRepository;
	
	@Autowired
	private RoomTypeService roomTypeService;
	
	
	// 全部房型和單月份查詢
	@Transactional(readOnly = true)
	public List<RoomTypeAvailability> findAllByYearMonth(int year,int month) {
		List<RoomTypeAvailability> result = roomTypeAvailabilityRepository.findAllByYearMonth(year,month);
		if (result.isEmpty()) {
			throw new BusinessException("查無當月記錄");
		}
		return result;
	}
	
	
	// 根據房型ID和單月份查詢
	@Transactional(readOnly = true)
	public List<RoomTypeAvailability> findByRoomTypeIdAndYearMonth(Integer roomTypeId, int year, int month){
		return roomTypeAvailabilityRepository.findByRoomTypeIdAndYearMonth(roomTypeId, year, month);
	}
	
	//當Room資料庫表格更動時，重新計算特定房型的可用數量
	@Transactional
	public void recalculateAvailability(RoomTypeCount roomTypeCount) {
		// 重新計算該房型未來所有日期的可用數量
		LocalDate startDate = LocalDate.now();
		LocalDate endDate = startDate.plusMonths(3); // 未來三個月
		
		LocalDate date = startDate;
		while (!date.isAfter(endDate)) {
			// 計算該日期的實際可用數量
			RoomTypeAvailability daliyRoomTypeAvailability = new RoomTypeAvailability();
			RoomTypeAvailabilityId roomTypeAvailabilityId = new RoomTypeAvailabilityId();
            		
			// 獲取 RoomType 實體
			RoomType roomType = roomTypeService.findById(roomTypeCount.getRoomTypeIdDTO());
			if (roomType != null) {
				roomTypeAvailabilityId.setRoomTypeId(roomTypeCount.getRoomTypeIdDTO());
				roomTypeAvailabilityId.setRoomTypeAvailabilityDate(date);
				
				daliyRoomTypeAvailability.setRoomTypeAvailabilityId(roomTypeAvailabilityId);
				daliyRoomTypeAvailability.setRoomType(roomType); // 設定 RoomType 實體
				            daliyRoomTypeAvailability.setRoomTypeAvailabilityCount(roomTypeCount.getRoomAvailableDTO());

				roomTypeAvailabilityRepository.save(daliyRoomTypeAvailability);
			}			
			date = date.plusDays(1);
		}
	}
	
	
//	/**
//	  * 訂房送出後調整空房庫存
//	  */
//	 @Transactional
//	 public void decreaseAvailability(Integer roomTypeId, LocalDate date, int count) {
//	  Optional<RoomTypeAvailability> opt = roomTypeAvailabilityRepository
//	   .findByRoomTypeAvailabilityId_RoomTypeIdAndRoomTypeAvailabilityId_RoomTypeAvailabilityDate(roomTypeId, date);
//	  if (opt.isPresent()) {
//	   RoomTypeAvailability availability = opt.get();
//	   int newCount = availability.getRoomTypeAvailabilityCount() - count;
//	   if (newCount < 0) newCount = 0;
//	   availability.setRoomTypeAvailabilityCount(newCount);
//	   roomTypeAvailabilityRepository.save(availability);
//	  } else {
//	   throw new RuntimeException("查無庫存資料: 房型ID=" + roomTypeId + ", 日期=" + date);
//	  }
//	 }
}
