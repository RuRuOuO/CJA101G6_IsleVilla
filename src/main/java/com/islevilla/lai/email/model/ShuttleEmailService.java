package com.islevilla.lai.email.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.islevilla.lai.shuttle.model.SeatRepository;
import com.islevilla.lai.shuttle.model.ShuttleReservation;
import com.islevilla.lai.shuttle.model.ShuttleReservationRepository;
import com.islevilla.lai.shuttle.model.ShuttleReservationSeat;
import com.islevilla.lai.shuttle.model.ShuttleReservationSeatRepository;

@Service
public class ShuttleEmailService {
	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private TemplateEngine templateEngine;

	@Autowired
	private ShuttleReservationRepository shuttleRVRepository;

	@Autowired
	private ShuttleReservationSeatRepository shuttleRVSeatRepository;

	@Autowired
	private SeatRepository seatRepository;

	// 寄送確認接駁預約郵件
	public void sendShuttleReservationConfirmation(Integer shuttleReservationId) {
		try {
			// 查詢預約資訊
			ShuttleReservation reservation = shuttleRVRepository.findById(shuttleReservationId)
					.orElseThrow(() -> new RuntimeException("找不到預約記錄"));

			// 查詢預約座位
			List<ShuttleReservationSeat> reservationSeats = shuttleRVSeatRepository
					.findByShuttleReservationId(shuttleReservationId);

			// 取得座位號碼
			List<String> seatNumbers = reservationSeats
					.stream().map(rs -> seatRepository.findById(rs.getSeatId())
							.orElseThrow(() -> new RuntimeException("找不到座位")).getSeatNumber())
					.collect(Collectors.toList());

			// 準備郵件內容
			ShuttleReservationEmailData emailData = new ShuttleReservationEmailData();
			emailData.setMemberName(reservation.getMembers().getMemberName());
			emailData.setMemberEmail(reservation.getMembers().getMemberEmail());
			emailData.setShuttleReservationId(reservation.getShuttleReservationId());
			emailData.setRoomReservationId(reservation.getRoomRVOrder().getRoomReservationId());
			emailData.setScheduleId(reservation.getShuttleSchedule().getShuttleScheduleId());
			emailData.setShuttleDate(reservation.getShuttleDate());
			emailData.setShuttleDirection(reservation.getShuttleDirection() == 0 ? "去程" : "回程");
			emailData.setShuttleTime(reservation.getShuttleSchedule().getShuttleDepartureTime());
			emailData.setShuttleNumber(reservation.getShuttleNumber());
			emailData.setSeatNumbers(seatNumbers);
			emailData.setBookingTime(LocalDateTime.now());

			// 發送郵件
			sendConfirmationEmail(emailData);

		} catch (Exception e) {
			System.err.println("發送接駁預約確認郵件失敗：" + e.getMessage());
			e.printStackTrace();
		}
	}

	// 寄送取消接駁預約郵件
	public void sendShuttleReservationCancellation(Integer shuttleReservationId) {
		try {
			// 查詢預約資訊
			ShuttleReservation reservation = shuttleRVRepository.findById(shuttleReservationId)
					.orElseThrow(() -> new RuntimeException("找不到預約記錄"));

			// 準備郵件內容
			ShuttleCancellationEmailData emailData = new ShuttleCancellationEmailData();
			emailData.setMemberName(reservation.getMembers().getMemberName());
			emailData.setMemberEmail(reservation.getMembers().getMemberEmail());
			emailData.setShuttleReservationId(reservation.getShuttleReservationId());
			emailData.setRoomReservationId(reservation.getRoomRVOrder().getRoomReservationId());
			emailData.setScheduleId(reservation.getShuttleSchedule().getShuttleScheduleId());
			emailData.setShuttleDate(reservation.getShuttleDate());
			emailData.setShuttleDirection(reservation.getShuttleDirection() == 0 ? "去程" : "回程");
			emailData.setShuttleTime(reservation.getShuttleSchedule().getShuttleDepartureTime());
			emailData.setCancellationTime(LocalDateTime.now());

			// 發送取消確認郵件
			sendCancellationEmail(emailData);

		} catch (Exception e) {
			System.err.println("發送接駁取消確認郵件失敗：" + e.getMessage());
			e.printStackTrace();
		}
	}

	private void sendConfirmationEmail(ShuttleReservationEmailData emailData) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

		// 設定郵件基本資訊
		helper.setFrom("no-reply@islevilla.com");
		helper.setTo(emailData.getMemberEmail());
		helper.setSubject("【測試中】【預約確認】接駁服務預約成功通知");

		// 準備模板變數
		Context context = new Context();
		context.setVariable("memberName", emailData.getMemberName());
		context.setVariable("shuttleReservationId", emailData.getShuttleReservationId());
		context.setVariable("roomReservationId", emailData.getRoomReservationId());
		context.setVariable("scheduleId", emailData.getScheduleId());
		context.setVariable("shuttleDate", emailData.getShuttleDate());
		context.setVariable("shuttleDirection", emailData.getShuttleDirection());
		context.setVariable("shuttleTime", emailData.getShuttleTime());
		context.setVariable("shuttleNumber", emailData.getShuttleNumber());
		context.setVariable("seatNumbers", emailData.getSeatNumbers());
		context.setVariable("bookingTime", emailData.getBookingTime());

		// 產生 HTML 內容
		String htmlContent = templateEngine.process("email/email-shuttle-reservation-confirmation", context);
		helper.setText(htmlContent, true);

		// 發送郵件
		mailSender.send(message);
	}

	private void sendCancellationEmail(ShuttleCancellationEmailData emailData) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

		// 設定郵件基本資訊
		helper.setTo(emailData.getMemberEmail());
		helper.setSubject("【測試中】【取消確認】接駁服務預約取消通知");

		// 準備模板變數
		Context context = new Context();
		context.setVariable("memberName", emailData.getMemberName());
		context.setVariable("shuttleReservationId", emailData.getShuttleReservationId());
		context.setVariable("roomReservationId", emailData.getRoomReservationId());
		context.setVariable("scheduleId", emailData.getScheduleId());
		context.setVariable("shuttleDate", emailData.getShuttleDate());
		context.setVariable("shuttleDirection", emailData.getShuttleDirection());
		context.setVariable("shuttleTime", emailData.getShuttleTime());
		context.setVariable("cancellationTime", emailData.getCancellationTime());

		// 產生 HTML 內容
		String htmlContent = templateEngine.process("email/email-shuttle-reservation-cancellation", context);
		helper.setText(htmlContent, true);

		// 發送郵件
		mailSender.send(message);
	}

	// 內部資料類別 - 確認預約
	public static class ShuttleReservationEmailData {
		private String memberName;
		private String memberEmail;
		private Integer shuttleReservationId;
		private Integer roomReservationId;
		private Integer scheduleId;
		private LocalDate shuttleDate;
		private String shuttleDirection;
		private LocalTime shuttleTime;
		private Integer shuttleNumber;
		private List<String> seatNumbers;
		private LocalDateTime bookingTime;

		// Getters and Setters
		public String getMemberName() {
			return memberName;
		}

		public void setMemberName(String memberName) {
			this.memberName = memberName;
		}

		public String getMemberEmail() {
			return memberEmail;
		}

		public void setMemberEmail(String memberEmail) {
			this.memberEmail = memberEmail;
		}

		public Integer getShuttleReservationId() {
			return shuttleReservationId;
		}

		public void setShuttleReservationId(Integer shuttleReservationId) {
			this.shuttleReservationId = shuttleReservationId;
		}

		public Integer getRoomReservationId() {
			return roomReservationId;
		}

		public void setRoomReservationId(Integer roomReservationId) {
			this.roomReservationId = roomReservationId;
		}

		public Integer getScheduleId() {
			return scheduleId;
		}

		public void setScheduleId(Integer scheduleId) {
			this.scheduleId = scheduleId;
		}

		public LocalDate getShuttleDate() {
			return shuttleDate;
		}

		public void setShuttleDate(LocalDate shuttleDate) {
			this.shuttleDate = shuttleDate;
		}

		public String getShuttleDirection() {
			return shuttleDirection;
		}

		public void setShuttleDirection(String shuttleDirection) {
			this.shuttleDirection = shuttleDirection;
		}

		public LocalTime getShuttleTime() {
			return shuttleTime;
		}

		public void setShuttleTime(LocalTime shuttleTime) {
			this.shuttleTime = shuttleTime;
		}

		public Integer getShuttleNumber() {
			return shuttleNumber;
		}

		public void setShuttleNumber(Integer shuttleNumber) {
			this.shuttleNumber = shuttleNumber;
		}

		public List<String> getSeatNumbers() {
			return seatNumbers;
		}

		public void setSeatNumbers(List<String> seatNumbers) {
			this.seatNumbers = seatNumbers;
		}

		public LocalDateTime getBookingTime() {
			return bookingTime;
		}

		public void setBookingTime(LocalDateTime bookingTime) {
			this.bookingTime = bookingTime;
		}
	}

	// 內部資料類別 - 取消預約
	public static class ShuttleCancellationEmailData {
		private String memberName;
		private String memberEmail;
		private Integer shuttleReservationId;
		private Integer roomReservationId;
		private Integer scheduleId;
		private LocalDate shuttleDate;
		private String shuttleDirection;
		private LocalTime shuttleTime;
		private LocalDateTime cancellationTime;

		// Getters and Setters
		public String getMemberName() {
			return memberName;
		}

		public void setMemberName(String memberName) {
			this.memberName = memberName;
		}

		public String getMemberEmail() {
			return memberEmail;
		}

		public void setMemberEmail(String memberEmail) {
			this.memberEmail = memberEmail;
		}

		public Integer getShuttleReservationId() {
			return shuttleReservationId;
		}

		public void setShuttleReservationId(Integer shuttleReservationId) {
			this.shuttleReservationId = shuttleReservationId;
		}

		public Integer getRoomReservationId() {
			return roomReservationId;
		}

		public void setRoomReservationId(Integer roomReservationId) {
			this.roomReservationId = roomReservationId;
		}

		public Integer getScheduleId() {
			return scheduleId;
		}

		public void setScheduleId(Integer scheduleId) {
			this.scheduleId = scheduleId;
		}

		public LocalDate getShuttleDate() {
			return shuttleDate;
		}

		public void setShuttleDate(LocalDate shuttleDate) {
			this.shuttleDate = shuttleDate;
		}

		public String getShuttleDirection() {
			return shuttleDirection;
		}

		public void setShuttleDirection(String shuttleDirection) {
			this.shuttleDirection = shuttleDirection;
		}

		public LocalTime getShuttleTime() {
			return shuttleTime;
		}

		public void setShuttleTime(LocalTime shuttleTime) {
			this.shuttleTime = shuttleTime;
		}

		public LocalDateTime getCancellationTime() {
			return cancellationTime;
		}

		public void setCancellationTime(LocalDateTime cancellationTime) {
			this.cancellationTime = cancellationTime;
		}
	}
}
