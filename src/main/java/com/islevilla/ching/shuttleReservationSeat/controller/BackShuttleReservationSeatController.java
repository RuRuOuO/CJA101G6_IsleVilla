package com.islevilla.ching.shuttleReservationSeat.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.islevilla.ching.shuttleReservationSeat.model.BackShuttleReservationSeat;
import com.islevilla.ching.shuttleReservationSeat.model.BackShuttleReservationSeatService;

@Controller
@RequestMapping("/backshuttlereservationseat")
public class BackShuttleReservationSeatController {

	private final BackShuttleReservationSeatService backShuttleReservationSeatService;

	public BackShuttleReservationSeatController(BackShuttleReservationSeatService backShuttleReservationSeatService) {
		this.backShuttleReservationSeatService = backShuttleReservationSeatService;
	}

	// 顯示所有預約
	@GetMapping("/list")
	public String listReservationSeat(Model model) {
		List<BackShuttleReservationSeat> list = backShuttleReservationSeatService.getAllShuttleReservationSeatDesc();
		model.addAttribute("shuttleReservationSeatList", list);
		return "front-end/backshuttlereservationseat/backshuttlereservationseat_list";
	}

	// 顯示查詢頁面
	@GetMapping("/getbackresseat")
	public String showReservationSelectSeatPage(Model model) {
		List<BackShuttleReservationSeat> allList = backShuttleReservationSeatService.getAllShuttleReservationSeat();

		List<Integer> reservationIdList = new ArrayList<>();
		List<Integer> seatIdList = new ArrayList<>();
		List<String> seatNumberList = new ArrayList<>();

		for (BackShuttleReservationSeat item : allList) {
			// 預約編號
			Integer resId = item.getShuttleReservationId();
			if (resId != null && !reservationIdList.contains(resId)) {
				reservationIdList.add(resId);
			}

			// 座位名稱
			Integer sId = item.getSeatId();
			if (sId != null && !seatIdList.contains(sId)) {
				seatIdList.add(sId);
			}

			// 座位號碼
			String seatNum = item.getSeat().getSeatNumber();
			if (seatNum != null && !seatNumberList.contains(seatNum)) {
				seatNumberList.add(seatNum);
			}
		}

		// 塞資料到頁面
		model.addAttribute("reservationIdList", reservationIdList);
		model.addAttribute("seatIdList", seatIdList);
		model.addAttribute("seatNumberList", seatNumberList);

		// 預設查詢結果為空
		model.addAttribute("shuttleReservationSeatList", null);

		return "front-end/backshuttlereservationseat/backshuttlereservationseat_getbackresseat";
	}

	// 單筆查詢
	@GetMapping("/getbackresseat/get")
	public String getReservationSeatById(
			@RequestParam(value = "shuttleReservationId", required = false) Integer shuttleReservationId,
			@RequestParam(value = "seatNumber", required = false) String seatNumber,
			@RequestParam(value = "seatId", required = false) Integer seatId, Model model) {

		List<BackShuttleReservationSeat> list = null;

		if (shuttleReservationId != null) {
			// 依編號查詢
			list = backShuttleReservationSeatService.getByBackShuttleReservationId(shuttleReservationId);
			model.addAttribute("selectedShuttleReservationId", shuttleReservationId);
		} else if (seatNumber != null && !seatNumber.trim().isEmpty()) {
			// 依座號查詢
			list = backShuttleReservationSeatService.getBySeatNumber(seatNumber);
			model.addAttribute("selectedSeatNumber", seatNumber);
		} else if (seatId != null) {
			list = backShuttleReservationSeatService.getBySeatId(seatId);
			model.addAttribute("selectedSeatId", seatId);
		}

		// 生成下拉式選單用的資料
		List<BackShuttleReservationSeat> allList = backShuttleReservationSeatService.getAllShuttleReservationSeat();

		List<Integer> reservationIdList = new ArrayList<>();
		List<Integer> seatIdList = new ArrayList<>();
		List<String> seatNumberList = new ArrayList<>();

		for (BackShuttleReservationSeat item : allList) {
			// 預約編號
			Integer resId = item.getShuttleReservationId();
			if (resId != null && !reservationIdList.contains(resId)) {
				reservationIdList.add(resId);
			}

			// 座位名稱
			Integer sId = item.getSeatId();
			if (sId != null && !seatIdList.contains(sId)) {
				seatIdList.add(sId);
			}

			// 座位號碼
			String seatNum = item.getSeat().getSeatNumber();
			if (seatNum != null && !seatNumberList.contains(seatNum)) {
				seatNumberList.add(seatNum);
			}
		}

		// 資料傳到前端
		model.addAttribute("reservationIdList", reservationIdList);
		model.addAttribute("seatIdList", seatIdList);
		model.addAttribute("seatNumberList", seatNumberList);
		model.addAttribute("shuttleReservationSeatList", list);

		return "front-end/backshuttlereservationseat/backshuttlereservationseat_getbackresseat";
	}

}
