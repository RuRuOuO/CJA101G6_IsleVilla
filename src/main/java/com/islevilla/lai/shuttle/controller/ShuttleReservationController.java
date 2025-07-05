package com.islevilla.lai.shuttle.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.islevilla.lai.members.model.Members;
import com.islevilla.lai.shuttle.model.SeatDTO;
import com.islevilla.lai.shuttle.model.ShuttleRVFrontEndService;
import com.islevilla.lai.shuttle.model.ShuttleReservation;
import com.islevilla.lai.shuttle.model.ShuttleReservationSeatService;
import com.islevilla.lai.shuttle.model.ShuttleReservationService;
import com.islevilla.lai.shuttle.model.ShuttleScheduleWithAvailabilityDTO;
import com.islevilla.lai.shuttle.model.ShuttleSeatAvailabilityService;
import com.islevilla.lai.shuttle.model.TempShuttleRVRequestDTO;
import com.islevilla.lai.shuttle.model.TempShuttleRVSummaryDTO;
import com.islevilla.wei.room.model.RoomRVDetailService;
import com.islevilla.wei.room.model.RoomRVOrder;
import com.islevilla.wei.room.model.RoomRVOrderService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ShuttleReservationController {

	@Autowired
	private RoomRVOrderService roomRVOrderService;

	@Autowired
	private RoomRVDetailService roomRVDetailService;

	@Autowired
	private ShuttleReservationService shuttleReservationService;

	@Autowired
	private ShuttleReservationSeatService shuttleReservationSeatService;

	@Autowired
	private ShuttleRVFrontEndService shuttleRVFrontEndService;

	@Autowired
	private ShuttleSeatAvailabilityService shuttleSeatAvailabilityService;

	// 前台渲染會員接駁預約
	@GetMapping("/member/shuttle/list")
	public String getShuttleRVFromMember(Model model, HttpSession session) {
		Members member = (Members) session.getAttribute("member");

		if (member == null) {
			return "redirect:/member/login";
		}

		// 查詢預約
		List<ShuttleReservation> shuttleReservationList = shuttleReservationService.getReservationsByMember(member);
		if (!shuttleReservationList.isEmpty()) {
			model.addAttribute("shuttleReservationList", shuttleReservationList);
		}
		return "front-end/member/member-shuttle-list";
	}

	// 後台 - 會員取消接駁預約
	@PostMapping("/member/shuttle/list/cancel")
	public String cancelShuttleRVFromMember(@RequestParam("shuttleReservationId") Integer shuttleReservationId,
			Model model, HttpSession session) {
		Members member = (Members) session.getAttribute("member");

		if (member == null) {
			return "redirect:/member/login";
		}
		boolean succeed = false;
		try {
			ShuttleReservation reservation = shuttleReservationService.cancelReservation(shuttleReservationId);
			shuttleReservationSeatService.deleteByShuttleReservationId(shuttleReservationId);
			succeed = shuttleSeatAvailabilityService.cancelReservation(
					reservation.getShuttleSchedule().getShuttleScheduleId(), reservation.getShuttleDate(),
					reservation.getShuttleNumber());
		} catch (RuntimeException e) {
			System.out.println(e);
		} catch (Exception e2) {
			System.out.println(e2);
		}
		if (succeed) {
			System.out.println("取消預約成功！");
		} else {
			System.out.println("取消預約失敗！");
		}

		return "redirect:/member/shuttle/list";
	}

	/**
	 * 顯示預約首頁 - 步驟1：申請預約
	 */
	@GetMapping("/shuttle/reservation")
	public String showReservationForm(HttpSession session, Model model) {
		// 檢查是否已登入
		Members member = (Members) session.getAttribute("member");
		if (member == null) {
			// 如果沒有登入，直接跳轉到登入頁面
			System.out.println("找不到登入紀錄，即將跳轉到登入頁面......");
			return "redirect:/member/login";
		}
		System.out.println("進入接駁預約頁面，會員ID: " + member.getMemberId());

		try {
			// 查詢會員的訂房記錄
			List<RoomRVOrder> memberRoomReservations = roomRVOrderService.getFutureRoomRVOrderByMember(member);
			System.out.println("找到 " + memberRoomReservations.size() + " 筆訂房記錄");

			// 為每筆訂房記錄查詢總入住人數
			Map<Integer, Integer> guestCountMap = new HashMap<>();
			Map<Integer, Boolean> departureReservedMap = new HashMap<>();
			Map<Integer, Boolean> arrivalReservedMap = new HashMap<>();
			for (RoomRVOrder roomOrder : memberRoomReservations) {
				try {
					// 查詢入住人數
					Integer guestCount = roomRVDetailService.getGuestCountByRoomRVOrder(roomOrder);
					guestCountMap.put(roomOrder.getRoomReservationId(), guestCount != null ? guestCount : 0);
					System.out.println("訂房編號 " + roomOrder.getRoomReservationId() + " 的總入住人數: " + guestCount);

					// 查詢接駁預約狀態
					boolean isDepartureReserved = shuttleReservationService
							.existsByRoomRVOrderAndShuttleReservationStatusDeparture(roomOrder);
					boolean isArrivalReserved = shuttleReservationService
							.existsByRoomRVOrderAndShuttleReservationStatusArrival(roomOrder);

					departureReservedMap.put(roomOrder.getRoomReservationId(), isDepartureReserved);
					arrivalReservedMap.put(roomOrder.getRoomReservationId(), isArrivalReserved);

					System.out.println("訂房編號 " + roomOrder.getRoomReservationId() + " 去程預約狀態: " + isDepartureReserved
							+ ", 回程預約狀態: " + isArrivalReserved);

				} catch (Exception e) {
					System.out.println("查詢訂房編號 " + roomOrder.getRoomReservationId() + " 資料時發生錯誤: " + e.getMessage());
					guestCountMap.put(roomOrder.getRoomReservationId(), 0);
					departureReservedMap.put(roomOrder.getRoomReservationId(), false);
					arrivalReservedMap.put(roomOrder.getRoomReservationId(), false);
				}
			}

			model.addAttribute("memberRoomReservations", memberRoomReservations);
			model.addAttribute("guestCountMap", guestCountMap);
			model.addAttribute("departureReservedMap", departureReservedMap);
			model.addAttribute("arrivalReservedMap", arrivalReservedMap);

		} catch (Exception e) {
			System.out.println("查詢會員訂房記錄時發生錯誤: " + e.getMessage());
			e.printStackTrace();
			model.addAttribute("memberRoomReservations", new ArrayList<>());
			model.addAttribute("guestCountMap", new HashMap<>());
			model.addAttribute("departureReservedMap", new HashMap<>());
			model.addAttribute("arrivalReservedMap", new HashMap<>());
		}

		// 初始化表單物件
		TempShuttleRVRequestDTO reservationRequest = new TempShuttleRVRequestDTO();
		// 預設設置會員ID
		reservationRequest.setMemberId(member.getMemberId());

		model.addAttribute("reservationRequest", reservationRequest);
		model.addAttribute("currentStep", 1);

		return "front-end/shuttle/shuttle-reservation";
	}

	/**
	 * 步驟1 - 驗證會員和選擇訂房資料 -> 步驟2 - 選擇班次
	 */
	@PostMapping("/shuttle/reservation/select-schedule")
	public String validateReservation(@Valid @ModelAttribute TempShuttleRVRequestDTO reservationRequest,
			BindingResult bindingResult,
			@RequestParam(value = "reservationRequestId", required = false) Integer reservationRequestId,
			HttpSession session, Model model, RedirectAttributes redirectAttributes) {

		// 檢查登入狀態
		Members member = (Members) session.getAttribute("member");
		if (member == null) {
			System.out.println("會員未登入，跳轉至登入頁面......");
			return "redirect:/member/login";
		}

		// 判斷是回到上一步還是新的預約
		if (reservationRequestId != null) {
			System.out.println("=== 回到步驟2 - 重新選擇班次 ===");
			return backToStep2(reservationRequestId, model);
		}

		System.out.println("=== 開始處理接駁預約驗證 ===");

		// 確保會員ID正確設置
		// 自動設置會員ID
		reservationRequest.setMemberId(member.getMemberId());
		System.out.println("會員ID: " + member.getMemberId());

		// 確認接收到的預約資料
		System.out.println("接收到的預約資料:");
		System.out.println("- 會員ID: " + reservationRequest.getMemberId());
		System.out.println("- 訂房編號: " + reservationRequest.getRoomReservationId());
		System.out.println("- 接駁日期: " + reservationRequest.getShuttleDate());
		System.out.println("- 接駁人數: " + reservationRequest.getShuttleNumber());
		System.out.println("- 接駁方向: " + (reservationRequest.getShuttleDirection() == 0 ? "0: 去程" : "1: 回程"));

		if (bindingResult.hasErrors()) {
			System.out.println("表單驗證失敗:");
			bindingResult.getFieldErrors().forEach(error -> {
				System.out.println("- " + error.getField() + ": " + error.getDefaultMessage());
			});

			return prepareFormPageWithError(model, member, reservationRequest);
		}

		System.out.println("表單驗證通過！");

		try {
			System.out.println("檢查點1");
			// 驗證會員和訂房資料
			boolean isValid = shuttleRVFrontEndService.validateMemberAndRoomReservation(
					reservationRequest.getMemberId(), reservationRequest.getRoomReservationId(),
					reservationRequest.getShuttleDate(), reservationRequest.getShuttleDirection());
			System.out.println("檢查點2");

			if (!isValid) {
				System.out.println("會員編號、訂房編號不匹配，或接駁日期不在住宿期間內，或訂房已取消");
				model.addAttribute("error", "會員編號、訂房編號不匹配，或接駁日期不在住宿期間內，或訂房已取消");
				model.addAttribute("reservationRequest", reservationRequest);
				model.addAttribute("currentStep", 1);
				return "front-end/shuttle/shuttle-reservation";
			}

			// 儲存預約請求並取得ID
			Integer newReservationRequestId = shuttleRVFrontEndService.saveReservationRequest(reservationRequest);

			// 查詢可用班次
			List<ShuttleScheduleWithAvailabilityDTO> availableSchedules = shuttleRVFrontEndService
					.getAvailableSchedules(reservationRequest.getShuttleDate(),
							reservationRequest.getShuttleDirection());

			if (availableSchedules.isEmpty()) {
				System.out.println("該日期沒有可用的班次");
				model.addAttribute("error", "該日期沒有可用的班次");
				model.addAttribute("reservationRequest", reservationRequest);
				model.addAttribute("currentStep", 1);
				return "front-end/shuttle/shuttle-reservation";
			}
			System.out.println("檢查點3");

			model.addAttribute("currentStep", 2);
			model.addAttribute("reservationRequestId", newReservationRequestId);
			model.addAttribute("shuttleNumber", reservationRequest.getShuttleNumber());
			model.addAttribute("availableSchedules", availableSchedules);

		} catch (Exception e) {
			System.out.println("進入Exception");
			model.addAttribute("error", "系統錯誤：" + e.getMessage());
			model.addAttribute("reservationRequest", reservationRequest);
			model.addAttribute("currentStep", 1);
			return "front-end/shuttle/shuttle-reservation";
		}

		return "front-end/shuttle/shuttle-reservation";
	}

	/*
	 * 步驟2 - 選擇班次 -> 步驟3 - 選擇座位
	 */
	@PostMapping("/shuttle/reservation/select-seats")
	public String selectSchedule(@RequestParam Integer reservationRequestId,
			@RequestParam(required = false) Integer selectedScheduleId, @RequestParam(required = false) String action,
			HttpSession session, Model model, RedirectAttributes redirectAttributes) {

		// 檢查是否已登入
		Members member = (Members) session.getAttribute("member");
		if (member == null) {
			// 如果沒有登入，直接跳轉到登入頁面
			System.out.println("找不到登入紀錄，即將跳轉到登入頁面......");
			return "redirect:/member/login";
		}

		try {
			// 取得預約請求資訊
			TempShuttleRVRequestDTO reservationRequest = shuttleRVFrontEndService
					.getReservationRequest(reservationRequestId);

			// 驗證預約請求是否屬於當前會員
			if (!reservationRequest.getMemberId().equals(member.getMemberId())) {
				redirectAttributes.addFlashAttribute("error", "無效的預約請求");
				return "redirect:/shuttle/reservation";
			}

			// 判斷是否為回到上一步的操作
			if ("back".equals(action) || selectedScheduleId == null) {
				// 回到步驟3 - 重新選擇座位的邏輯
				List<SeatDTO> seats = shuttleRVFrontEndService.getSeatsWithAvailability(
						reservationRequest.getSelectedScheduleId(), reservationRequest.getShuttleDate());

				model.addAttribute("currentStep", 3);
				model.addAttribute("reservationRequestId", reservationRequestId);
				model.addAttribute("seats", seats);
				model.addAttribute("requiredSeats", reservationRequest.getShuttleNumber());

			} else {
				// 原本選擇班次的邏輯
				System.out.println("接收到的預約資料:");
				System.out.println("- 會員ID: " + reservationRequest.getMemberId());
				System.out.println("- 訂房編號: " + reservationRequest.getRoomReservationId());
				System.out.println("- 接駁日期: " + reservationRequest.getShuttleDate());
				System.out.println("- 接駁人數: " + reservationRequest.getShuttleNumber());
				System.out.println("- 接駁方向: " + (reservationRequest.getShuttleDirection() == 0 ? "0: 去程" : "1: 回程"));
				System.out.println("- 接駁班次: " + selectedScheduleId);

				// 更新預約請求的班次資訊
				shuttleRVFrontEndService.updateScheduleSelection(reservationRequestId, selectedScheduleId);

				// 查詢座位資訊
				List<SeatDTO> seats = shuttleRVFrontEndService.getSeatsWithAvailability(selectedScheduleId,
						reservationRequest.getShuttleDate());

				model.addAttribute("currentStep", 3);
				model.addAttribute("reservationRequestId", reservationRequestId);
				model.addAttribute("seats", seats);
				model.addAttribute("requiredSeats", reservationRequest.getShuttleNumber());
			}

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "處理請求時發生錯誤：" + e.getMessage());
			return "redirect:/shuttle/reservation";
		}

		return "front-end/shuttle/shuttle-reservation";
	}

	/**
	 * 步驟3 - 選擇座位 -> 步驟4 - 確認預約資訊
	 */
	@PostMapping("/shuttle/reservation/check-details")
	public String selectSeats(@RequestParam Integer reservationRequestId, @RequestParam List<Integer> selectedSeatIds,
			HttpSession session, Model model, RedirectAttributes redirectAttributes) {

		// 檢查是否已登入
		Members member = (Members) session.getAttribute("member");
		if (member == null) {
			// 如果沒有登入，直接跳轉到登入頁面
			System.out.println("找不到登入紀錄，即將跳轉到登入頁面......");
			return "redirect:/member/login";
		}

		try {
			// 取得預約請求資訊
			TempShuttleRVRequestDTO reservationRequest = shuttleRVFrontEndService
					.getReservationRequest(reservationRequestId);

			System.out.println("接收到的預約資料:");
			System.out.println("- 會員ID: " + reservationRequest.getMemberId());
			System.out.println("- 訂房編號: " + reservationRequest.getRoomReservationId());
			System.out.println("- 接駁日期: " + reservationRequest.getShuttleDate());
			System.out.println("- 接駁人數: " + reservationRequest.getShuttleNumber());
			System.out.println("- 接駁方向: " + (reservationRequest.getShuttleDirection() == 0 ? "0: 去程" : "1: 回程"));
			System.out.println("- 接駁班次: " + reservationRequest.getSelectedScheduleId());

			// 驗證預約請求是否屬於當前會員
			if (!reservationRequest.getMemberId().equals(member.getMemberId())) {
				redirectAttributes.addFlashAttribute("error", "無效的預約請求");
				return "redirect:/shuttle/reservation";
			}

			// 驗證選擇的座位數量
			if (selectedSeatIds.size() != reservationRequest.getShuttleNumber()) {
				redirectAttributes.addFlashAttribute("error",
						"選擇的座位數量不正確，需要選擇 " + reservationRequest.getShuttleNumber() + " 個座位");
				redirectAttributes.addAttribute("reservationRequestId", reservationRequestId);
				redirectAttributes.addAttribute("action", "back");
				return "redirect:/shuttle/reservation/select-seats";
			}

			// 儲存座位選擇
			shuttleRVFrontEndService.updateSeatSelection(reservationRequestId, selectedSeatIds);

			// 取得預約摘要資訊
			TempShuttleRVSummaryDTO reservationSummary = shuttleRVFrontEndService
					.getReservationSummary(reservationRequestId);

			model.addAttribute("currentStep", 4);
			model.addAttribute("reservationRequestId", reservationRequestId);
			model.addAttribute("reservationSummary", reservationSummary);

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "選擇座位時發生錯誤：" + e.getMessage());
			redirectAttributes.addAttribute("reservationRequestId", reservationRequestId);
			redirectAttributes.addAttribute("action", "back");
			return "redirect:/shuttle/reservation/select-seats";
		}

		return "front-end/shuttle/shuttle-reservation";
	}

	/**
	 * 步驟4 - 確認預約資訊 -> 完成預約流程
	 */
	@PostMapping("/shuttle/reservation/submit")
	public String submitReservation(@RequestParam Integer reservationRequestId, Model model, HttpSession session,
			RedirectAttributes redirectAttributes) {

		// 檢查是否已登入
		Members member = (Members) session.getAttribute("member");
		if (member == null) {
			// 如果沒有登入，直接跳轉到登入頁面
			System.out.println("找不到登入紀錄，即將跳轉到登入頁面......");
			return "redirect:/member/login";
		}

		try {
			// 建立正式預約
			Integer shuttleReservationId = shuttleRVFrontEndService.createReservation(reservationRequestId);

			model.addAttribute("reservationSuccess", true);
			model.addAttribute("currentStep", 5);
			model.addAttribute("newShuttleReservationId", shuttleReservationId);

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "提交預約時發生錯誤：" + e.getMessage());
			return "redirect:/shuttle/reservation/submit";
		}

		return "/front-end/shuttle/shuttle-reservation";
	}

	/**
	 * 處理回到步驟2的邏輯
	 */
	private String backToStep2(Integer reservationRequestId, Model model) {
		try {
			System.out.println("回到步驟2，預約請求ID: " + reservationRequestId);

			TempShuttleRVRequestDTO reservationRequest = shuttleRVFrontEndService
					.getReservationRequest(reservationRequestId);

			List<ShuttleScheduleWithAvailabilityDTO> availableSchedules = shuttleRVFrontEndService
					.getAvailableSchedules(reservationRequest.getShuttleDate(),
							reservationRequest.getShuttleDirection());

			model.addAttribute("currentStep", 2);
			model.addAttribute("reservationRequestId", reservationRequestId);
			model.addAttribute("shuttleNumber", reservationRequest.getShuttleNumber());
			model.addAttribute("availableSchedules", availableSchedules);

		} catch (Exception e) {
			System.out.println("載入班次資訊時發生錯誤: " + e.getMessage());
			model.addAttribute("error", "載入班次資訊時發生錯誤：" + e.getMessage());
			return "redirect:/shuttle/reservation";
		}

		return "front-end/shuttle/shuttle-reservation";
	}

	/**
	 * 處理直接訪問班次選擇頁面的 GET 請求
	 */
	@GetMapping("/shuttle/reservation/select-schedule")
	public String redirectToReservation1(RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("error", "請從正常流程進行預約");
		return "redirect:/shuttle/reservation";
	}

	/**
	 * 處理直接訪問座位選擇頁面的 GET 請求
	 */
	@GetMapping("/shuttle/reservation/select-seats")
	public String redirectToReservation2(RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("error", "請從正常流程進行預約");
		return "redirect:/shuttle/reservation";
	}

	/**
	 * 處理直接訪問確認預約資訊頁面的 GET 請求
	 */
	@GetMapping("/shuttle/reservation/check-details")
	public String redirectToReservation3(RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("error", "請從正常流程進行預約");
		return "redirect:/shuttle/reservation";
	}

	/**
	 * 處理直接訪問預約成功頁面的 GET 請求
	 */
	@GetMapping("/shuttle/reservation/submit")
	public String redirectToReservation4(RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("error", "請從正常流程進行預約");
		return "redirect:/shuttle/reservation";
	}

	/**
	 * 準備錯誤頁面的輔助方法
	 */
	private String prepareFormPageWithError(Model model, Members member, TempShuttleRVRequestDTO reservationRequest) {
		model.addAttribute("reservationRequest", reservationRequest);
		model.addAttribute("currentStep", 1);

		try {
			List<RoomRVOrder> memberRoomReservations = roomRVOrderService.getRoomRVOrderByMember(member);
			model.addAttribute("memberRoomReservations", memberRoomReservations);
		} catch (Exception e) {
			System.out.println("載入會員訂房記錄時發生錯誤: " + e.getMessage());
			model.addAttribute("memberRoomReservations", new ArrayList<>());
		}

		return "front-end/shuttle/shuttle-reservation";
	}

}
