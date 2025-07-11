package com.islevilla.ching.shuttleSeat.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.islevilla.ching.shuttleSeat.model.BackSeat;
import com.islevilla.ching.shuttleSeat.model.BackSeatService;

@Controller
@RequestMapping("/backend/backseat")
public class BackSeatController {
	
	private final BackSeatService backSeatService;
	
	public BackSeatController(BackSeatService backSeatService) {
		this.backSeatService = backSeatService;
	}
	
	//顯示所有座位
	@GetMapping("/list")
	public String BackSeatList(Model model) {
		List<BackSeat> list = backSeatService.getAllSeat();
		model.addAttribute("backSeatList", list);
		model.addAttribute("sidebarActive", "backseat-list");
		return "back-end/backseat/backseat_list";
	}

}
