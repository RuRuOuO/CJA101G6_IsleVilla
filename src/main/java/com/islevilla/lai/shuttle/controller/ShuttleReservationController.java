package com.islevilla.lai.shuttle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.islevilla.lai.members.model.Members;
import com.islevilla.lai.members.model.MembersService;
import com.islevilla.lai.shuttle.model.SeatService;
import com.islevilla.lai.shuttle.model.ShuttleReservationSeatService;
import com.islevilla.lai.shuttle.model.ShuttleReservationService;
import com.islevilla.lai.shuttle.model.ShuttleScheduleService;
import com.islevilla.lai.shuttle.model.ShuttleSeatAvailabilityService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ShuttleReservationController {
	
	@Autowired
	private MembersService membersService;

	@Autowired
	private SeatService seatService;

	@Autowired
	private ShuttleReservationService shuttleReservationService;

	@Autowired
	private ShuttleReservationSeatService shuttleReservationSeatService;

	@Autowired
	private ShuttleScheduleService shuttleScheduleService;

	@Autowired
	private ShuttleSeatAvailabilityService shuttleSeatAvailabilityService;

//	// 顯示登入頁面
//	@GetMapping("/shuttle/reservation")
//	public String showShuttleReservationPage(HttpSession session, Model model) {
//		// 檢查是否已登入
//		Members member = (Members) session.getAttribute("member");
//		if (member == null) {
//			// 如果沒有登入，直接跳轉到登入頁面
//			System.out.println("找不到登入紀錄，即將跳轉到登入頁面......");
//			return "redirect:/member/login";
//		}
//		System.out.println("進入接駁預約頁面");
//		// 將會員資訊傳給頁面顯示
//		model.addAttribute("member", member);
//		return "front-end/shuttle/shuttle-reservation";
//	}
	
	
	
	
	
}
