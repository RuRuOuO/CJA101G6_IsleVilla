package com.islevilla.lai.shuttle.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShuttleSeatAvailabilityService {

	@Autowired
	private ShuttleSeatAvailabilityRepository shuttleSeatAvailabilityRepository;

	// 獲取所有座位可用性記錄
	public List<ShuttleSeatAvailability> getAllSeatAvailability() {
		return shuttleSeatAvailabilityRepository.findAll();
	}

	// 根據複合主鍵獲取座位可用性
	public Optional<ShuttleSeatAvailability> getSeatAvailabilityById(
			ShuttleSeatAvailability.ShuttleSeatAvailabilityId id) {
		return shuttleSeatAvailabilityRepository.findById(id);
	}

	/**
	 * 根據班次ID和日期獲取座位可用性（便捷方法）
	 */
	public Optional<ShuttleSeatAvailability> getSeatAvailabilityById(Integer scheduleId, LocalDate date) {
		ShuttleSeatAvailability.ShuttleSeatAvailabilityId id = new ShuttleSeatAvailability.ShuttleSeatAvailabilityId(
				scheduleId, date);
		return shuttleSeatAvailabilityRepository.findById(id);
	}

	// 獲取特定班次和日期的座位設定
	public Optional<ShuttleSeatAvailability> getSeatAvailability(Integer scheduleId, LocalDate date) {
		return shuttleSeatAvailabilityRepository.findByShuttleScheduleIdAndShuttleDate(scheduleId, date);
	}

	// 根據班次ID獲取座位可用性
	public List<ShuttleSeatAvailability> getSeatAvailabilityByScheduleId(Integer scheduleId) {
		return shuttleSeatAvailabilityRepository.findByShuttleScheduleId(scheduleId);
	}

	// 根據日期獲取座位可用性
	public List<ShuttleSeatAvailability> getSeatAvailabilityByDate(LocalDate date) {
		return shuttleSeatAvailabilityRepository.findByShuttleDate(date);
	}

	// 根據班次ID和日期獲取座位可用性
	public Optional<ShuttleSeatAvailability> getSeatAvailabilityByScheduleAndDate(Integer scheduleId, LocalDate date) {
		return shuttleSeatAvailabilityRepository.findByShuttleScheduleIdAndShuttleDate(scheduleId, date);
	}

	// 根據日期範圍獲取座位可用性
	public List<ShuttleSeatAvailability> getSeatAvailabilityByDateRange(LocalDate startDate, LocalDate endDate) {
		return shuttleSeatAvailabilityRepository.findByShuttleDateBetween(startDate, endDate);
	}

	// 根據班次ID和日期範圍獲取座位可用性
	public List<ShuttleSeatAvailability> getSeatAvailabilityByScheduleAndDateRange(Integer scheduleId,
			LocalDate startDate, LocalDate endDate) {
		return shuttleSeatAvailabilityRepository.findByShuttleScheduleIdAndShuttleDateBetween(scheduleId, startDate,
				endDate);
	}

	// 獲取座位數量大於等於指定值的記錄
	public List<ShuttleSeatAvailability> getSeatAvailabilityWithMinSeats(Integer minSeats) {
		return shuttleSeatAvailabilityRepository.findBySeatQuantityGreaterThanEqual(minSeats);
	}

	// 獲取特定日期座位數量大於等於指定值的記錄
	public List<ShuttleSeatAvailability> getSeatAvailabilityByDateWithMinSeats(LocalDate date, Integer minSeats) {
		return shuttleSeatAvailabilityRepository.findByShuttleDateAndSeatQuantityGreaterThanEqual(date, minSeats);
	}

	// 獲取特定班次和日期的實際可用座位數（扣除已預約數量）
	public Integer getAvailableSeats(Integer scheduleId, LocalDate date, Integer direction) {
		return shuttleSeatAvailabilityRepository.getAvailableSeats(scheduleId, date, direction);
	}

	// 檢查特定班次和日期是否存在座位設定
	public boolean existsSeatConfiguration(Integer scheduleId, LocalDate date) {
		return shuttleSeatAvailabilityRepository.existsByShuttleScheduleIdAndShuttleDate(scheduleId, date);
	}

	/**
	 * 查找指定日期範圍內座位充足的班次
	 */
	public List<ShuttleSeatAvailability> getAvailableSeatsInDateRange(LocalDate startDate, LocalDate endDate,
			Integer minSeats) {
		return shuttleSeatAvailabilityRepository.findAvailableSeatsInDateRange(startDate, endDate, minSeats);
	}

	// 保存或更新座位可用性記錄
	@Transactional
	public ShuttleSeatAvailability saveSeatAvailability(ShuttleSeatAvailability seatAvailability) {
		return shuttleSeatAvailabilityRepository.save(seatAvailability);
	}

	// 創建新的座位可用性記錄
	@Transactional
	public ShuttleSeatAvailability createSeatAvailability(Integer scheduleId, LocalDate date, Integer seatQuantity) {
		ShuttleSeatAvailability seatAvailability = new ShuttleSeatAvailability();
		seatAvailability.setShuttleScheduleId(scheduleId);
		seatAvailability.setShuttleDate(date);
		seatAvailability.setSeatQuantity(seatQuantity != null ? seatQuantity : 100);

		return shuttleSeatAvailabilityRepository.save(seatAvailability);
	}

	// 更新座位數量
	@Transactional
	public ShuttleSeatAvailability updateSeatQuantity(Integer scheduleId, LocalDate date, Integer newQuantity) {
		Optional<ShuttleSeatAvailability> availabilityOpt = getSeatAvailability(scheduleId, date);

		if (availabilityOpt.isPresent()) { // 如果存在，更新座位數量
			ShuttleSeatAvailability availability = availabilityOpt.get();
			availability.setSeatQuantity(newQuantity);
			return shuttleSeatAvailabilityRepository.save(availability);
		} else { // 如果不存在，創建新記錄
			return createSeatAvailability(scheduleId, date, newQuantity);
		}
	}

	// 批量創建多天的座位可用性記錄
	@Transactional
	public void createSeatAvailabilitiesForDateRange(Integer scheduleId, LocalDate startDate, LocalDate endDate,
			Integer seatQuantity) {
		LocalDate currentDate = startDate;
		while (!currentDate.isAfter(endDate)) {
			if (!existsSeatConfiguration(scheduleId, currentDate)) {
				createSeatAvailability(scheduleId, currentDate, seatQuantity);
			}
			currentDate = currentDate.plusDays(1);
		}
	}

	// 刪除特定班次的所有座位設定
	@Transactional
	public void deleteSeatAvailabilitiesBySchedule(Integer scheduleId) {
		shuttleSeatAvailabilityRepository.deleteByShuttleScheduleId(scheduleId);
	}

	// 刪除特定日期的所有座位設定
	@Transactional
	public void deleteSeatAvailabilitiesByDate(LocalDate date) {
		shuttleSeatAvailabilityRepository.deleteByShuttleDate(date);
	}

	// 檢查是否有足夠的座位可供預約
	public boolean hasEnoughSeatsForReservation(Integer scheduleId, LocalDate date, Integer direction,
			Integer requestedSeats) {
		Integer availableSeats = getAvailableSeats(scheduleId, date, direction);
		return availableSeats != null && availableSeats >= requestedSeats;
	}

	// 獲取座位使用率（已預約座位數/總座位數）
	public Double getSeatUtilizationRate(Integer scheduleId, LocalDate date, Integer direction) {
		Optional<ShuttleSeatAvailability> seatAvailabilityOpt = getSeatAvailability(scheduleId, date);
		if (seatAvailabilityOpt.isEmpty()) {
			return null;
		}

		Integer totalSeats = seatAvailabilityOpt.get().getSeatQuantity();
		Integer availableSeats = getAvailableSeats(scheduleId, date, direction);

		if (totalSeats == null || totalSeats == 0 || availableSeats == null) {
			return null;
		}

		Integer reservedSeats = totalSeats - availableSeats;
		return (double) reservedSeats / totalSeats;
	}

	// 預訂座位（減少可用座位數量）
	@Transactional
	public boolean reserveSeats(Integer scheduleId, LocalDate date, Integer seatCount) {
		Optional<ShuttleSeatAvailability> availabilityOpt = getSeatAvailability(scheduleId, date);

		if (availabilityOpt.isPresent()) {
			ShuttleSeatAvailability availability = availabilityOpt.get();
			if (availability.getSeatQuantity() >= seatCount) {
				availability.setSeatQuantity(availability.getSeatQuantity() - seatCount);
				shuttleSeatAvailabilityRepository.save(availability);
				return true;
			}
		}
		return false;
	}

	// 取消預訂（增加可用座位數量）
	@Transactional
	public boolean cancelReservation(Integer scheduleId, LocalDate date, Integer seatCount) {
		Optional<ShuttleSeatAvailability> availabilityOpt = getSeatAvailability(scheduleId, date);

		if (availabilityOpt.isPresent()) {
			ShuttleSeatAvailability availability = availabilityOpt.get();
			availability.setSeatQuantity(availability.getSeatQuantity() + seatCount);
			shuttleSeatAvailabilityRepository.save(availability);
			return true;
		}
		return false;
	}

	/**
	 * 檢查是否有足夠的座位可預訂
	 */
	public boolean hasEnoughSeats(Integer scheduleId, LocalDate date, Integer requiredSeats) {
		Optional<ShuttleSeatAvailability> availabilityOpt = getSeatAvailability(scheduleId, date);

		return availabilityOpt.map(availability -> availability.getSeatQuantity() >= requiredSeats).orElse(false);
	}

	/**
	 * 獲取剩餘座位數量
	 */
	public Integer getAvailableSeats(Integer scheduleId, LocalDate date) {
		Optional<ShuttleSeatAvailability> availabilityOpt = shuttleSeatAvailabilityRepository
				.findByShuttleScheduleIdAndShuttleDate(scheduleId, date);

		return availabilityOpt.map(ShuttleSeatAvailability::getSeatQuantity).orElse(0);
	}

	/**
	 * 檢查記錄是否存在
	 */
	public boolean existsSeatAvailability(Integer scheduleId, LocalDate date) {
		return shuttleSeatAvailabilityRepository.existsByShuttleScheduleIdAndShuttleDate(scheduleId, date);
	}

	/**
	 * 獲取指定班次在日期範圍內的總座位數
	 */
	public Long getTotalAvailableSeatsForScheduleInDateRange(Integer scheduleId, LocalDate startDate,
			LocalDate endDate) {
		Long total = shuttleSeatAvailabilityRepository.getTotalAvailableSeatsForScheduleInDateRange(scheduleId,
				startDate, endDate);
		return total != null ? total : 0L;
	}

	// 刪除座位可用性記錄
	@Transactional
	public void deleteSeatAvailability(Integer scheduleId, LocalDate date) {
		ShuttleSeatAvailability.ShuttleSeatAvailabilityId id = new ShuttleSeatAvailability.ShuttleSeatAvailabilityId(
				scheduleId, date);
		shuttleSeatAvailabilityRepository.deleteById(id);
	}

	// 檢查特定班次是否還有可用座位
	public boolean hasAvailableSeats(Integer scheduleId, LocalDate date, Integer direction, Integer requestedSeats) {
		// 直接使用 Repository 的查詢方法獲取可用座位數
		Integer availableSeats = shuttleSeatAvailabilityRepository.getAvailableSeats(scheduleId, date, direction);

		// 如果查詢結果為 null，表示該日期沒有設定座位數量，預設不允許預約
		if (availableSeats == null) {
			return false;
		}

		return availableSeats >= requestedSeats;
	}

	// 獲取特定班次在特定日期和方向的可用座位數
	public Integer getAvailableSeatsCount(Integer scheduleId, LocalDate date, Integer direction) {
		Integer availableSeats = shuttleSeatAvailabilityRepository.getAvailableSeats(scheduleId, date, direction);
		return availableSeats != null ? Math.max(0, availableSeats) : 0;
	}

	// 檢查特定班次和日期是否有座位設定
	public boolean hasSeatConfiguration(Integer scheduleId, LocalDate date) {
		return shuttleSeatAvailabilityRepository.existsByShuttleScheduleIdAndShuttleDate(scheduleId, date);
	}

	// 驗證預約是否可行（檢查座位可用性）
	public boolean validateReservation(ShuttleReservation reservation) {
		Integer scheduleId = reservation.getShuttleSchedule().getShuttleScheduleId();
		LocalDate date = reservation.getShuttleDate();
		Integer direction = reservation.getShuttleDirection();
		Integer requestedSeats = reservation.getShuttleNumber();

		// 檢查是否有座位設定
		if (!hasSeatConfiguration(scheduleId, date)) {
			return false;
		}

		// 檢查座位可用性
		return hasAvailableSeats(scheduleId, date, direction, requestedSeats);
	}
}
