package com.islevilla.ching.shuttleReservation.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.islevilla.ching.shuttleReservation.model.BackShuttleReservation;
import com.islevilla.ching.shuttleReservation.model.BackShuttleReservationService;

@Controller
@RequestMapping("/backend/reservation")
public class BackShuttleReservationController {

	private final BackShuttleReservationService backShuttleReservationService;
	
	public BackShuttleReservationController(BackShuttleReservationService backShuttleReservationService) {
		this.backShuttleReservationService = backShuttleReservationService;
	}
	
	//顯示所有預約
	@GetMapping("/list")
	public String listReservation(Model model) {
		List<BackShuttleReservation> list = backShuttleReservationService.getAllShuttleReservation();
		model.addAttribute("shuttleReservationList", list);
		return "back-end/shuttlereservation/backreservation_list";
	}
	
	//顯示查詢頁面
	@GetMapping("/getbackres")
	public String showReservationSelectPage(Model model) {
		model.addAttribute("allShuttleReservation", backShuttleReservationService.getAllShuttleReservation());
		model.addAttribute("backShuttleReservation",null); //第一次載入無資料
//		model.addAttribute("message", "請選擇預約編號進行查詢");
		return "back-end/shuttlereservation/backreservation_getbackres";
	}
	
	//接收查詢表單結果
	@GetMapping("/getbackres/get")
	public String getShuttleReservationById(
	        @RequestParam("shuttleReservationId") Integer shuttleReservationId,
	        Model model) {

	    BackShuttleReservation res = backShuttleReservationService.getShuttleReservationById(shuttleReservationId);

	    model.addAttribute("allShuttleReservation", backShuttleReservationService.getAllShuttleReservation());
	    model.addAttribute("backShuttleReservation", res); // 查詢到就顯示，沒查到就空白

	    return "back-end/shuttlereservation/backreservation_getbackres";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
