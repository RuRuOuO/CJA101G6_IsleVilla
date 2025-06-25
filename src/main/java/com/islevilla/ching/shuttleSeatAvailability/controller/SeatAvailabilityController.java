package com.islevilla.ching.shuttleSeatAvailability.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.islevilla.ching.shuttleSchedule.model.ShuttleService;
import com.islevilla.ching.shuttleSeatAvailability.model.SeatAvailability;
import com.islevilla.ching.shuttleSeatAvailability.model.SeatAvailability.ShuttleSeatAvailabilityId;
import com.islevilla.ching.shuttleSeatAvailability.model.SeatAvailabilityService;

@Controller
@RequestMapping("/seatavailability")
public class SeatAvailabilityController {

	private final SeatAvailabilityService seatAvailabilityService;
	private final ShuttleService shuttleService;

	public SeatAvailabilityController(SeatAvailabilityService seatAvailabilityService, ShuttleService shuttleService) {
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

	// 處理提交新增
	@PostMapping("/add")
	public String addSetAvailability(@ModelAttribute("seatavailability") SeatAvailability seatAvailability) {
		seatAvailabilityService.addSeatAvailability(seatAvailability);
		return "redirect:/seatavailability/list";
	}

	// 處理複合主鍵刪除（必須同時接收兩個路徑變數）
	@GetMapping("/delete/{scheduleId}/{date}")
	public String deleteSeatAvailability(@PathVariable("scheduleId") Integer scheduleId,
										 @PathVariable("date") String dateStr) {

		LocalDate date = LocalDate.parse(dateStr);
		seatAvailabilityService.deleteSeatAvailability(scheduleId,date);

		return "redirect:/seatavailability/list";
	}

	// 顯示編輯表單
	@GetMapping("/edit/{scheduleId}/{date}")
	public String showEditForm(@PathVariable("scheduleId") Integer scheduleId,
	                           @PathVariable("date") String dateStr,
	                           Model model) {
		LocalDate date = LocalDate.parse(dateStr);
		SeatAvailability seatAvailability = seatAvailabilityService.getSeatById(scheduleId, date);

		if (seatAvailability != null) {
			model.addAttribute("seatavailability", seatAvailability);
//			model.addAttribute("scheduleList", shuttleService.getAllShuttle()); // 雖然這裡用不到但也可保留
			return "front-end/seatavailability/seatavailability_edit";
		} else {
			return "redirect:/seatavailability/list";
		}
	}

	// 提交編輯表單
	@PostMapping("/edit")
	public String updateSeatAvailability(@ModelAttribute("seatavailability") SeatAvailability formInput) {
	    Integer scheduleId = formInput.getShuttleScheduleId();
	    LocalDate date = formInput.getShuttleDate();

	    SeatAvailability existing = seatAvailabilityService.getSeatById(scheduleId, date);

	    if (existing != null) {
	        existing.setSeatQuantity(formInput.getSeatQuantity());
	        seatAvailabilityService.updateSeatAvailability(existing);
	    }

	    return "redirect:/seatavailability/list";
	}
	
	//顯示查詢頁面
	@GetMapping("/getseat")
	public String showGetOneForm(@RequestParam(value = "date",required = false )
								 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date,
								 Model model){
		List<LocalDate> dates = seatAvailabilityService.getAllAvailableDates();
		model.addAttribute("availableDates",dates);
		
		if(date != null) {
			List<SeatAvailability> schedules = seatAvailabilityService.getAllSeatAvailability()
					.stream()
					.filter(sa -> sa.getShuttleDate().equals(date))
					.toList();
		model.addAttribute("availableDates", dates);
		model.addAttribute("scheduleForSelectedDate",schedules);
		model.addAttribute("selectedDate",date);
		}
		model.addAttribute("seat",null);
		return "front-end/seatavailability/seatavailability_getseat";
	}
	
	//處裡查詢頁面
	@GetMapping("/get/{scheduleId}/{date}")
	public String getSeatById(@PathVariable("scheduleId") Integer scheduleId,
							  @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,Model model) {
		//查詢seat的資料
		SeatAvailability.ShuttleSeatAvailabilityId id = new SeatAvailability.ShuttleSeatAvailabilityId(scheduleId, date);
		SeatAvailability seat = seatAvailabilityService.getSeatById(scheduleId, date);

		// 重新取得所有可選日期
		List<LocalDate> dates = seatAvailabilityService.getAllAvailableDates();
	    model.addAttribute("availableDates", dates);
	    
		//帶入該選擇日期的所有班次(選單顯示)
		List<SeatAvailability> schedules = seatAvailabilityService.getAllSeatAvailability()
	            .stream()
	            .filter(sa -> sa.getShuttleDate().equals(date))
	            .toList();
	    model.addAttribute("scheduleForSelectedDate", schedules);
		
	    // 傳回原本選擇的日期 顯示在getseat下拉選單上
	    model.addAttribute("selectedDate", date);
	    
	    // 傳回原本選擇的班次 顯示在getseat下拉選單上
	    model.addAttribute("selectedScheduleId", scheduleId);
	    
	    //帶入查詢結果
	    model.addAttribute("seat", seat);
	    
		return "front-end/seatavailability/seatavailability_getseat";
		}
	}
