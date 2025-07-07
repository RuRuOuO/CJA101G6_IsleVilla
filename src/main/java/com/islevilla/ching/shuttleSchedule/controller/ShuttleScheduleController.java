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
@RequestMapping("/backend/shuttle")
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
		model.addAttribute("sidebarActive", "shuttle-list");
		return "back-end/shuttle/shuttle_list";
	}

	// 顯示新增表單
	@GetMapping("/add")
	public String showAddForm(Model model) {
		model.addAttribute("shuttleSchedule", new ShuttleSchedule());
		model.addAttribute("formMode", "add");
		return "back-end/shuttle/shuttle_add";
	}

	// 處理新增
	@PostMapping("/add")
	public String addShuttle(@Valid @ModelAttribute("shuttleSchedule") ShuttleSchedule shuttleSchedule,
			BindingResult result, Model model) {

		model.addAttribute("formMode", "add");
		// 基本欄位格式驗證
		if (result.hasErrors()) {
			return "back-end/shuttle/shuttle_add";
		}

		// 驗證：抵達時間不能早於出發時間
		if (shuttleSchedule.getShuttleArrivalTime().isBefore(shuttleSchedule.getShuttleDepartureTime())) {
			result.reject("time.invalid", "抵達時間不得早於出發時間 !");
			model.addAttribute("formMode", "add");
			return "back-end/shuttle/shuttle_add";
		}
		if (shuttleSchedule.getShuttleArrivalTime().equals(shuttleSchedule.getShuttleDepartureTime())) {
			result.reject("time.equal", "出發與抵達時間不可相同 !");
			return "back-end/shuttle/shuttle_add";
		}

		// 驗證：是否已有相同時間、方向的班次存在
		boolean exists = shuttleService.existsSchedule(shuttleSchedule.getShuttleDirection(),
				shuttleSchedule.getShuttleDepartureTime(), shuttleSchedule.getShuttleArrivalTime());

		if (exists) {
			result.reject("duplicate.schedule", "已有相同的出發與抵達時間 !");
			return "back-end/shuttle/shuttle_add";
		}
		// 通過驗證後儲存
		shuttleService.addShuttle(shuttleSchedule);
		return "redirect:/backend/shuttle/list";
	}

	// 顯示編輯表單
	@GetMapping("/edit/{shuttleScheduleId}")
	public String showEditForm(@PathVariable("shuttleScheduleId") Integer shuttleScheduleId, Model model) {
		ShuttleSchedule shuttle = shuttleService.getShuttleById(shuttleScheduleId);
		if (shuttle == null) {
			return "redirect:/backend/shuttle/list";
		}
		model.addAttribute("shuttleSchedule", shuttle);
		model.addAttribute("formMode", "edit");
		return "back-end/shuttle/shuttle_add";
	}

	// 處理編輯
	@PostMapping("/edit")
	public String updateShuttle(@Valid @ModelAttribute("shuttleSchedule") ShuttleSchedule shuttleSchedule,
			BindingResult result, Model model) {

		model.addAttribute("formMode", "edit");

		if (result.hasErrors()) {
			return "back-end/shuttle/shuttle_add";
		}
		// 抵達時間不能早於出發時間
		if (shuttleSchedule.getShuttleArrivalTime().isBefore(shuttleSchedule.getShuttleDepartureTime())) {
			result.reject("time.invalid", "抵達時間不得早於出發時間 !");
			return "back-end/shuttle/shuttle_add";
		}

		if (shuttleSchedule.getShuttleArrivalTime().equals(shuttleSchedule.getShuttleDepartureTime())) {
			result.reject("time.equal", "出發時間與抵達時間不可相同 !");
			return "back-end/shuttle/shuttle_add";
		}

		// 檢查是否有「其他班次」使用相同時間
		boolean exists = shuttleService.existsScheduleExcludingSelf(shuttleSchedule.getShuttleDirection(),
				shuttleSchedule.getShuttleDepartureTime(), shuttleSchedule.getShuttleArrivalTime(),
				shuttleSchedule.getShuttleScheduleId());

		if (exists) {
			result.reject("duplicate.schedule", "已有其他班次使用相同的出發與抵達時間 !");
			return "back-end/shuttle/shuttle_add";
		}
		// 通過驗證後更新
		shuttleService.updateShuttle(shuttleSchedule);
		return "redirect:/backend/shuttle/list";
	}

	// 刪除班次
	@GetMapping("/delete/{shuttleScheduleId}")
	public String deleteShuttle(@PathVariable("shuttleScheduleId") Integer shuttleScheduleId) {
		shuttleService.deleteShuttle(shuttleScheduleId);
		return "redirect:/backend/shuttle/list";
	}

	// 顯示查詢頁面
	@GetMapping("/get")
	public String showGetOneForm(Model model) {
		model.addAttribute("allShuttleSchedules", shuttleService.getAllShuttle());
		model.addAttribute("shuttle", null); // 第一次載入沒有資料
		return "back-end/shuttle/shuttle_get";
	}

	// 查詢功能
	@GetMapping("/getshuttle")
	public String getShuttleById(@RequestParam(value = "departureId", required = false) Integer departureId,
			@RequestParam(value = "returnId", required = false) Integer returnId, Model model) {

		ShuttleSchedule departureShuttle = null;
		ShuttleSchedule returnShuttle = null;

		if (departureId != null) {
			departureShuttle = shuttleService.getShuttleById(departureId);
			if (departureShuttle == null) {
				model.addAttribute("errorMsg", "查無去程班次編號：" + departureId);
			}
		}

		if (returnId != null) {
			returnShuttle = shuttleService.getShuttleById(returnId);
			if (returnShuttle == null) {
				model.addAttribute("errorMsg", "查無回程班次編號：" + returnId);
			}
		}

		model.addAttribute("departureShuttles", shuttleService.getDepartureShuttles());
		model.addAttribute("returnShuttles", shuttleService.getReturnShuttles());

		model.addAttribute("departureShuttle", departureShuttle);
		model.addAttribute("returnShuttle", returnShuttle);
		model.addAttribute("selectedDepartureId", departureId);
		model.addAttribute("selectedReturnId", returnId);

		return "back-end/shuttle/shuttle_get";
	}
}