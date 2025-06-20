package com.islevilla.ching.shuttleSeatAvailability.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.islevilla.ching.shuttleSchedule.model.ShuttleService;
import com.islevilla.ching.shuttleSeatAvailability.model.SeatAvailability;
import com.islevilla.ching.shuttleSeatAvailability.model.SeatAvailabilityId;
import com.islevilla.ching.shuttleSeatAvailability.model.SeatAvailabilityService;

@Controller
@RequestMapping("/seatavailability")
public class SeatAvailabilityController {

	private final SeatAvailabilityService seatAvailabilityService;
	private final ShuttleService shuttleService;

	public SeatAvailabilityController(SeatAvailabilityService seatAvailabilityService,
									  ShuttleService shuttleService) {
		this.seatAvailabilityService = seatAvailabilityService;
		this.shuttleService = shuttleService;
	}

	// 顯示所有座位量
	@GetMapping("/list")
	public String listSeatAvailability(Model model) {
		List<SeatAvailability> list = seatAvailabilityService.getAllSeatAvailability();
		model.addAttribute("seatavailability", list);
		return "front-end/seatavailability/seatavailability_list";
	}

	// 顯示新增表單
	@GetMapping("/add")
	public String showAddForm(Model model) {
		model.addAttribute("seatavailability", new SeatAvailability());
		model.addAttribute("scheduleList", shuttleService.getAllShuttle());
		return "front-end/seatavailability/seatavailability_add";
	}

	//處理提交新增
	@PostMapping("/add")
	public String addSetAvailability(@ModelAttribute("seatavailability") SeatAvailability seatAvailability) {
		seatAvailabilityService.addSeatAvailability(seatAvailability);
		return "redirect:/seatavailability/list";
	}
	
	
	// 處理複合主鍵刪除（必須同時接收兩個路徑變數）
		@GetMapping("/delete/{scheduleId}/{date}")
		public String deleteSeatAvailability(
				@PathVariable("scheduleId") Integer scheduleId,
				@PathVariable("date") String dateStr) {

			LocalDate date = LocalDate.parse(dateStr);
			SeatAvailabilityId id = new SeatAvailabilityId(scheduleId, date);
			seatAvailabilityService.deleteSeatAvailability(id);

			return "redirect:/seatavailability/list";
		}
		
		// 顯示編輯表單
		@GetMapping("/edit/{scheduleId}/{date}")
		public String showEditForm(@PathVariable("scheduleId") Integer scheduleId,
								   @PathVariable("date") String dateStr,
								   Model model){
			LocalDate date = LocalDate.parse(dateStr);
			SeatAvailabilityId id = new SeatAvailabilityId(scheduleId , date);
			SeatAvailability seatAvailability = seatAvailabilityService.getSeatById(id);
			
			if (seatAvailability != null) {
		        model.addAttribute("seatavailability", seatAvailability);
		        model.addAttribute("scheduleList", shuttleService.getAllShuttle());
		        return "front-end/seatavailability/seatavailability_edit";
		    } else {
		        return "redirect:/seatavailability/list"; // 找不到資料導回列表
		    }
		}
		
		// 提交編輯表單
		@PostMapping("/edit")
		public String updateSeatAvailability(@ModelAttribute("seatavailability") SeatAvailability seatAvailability) {
			seatAvailabilityService.updateSeatAvailability(seatAvailability);
			return "redirect:/seatavailability/list";
		}
		
}
