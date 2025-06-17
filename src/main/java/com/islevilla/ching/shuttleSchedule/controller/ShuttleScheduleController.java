package com.islevilla.ching.shuttleSchedule.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.islevilla.ching.shuttleSchedule.model.ShuttleSchedule;
import com.islevilla.ching.shuttleSchedule.model.ShuttleService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/shuttle")
public class ShuttleScheduleController {

	private final ShuttleService shuttleService;

	public ShuttleScheduleController(ShuttleService shuttleService) {
		this.shuttleService = shuttleService;
	}

	// 顯示所有班次
	@GetMapping("/list")
	public String listShuttles(Model model) {
		List<ShuttleSchedule> list = shuttleService.getAllShuttle();
		model.addAttribute("shuttleList", list);
		return "front-end/shuttle/shuttle_list";
	}

	// 顯示新增表單
	@GetMapping("/add")
	public String showAddForm(Model model) {
		model.addAttribute("shuttleSchedule", new ShuttleSchedule());
		model.addAttribute("formMode", "add");
		return "front-end/shuttle/shuttle_add";
	}

	// 處理新增
	@PostMapping("/add")
	public String addShuttle(@Valid @ModelAttribute("shuttleSchedule") ShuttleSchedule shuttleSchedule,
			BindingResult result, Model model) {
		
		model.addAttribute("formMode", "add");
		// 基本欄位格式驗證
		if (result.hasErrors()) {
			return "front-end/shuttle/shuttle_add";
		}
		
		// 驗證：抵達時間不能早於出發時間
		if (shuttleSchedule.getArrivalTime().isBefore(shuttleSchedule.getDepartureTime())) {
			result.reject("time.invalid", "抵達時間不得早於出發時間 !");
			model.addAttribute("formMode", "add");
			return "front-end/shuttle/shuttle_add";
		}
		if (shuttleSchedule.getArrivalTime().equals(shuttleSchedule.getDepartureTime())) {
	        result.reject("time.equal", "出發與抵達時間不可相同 !");
	        return "front-end/shuttle/shuttle_add";
	    }
		
		// 驗證：是否已有相同時間、方向的班次存在
		boolean exists = shuttleService.existsSchedule(shuttleSchedule.getDirection(),
				shuttleSchedule.getDepartureTime(), shuttleSchedule.getArrivalTime());

		if (exists) {
			result.reject("duplicate.schedule", "已有相同的出發與抵達時間 !");
			return "front-end/shuttle/shuttle_add";
		}
		// 通過驗證後儲存
		shuttleService.addShuttle(shuttleSchedule);
		return "redirect:/shuttle/list";
	}

	// 顯示編輯表單
	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable("id") Integer id, Model model) {
		ShuttleSchedule shuttle = shuttleService.getShuttleById(id);
		if (shuttle == null) {
			return "redirect:/shuttle/list";
		}
		model.addAttribute("shuttleSchedule", shuttle);
		model.addAttribute("formMode", "edit");
		return "front-end/shuttle/shuttle_add";
	}

	// 處理編輯
	@PostMapping("/edit")
	public String updateShuttle(@Valid @ModelAttribute("shuttleSchedule") ShuttleSchedule shuttleSchedule,
			BindingResult result, Model model) {
		
		model.addAttribute("formMode", "edit");
		
		if (result.hasErrors()) {
			return "front-end/shuttle/shuttle_add";
		}
		// 抵達時間不能早於出發時間
		if (shuttleSchedule.getArrivalTime().isBefore(shuttleSchedule.getDepartureTime())) {
			result.reject("time.invalid", "抵達時間不得早於出發時間 !");
			return "front-end/shuttle/shuttle_add";
		}
		
		if (shuttleSchedule.getArrivalTime().equals(shuttleSchedule.getDepartureTime())) {
		    result.reject("time.equal", "出發時間與抵達時間不可相同 !");
		    return "front-end/shuttle/shuttle_add";
		}

		// 檢查是否有「其他班次」使用相同時間
		boolean exists = shuttleService.existsScheduleExcludingSelf(shuttleSchedule.getDirection(),
				shuttleSchedule.getDepartureTime(), shuttleSchedule.getArrivalTime(), shuttleSchedule.getId());

		if (exists) {
			result.reject("duplicate.schedule", "已有其他班次使用相同的出發與抵達時間 !");
			return "front-end/shuttle/shuttle_add";
		}
		// 通過驗證後更新
		shuttleService.updateShuttle(shuttleSchedule);
		return "redirect:/shuttle/list";
	}

	// 刪除班次
	@GetMapping("/delete/{id}")
	public String deleteShuttle(@PathVariable("id") Integer id) {
		shuttleService.deleteShuttle(id);
		return "redirect:/shuttle/list";
	}

	// 顯示查詢頁面
	@GetMapping("/getShuttle")
	public String showGetOneForm(Model model) {
		model.addAttribute("shuttle", null); // 第一次載入沒有資料
		return "front-end/shuttle/shuttle_getShuttle";
	}

	// 處裡查詢頁面
	@GetMapping("/get/{id}")
	public String getShuttleById(@PathVariable("id") Integer id, Model model) {
		ShuttleSchedule shuttle = shuttleService.getShuttleById(id);
		if (shuttle == null) {
			model.addAttribute("errorMsg", "查無此班次資料");
			return "front-end/shuttle/shuttle_getShuttle";
		}
		model.addAttribute("shuttle", shuttle);
		return "front-end/shuttle/shuttle_getShuttle";
	}

}
