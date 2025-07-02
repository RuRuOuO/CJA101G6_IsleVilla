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
import com.islevilla.lai.members.model.MembersRepository;
import com.islevilla.lai.members.model.MembersService;
import com.islevilla.wei.room.model.RoomRVOrder;
import com.islevilla.wei.room.model.RoomRVOrderRepository;

@Service
public class ShuttleRVFrontEndService {

	@Autowired
	private MembersRepository membersRepository;

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
	public List<ShuttleScheduleWithAvailabilityDTO> getAvailableSchedules(LocalDate shuttleDate,
			Integer shuttleDirection) {
		List<ShuttleSchedule> schedules = shuttleScheduleRepository
				.findByShuttleDirectionOrderByShuttleDepartureTime(shuttleDirection);

		return schedules.stream().map(schedule -> {
			Integer availableSeats = calculateAvailableSeats(schedule.getShuttleScheduleId(), shuttleDate);
			return new ShuttleScheduleWithAvailabilityDTO(schedule.getShuttleScheduleId(),
					schedule.getShuttleDirection(), schedule.getShuttleDepartureTime(),
					schedule.getShuttleArrivalTime(), availableSeats);
		}).filter(dto -> dto.getAvailableSeats() > 0) // 只返回有可用座位的班次
				.collect(Collectors.toList());
	}

	/**
	 * 計算可用座位數
	 */
	private Integer calculateAvailableSeats(Integer scheduleId, LocalDate shuttleDate) {
		// 先查詢該班次該日期的座位總數設定
		Optional<ShuttleSeatAvailability> availability = shuttleSeatAvailabilityRepository
				.findByShuttleScheduleIdAndShuttleDate(scheduleId, shuttleDate);
		Integer totalSeats = availability.map(ShuttleSeatAvailability::getSeatQuantity)
				.orElse(100 - seatRepository.countUnavailableSeats());

		return totalSeats;
	}

	/**
	 * 增加座位數量 (取消預約接駁時使用)
	 * 
	 * @param shuttleScheduleId 班次編號
	 * @param shuttleDate       日期
	 * @param increaseAmount    要增加的座位數量
	 * @return 是否操作成功
	 */
	@Transactional
	public boolean increaseSeatQuantity(Integer shuttleScheduleId, LocalDate shuttleDate, Integer increaseAmount) {
		// 驗證輸入參數
		if (shuttleScheduleId == null || shuttleDate == null || increaseAmount == null) {
			throw new IllegalArgumentException("班次編號、日期和增加數量都不能為空");
		}

		if (increaseAmount <= 0) {
			throw new IllegalArgumentException("增加數量必須大於0");
		}

		// 獲取當前座位數量
		Optional<ShuttleSeatAvailability> availabilityOpt = shuttleSeatAvailabilityRepository
				.findByShuttleScheduleIdAndShuttleDate(shuttleScheduleId, shuttleDate);

		if (availabilityOpt.isPresent()) {
			// 記錄存在，更新座位數量
			Integer currentQuantity = availabilityOpt.get().getSeatQuantity();
			Integer newQuantity = currentQuantity + increaseAmount;

			int updatedRows = shuttleSeatAvailabilityRepository
					.updateSeatQuantityByShuttleScheduleIdAndShuttleDate(shuttleScheduleId, shuttleDate, newQuantity);
			return updatedRows > 0;
		} else {
			// 記錄不存在，返回 false
			return false;
		}
	}

	/**
	 * 減少座位數量 (預約成功時使用)
	 * 
	 * @param shuttleScheduleId 班次編號
	 * @param shuttleDate       日期
	 * @param decreaseAmount    要減少的座位數量
	 * @return 是否操作成功
	 */
	@Transactional
	public boolean decreaseSeatQuantity(Integer shuttleScheduleId, LocalDate shuttleDate, Integer decreaseAmount) {
		// 驗證輸入參數
		if (shuttleScheduleId == null || shuttleDate == null || decreaseAmount == null) {
			throw new IllegalArgumentException("班次編號、日期和減少數量都不能為空");
		}

		if (decreaseAmount <= 0) {
			throw new IllegalArgumentException("減少數量必須大於0");
		}

		// 獲取當前座位數量
		Optional<ShuttleSeatAvailability> availabilityOpt = shuttleSeatAvailabilityRepository
				.findByShuttleScheduleIdAndShuttleDate(shuttleScheduleId, shuttleDate);

		if (availabilityOpt.isPresent()) {
			// 記錄存在，檢查減少後座位數量是否會小於0
			Integer currentQuantity = availabilityOpt.get().getSeatQuantity();

			if (currentQuantity < decreaseAmount) {
				throw new IllegalArgumentException("減少數量(" + decreaseAmount + ")超過當前座位數量(" + currentQuantity + ")");
			}

			Integer newQuantity = currentQuantity - decreaseAmount;

			int updatedRows = shuttleSeatAvailabilityRepository
					.updateSeatQuantityByShuttleScheduleIdAndShuttleDate(shuttleScheduleId, shuttleDate, newQuantity);
			return updatedRows > 0;
		} else {
			// 創建新記錄，座位數量 = 預設值(100) - 減少數量
			ShuttleSeatAvailability newAvailability = new ShuttleSeatAvailability();
			newAvailability.setShuttleScheduleId(shuttleScheduleId);
			newAvailability.setShuttleDate(shuttleDate);
			newAvailability.setSeatQuantity(100 - seatRepository.countUnavailableSeats() - decreaseAmount);

			try {
				shuttleSeatAvailabilityRepository.save(newAvailability);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
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

		return allSeats.stream().map(seat -> new SeatDTO(seat.getSeatId(), seat.getSeatNumber(), seat.getSeatStatus(),
				occupiedSeatIds.contains(seat.getSeatId()))).collect(Collectors.toList());
	}

	/**
	 * 更新座位選擇
	 */
	@Transactional
	public void updateSeatSelection(Integer reservationRequestId, List<Integer> selectedSeatIds) {
		TempShuttleRVRequest tempShuttleRVRequest = tempShuttleRVRequestRepository.findById(reservationRequestId)
				.orElseThrow(() -> new RuntimeException("預約請求不存在"));

		// 驗證座位是否可用
		List<SeatDTO> seats = getSeatsWithAvailability(tempShuttleRVRequest.getSelectedScheduleId(),
				tempShuttleRVRequest.getShuttleDate());

		for (Integer seatId : selectedSeatIds) {
			SeatDTO seat = seats.stream().filter(s -> s.getSeatId().equals(seatId)).findFirst()
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
		TempShuttleRVRequest tempShuttleRVRequest = tempShuttleRVRequestRepository.findById(reservationRequestId)
				.orElseThrow(() -> new RuntimeException("預約請求不存在"));

		// 取得班次資訊
		ShuttleSchedule schedule = shuttleScheduleRepository.findById(tempShuttleRVRequest.getSelectedScheduleId())
				.orElseThrow(() -> new RuntimeException("班次不存在"));

		ShuttleScheduleWithAvailabilityDTO scheduleDTO = new ShuttleScheduleWithAvailabilityDTO(
				schedule.getShuttleScheduleId(), schedule.getShuttleDirection(), schedule.getShuttleDepartureTime(),
				schedule.getShuttleArrivalTime(),
				calculateAvailableSeats(schedule.getShuttleScheduleId(), tempShuttleRVRequest.getShuttleDate()));

		// 取得選擇的座位資訊
		List<SeatDTO> selectedSeats = new ArrayList<>();
		if (tempShuttleRVRequest.getSelectedSeatIdsList() != null
				&& !tempShuttleRVRequest.getSelectedSeatIdsList().isEmpty()) {
			List<Seat> seats = seatRepository.findAllById(tempShuttleRVRequest.getSelectedSeatIdsList());
			selectedSeats = seats.stream()
					.map(seat -> new SeatDTO(seat.getSeatId(), seat.getSeatNumber(), seat.getSeatStatus(), false))
					.collect(Collectors.toList());
		}

		TempShuttleRVSummaryDTO summary = new TempShuttleRVSummaryDTO();
		summary.setMemberId(tempShuttleRVRequest.getMemberId());
		summary.setRoomReservationId(tempShuttleRVRequest.getRoomReservationId());
		summary.setShuttleDate(tempShuttleRVRequest.getShuttleDate());
		summary.setShuttleDirection(tempShuttleRVRequest.getShuttleDirection());
		summary.setShuttleNumber(tempShuttleRVRequest.getShuttleNumber());
		summary.setSchedule(scheduleDTO);
		summary.setSelectedSeats(selectedSeats);

		return summary;
	}

	/**
	 * 建立正式預約
	 */
	@Transactional
	public Integer createReservation(Integer reservationRequestId) {
		TempShuttleRVRequest request = tempShuttleRVRequestRepository.findById(reservationRequestId)
				.orElseThrow(() -> new RuntimeException("預約請求不存在"));

		// 再次驗證座位可用性
		List<SeatDTO> seats = getSeatsWithAvailability(request.getSelectedScheduleId(), request.getShuttleDate());
		List<Integer> selectedSeatIds = request.getSelectedSeatIdsList();

		for (Integer seatId : selectedSeatIds) {
			SeatDTO seat = seats.stream().filter(s -> s.getSeatId().equals(seatId)).findFirst()
					.orElseThrow(() -> new RuntimeException("座位不存在"));

			if (seat.isOccupied()) {
				throw new RuntimeException("座位 " + seat.getSeatNumber() + " 已被其他人預約");
			}
		}

		// 建立接駁預約記錄
		ShuttleReservation reservation = new ShuttleReservation();

		Members members = membersRepository.findById(request.getMemberId())
				.orElseThrow(() -> new RuntimeException("找不到會員"));
		RoomRVOrder roomRVOrder = roomRVOrderRepository.findById(request.getRoomReservationId())
				.orElseThrow(() -> new RuntimeException("找不到訂房編號"));
		reservation.setMembers(members);
		reservation.setRoomRVOrder(roomRVOrder);
		reservation.setShuttleDate(request.getShuttleDate());
		ShuttleSchedule shuttleSchedule = shuttleScheduleRepository.findById(request.getSelectedScheduleId())
				.orElseThrow(() -> new RuntimeException("找不到接駁班次"));
		reservation.setShuttleSchedule(shuttleSchedule);
		reservation.setShuttleDirection(request.getShuttleDirection());
		reservation.setShuttleNumber(request.getShuttleNumber());
		reservation.setShuttleReservationStatus(1); // 正常狀態

		reservation = shuttleRVRepository.save(reservation);

		// 建立座位預約記錄
		for (Integer seatId : selectedSeatIds) {
			ShuttleReservationSeat reservationSeat = new ShuttleReservationSeat();
			reservationSeat.setShuttleReservationId(reservation.getShuttleReservationId());
			reservationSeat.setSeatId(seatId);
			shuttleRVSeatRepository.save(reservationSeat);
		}

		// 更新座位剩餘量
		try {
			boolean success = decreaseSeatQuantity(request.getSelectedScheduleId(), request.getShuttleDate(),
					request.getShuttleNumber());
			if (success) {
				System.out.println("減少座位數量成功");
			} else {
				System.out.println("減少座位數量失敗！");
			}
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}

		// 清理暫存的預約請求
		tempShuttleRVRequestRepository.delete(request);

		return reservation.getShuttleReservationId();
	}
}
