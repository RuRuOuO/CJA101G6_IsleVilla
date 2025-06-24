package com.islevilla.lai.shuttle.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.islevilla.lai.members.model.Members;
import com.islevilla.wei.room.model.RoomRVOrder;
import com.islevilla.wei.room.model.RoomRVOrderRepository;

@Service
public class ShuttleRVFrontEndService {
	@Autowired
	private ShuttleReservationRepository shuttleRVRepository;
	
	@Autowired
	private ShuttleScheduleRepository shuttleScheduleRepository;
	
	@Autowired
	private ShuttleSeatAvailabilityRepository shuttleSeatAvailabilityRepository;
	
	@Autowired
	private RoomRVOrderRepository roomRVOrderRepository;
	
	@Autowired
	private TempShuttleRVRequestRepository tempShuttleRVRequestRepository;
	
	

	// Controller: 步驟1到步驟2的驗證
	public boolean validateMemberAndRoomReservation(Integer memberId, Integer roomReservationId, LocalDate shuttleDate,
			Integer shuttleDirection) {
		switch (shuttleDirection) {
		case 0:
			return roomRVOrderRepository
					.validateMemberAndRoomReservationOutward(memberId, roomReservationId, shuttleDate).isPresent();
		case 1:
			return roomRVOrderRepository
					.validateMemberAndRoomReservationReturn(memberId, roomReservationId, shuttleDate).isPresent();
		default:
			return false;
		}

	}
	
	// 儲存預約請求
    @Transactional
    public Integer saveReservationRequest(TempShuttleRVRequestDTO dto) {
    	TempShuttleRVRequest tempShuttleRVRequest = new TempShuttleRVRequest();
    	tempShuttleRVRequest.setMemberId(dto.getMemberId());
    	tempShuttleRVRequest.setRoomReservationId(dto.getRoomReservationId());
    	tempShuttleRVRequest.setShuttleDate(dto.getShuttleDate());
    	tempShuttleRVRequest.setShuttleNumber(dto.getShuttleNumber());
    	tempShuttleRVRequest.setShuttleDirection(dto.getShuttleDirection());
        
    	tempShuttleRVRequest = tempShuttleRVRequestRepository.save(tempShuttleRVRequest);
        return tempShuttleRVRequest.getId();
    }
    
    /**
     * 取得可用班次
     */
    public List<ShuttleScheduleWithAvailabilityDTO> getAvailableSchedules(LocalDate shuttleDate, Integer shuttleDirection) {
        List<ShuttleSchedule> schedules = shuttleScheduleRepository.findByShuttleDirectionOrderByShuttleDepartureTime(shuttleDirection);
        
        return schedules.stream()
            .map(schedule -> {
                Integer availableSeats = calculateAvailableSeats(schedule.getShuttleScheduleId(), shuttleDate);
                return new ShuttleScheduleWithAvailabilityDTO(
                    schedule.getShuttleScheduleId(),
                    schedule.getShuttleDirection(),
                    schedule.getShuttleDepartureTime(),
                    schedule.getShuttleArrivalTime(),
                    availableSeats
                );
            })
            .filter(dto -> dto.getAvailableSeats() > 0) // 只返回有可用座位的班次
            .collect(Collectors.toList());
    }
    
    /**
     * 計算可用座位數
     */
    private Integer calculateAvailableSeats(Integer scheduleId, LocalDate shuttleDate) {
        // 先查詢該班次該日期的座位總數設定
        Optional<ShuttleSeatAvailability> availability = 
            shuttleSeatAvailabilityRepository.findByShuttleScheduleIdAndShuttleDate(scheduleId, shuttleDate);
        
        Integer totalSeats = availability.map(ShuttleSeatAvailability::getSeatQuantity).orElse(100);
        
        // 查詢已預約的座位數
        Long reservedSeats = shuttleRVRepository.countReservationsByDateAndScheduleId(shuttleDate, scheduleId);
        
        return totalSeats - reservedSeats.intValue();
    }
}
