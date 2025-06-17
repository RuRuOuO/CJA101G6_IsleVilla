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
@RequestMapping("/shuttleSchedule")
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
		if (result.hasErrors()) {
			model.addAttribute("formMode", "add");
			return "front-end/shuttle/shuttle_add";
		}
		shuttleService.addShuttle(shuttleSchedule);
		return "redirect:/shuttleSchedule/list";
	}

	// 顯示編輯表單
	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable("id") Integer id, Model model) {
		ShuttleSchedule shuttle = shuttleService.getShuttleById(id);
		if (shuttle == null) {
			return "redirect:/shuttleSchedule/list";
		}
		model.addAttribute("shuttleSchedule", shuttle);
		model.addAttribute("formMode", "edit");
		return "front-end/shuttle/shuttle_add";
	}

	// 處理編輯
	@PostMapping("/edit")
	public String updateShuttle(@Valid @ModelAttribute("shuttleSchedule") ShuttleSchedule shuttleSchedule,
			BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("formMode", "edit");
			return "front-end/shuttle/shuttle_add";
		}
		shuttleService.updateShuttle(shuttleSchedule);
		return "redirect:/shuttleSchedule/list";
	}

	// 刪除班次
	@GetMapping("/delete/{id}")
	public String deleteShuttle(@PathVariable("id") Integer id) {
		shuttleService.deleteShuttle(id);
		return "redirect:/shuttleSchedule/list";
	}

	//顯示查詢頁面
	@GetMapping("/getShuttle")
	public String showGetOneForm(Model model) {
		model.addAttribute("shuttle", null); // 第一次載入沒有資料
		return "front-end/shuttle/shuttle_getShuttle";
	}

	//處裡查詢頁面
	@GetMapping("/get/{id}")
	public String getShuttleById(@PathVariable("id") Integer id, Model model) {
		ShuttleSchedule shuttle = shuttleService.getShuttleById(id);
		if (shuttle == null) {
			model.addAttribute("errorMsg", "查無此班次資料");
		}
		model.addAttribute("shuttle", shuttle);
		return "front-end/shuttle/shuttle_getShuttle";
	}

}
