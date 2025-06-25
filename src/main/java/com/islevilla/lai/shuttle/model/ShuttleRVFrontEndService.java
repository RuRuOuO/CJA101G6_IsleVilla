package com.islevilla.lai.shuttle.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
	private RoomRVOrderRepository roomRVOrderRepository;
	
	@Autowired
    private SeatRepository seatRepository;
	
	@Autowired
	private ShuttleReservationRepository shuttleRVRepository;
	
	@Autowired
	private ShuttleReservationSeatRepository shuttleRVSeatRepository;
	
	@Autowired
	private ShuttleScheduleRepository shuttleScheduleRepository;
	
	@Autowired
	private ShuttleSeatAvailabilityRepository shuttleSeatAvailabilityRepository;
	
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
    
    /**
     * 取得預約請求
     */
    public TempShuttleRVRequestDTO getReservationRequest(Integer reservationRequestId) {
        TempShuttleRVRequest entity = tempShuttleRVRequestRepository.findById(reservationRequestId)
            .orElseThrow(() -> new RuntimeException("預約請求不存在"));
        
        TempShuttleRVRequestDTO dto = new TempShuttleRVRequestDTO();
        dto.setId(entity.getId());
        dto.setMemberId(entity.getMemberId());
        dto.setRoomReservationId(entity.getRoomReservationId());
        dto.setShuttleDate(entity.getShuttleDate());
        dto.setShuttleNumber(entity.getShuttleNumber());
        dto.setShuttleDirection(entity.getShuttleDirection());
        dto.setSelectedScheduleId(entity.getSelectedScheduleId());
        dto.setSelectedSeatIds(entity.getSelectedSeatIdsList());
        
        return dto;
    }
    
    /**
     * 更新班次選擇
     */
    @Transactional
    public void updateScheduleSelection(Integer reservationRequestId, Integer selectedScheduleId) {
        TempShuttleRVRequest entity = tempShuttleRVRequestRepository.findById(reservationRequestId)
            .orElseThrow(() -> new RuntimeException("預約請求不存在"));
        
        entity.setSelectedScheduleId(selectedScheduleId);
        tempShuttleRVRequestRepository.save(entity);
    }
    
    /**
     * 取得座位資訊（包含可用性）
     */
    public List<SeatDTO> getSeatsWithAvailability(Integer scheduleId, LocalDate shuttleDate) {
        List<Seat> allSeats = seatRepository.findAllByOrderBySeatNumber();
        List<Integer> occupiedSeatIds = shuttleRVSeatRepository.findOccupiedSeatIds(shuttleDate, scheduleId);
        
        return allSeats.stream()
            .map(seat -> new SeatDTO(
                seat.getSeatId(),
                seat.getSeatNumber(),
                seat.getSeatStatus(),
                occupiedSeatIds.contains(seat.getSeatId())
            ))
            .collect(Collectors.toList());
    }
    
    /**
     * 更新座位選擇
     */
    @Transactional
    public void updateSeatSelection(Integer reservationRequestId, List<Integer> selectedSeatIds) {
        TempShuttleRVRequest tempShuttleRVRequest = tempShuttleRVRequestRepository.findById(reservationRequestId)
            .orElseThrow(() -> new RuntimeException("預約請求不存在"));
        
        // 驗證座位是否可用
        List<SeatDTO> seats = getSeatsWithAvailability(tempShuttleRVRequest.getSelectedScheduleId(), tempShuttleRVRequest.getShuttleDate());
        
        for (Integer seatId : selectedSeatIds) {
            SeatDTO seat = seats.stream()
                .filter(s -> s.getSeatId().equals(seatId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("座位不存在: " + seatId));
            
            if (seat.getSeatStatus() == 0) {
                throw new RuntimeException("座位 " + seat.getSeatNumber() + " 故障無法使用");
            }
            
            if (seat.isOccupied()) {
                throw new RuntimeException("座位 " + seat.getSeatNumber() + " 已被預約");
            }
        }
        
        tempShuttleRVRequest.setSelectedSeatIdsList(selectedSeatIds);
        tempShuttleRVRequestRepository.save(tempShuttleRVRequest);
    }
    
    /**
     * 取得預約摘要
     */
    public TempShuttleRVSummaryDTO getReservationSummary(Integer reservationRequestId) {
    	TempShuttleRVRequest entity = tempShuttleRVRequestRepository.findById(reservationRequestId)
            .orElseThrow(() -> new RuntimeException("預約請求不存在"));
        
        // 取得班次資訊
        ShuttleSchedule schedule = shuttleScheduleRepository.findById(entity.getSelectedScheduleId())
            .orElseThrow(() -> new RuntimeException("班次不存在"));
        
        ShuttleScheduleWithAvailabilityDTO scheduleDTO = new ShuttleScheduleWithAvailabilityDTO(
            schedule.getShuttleScheduleId(),
            schedule.getShuttleDirection(),
            schedule.getShuttleDepartureTime(),
            schedule.getShuttleArrivalTime(),
            calculateAvailableSeats(schedule.getShuttleScheduleId(), entity.getShuttleDate())
        );
        
        // 取得選擇的座位資訊
        List<SeatDTO> selectedSeats = new ArrayList<>();
        if (entity.getSelectedSeatIdsList() != null && !entity.getSelectedSeatIdsList().isEmpty()) {
            List<Seat> seats = seatRepository.findAllById(entity.getSelectedSeatIdsList());
            selectedSeats = seats.stream()
                .map(seat -> new SeatDTO(seat.getSeatId(), seat.getSeatNumber(), seat.getSeatStatus(), false))
                .collect(Collectors.toList());
        }
        
        TempShuttleRVSummaryDTO summary = new TempShuttleRVSummaryDTO();
        summary.setMemberId(entity.getMemberId());
        summary.setRoomReservationId(entity.getRoomReservationId());
        summary.setShuttleDate(entity.getShuttleDate());
        summary.setShuttleDirection(entity.getShuttleDirection());
        summary.setShuttleNumber(entity.getShuttleNumber());
        summary.setSchedule(scheduleDTO);
        summary.setSelectedSeats(selectedSeats);
        
        return summary;
    }
}
