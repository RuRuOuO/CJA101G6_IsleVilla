//package com.islevilla.patty.roompromotionprice.controller;
//
//import java.io.IOException;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.ui.ModelMap;
//import org.springframework.validation.BeanPropertyBindingResult;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.islevilla.patty.promotion.model.PromotionService;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.validation.Valid;
//
//@Controller
//@RequestMapping("/roomPromotionPrice")
//public class RoomPromotionPriceController {
//
//	@Autowired
//	RoomPromotionPriceService roomPromotionPriceSvc;
//
//	@Autowired
//	PromotionService promotionSvc;
//
//	/*
//	 * This method will serve as addEmp.html handler.
//	 */
//	@GetMapping("addRoomPromotionPrice")
//	public String addEmp(ModelMap model) {
//		RoomPromotionPrice roomPromotionPrice = new RoomPromotionPrice();
//		model.addAttribute("roomPromotionPrice", roomPromotionPrice);
//		return "back-end/roomPromotionPrice/addRoomPromotionPrice";
//	}
//
//	/*
//	 * This method will be called on addEmp.html form submission, handling POST request It also validates the user input
//	 */
//	@PostMapping("insert")
//	public String insert(@Valid RoomPromotionPrice roomPromotionPrice, BindingResult result, ModelMap model,
//			@RequestParam("upFiles") MultipartFile[] parts) throws IOException {
//
//		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
//		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
//		result = removeFieldError(roomPromotionPrice, result, "upFiles");
//
//		if (parts[0].isEmpty()) { // 使用者未選擇要上傳的圖片時
//			model.addAttribute("errorMessage", "員工照片: 請上傳照片");
//		} else {
//			for (MultipartFile multipartFile : parts) {
//				byte[] buf = multipartFile.getBytes();
//				roomPromotionPrice.setUpFiles(buf);
//			}
//		}
//		if (result.hasErrors() || parts[0].isEmpty()) {
//			return "back-end/roomPromotionPrice/addRoomPromotionPrice";
//		}
//		/*************************** 2.開始新增資料 *****************************************/
//		// EmpService empSvc = new EmpService();
//		roomPromotionPriceSvc.addRoomPromotionPrice(roomPromotionPrice);
//		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
//		List<RoomPromotionPrice> list = roomPromotionPriceSvc.getAll();
//		model.addAttribute("roomPromotionPriceListData", list); // for listAllEmp.html 第85行用
//		model.addAttribute("success", "- (新增成功)");
//		return "redirect:/roomPromotionPrice/listAllRoomPromotionPrice"; // 新增成功後重導至IndexController_inSpringBoot.java的第58行@GetMapping("/emp/listAllEmp")
//	}
//
//	/*
//	 * This method will be called on listAllEmp.html form submission, handling POST request
//	 */
//	@PostMapping("getOne_For_Update")
//	public String getOne_For_Update(@RequestParam("roomPromotionId") String roomPromotionId, ModelMap model) {
//		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
//		/*************************** 2.開始查詢資料 *****************************************/
//		// EmpService empSvc = new EmpService();
//		RoomPromotionId roomPromotionId = roomPromotionIdSvc.getOneRoomPromotionId(Integer.valueOf(roomPromotionId));
//
//		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
//		model.addAttribute("roomPromotionPrice", roomPromotionPrice);
//		return "back-end/roomPromotionPrice/update_roomPromotionPrice_input"; // 查詢完成後轉交update_emp_input.html
//	}
//
//	/*
//	 * This method will be called on update_emp_input.html form submission, handling POST request It also validates the user input
//	 */
//	@PostMapping("update")
//	public String update(@Valid RoomPromotionPrice roomPromotionPrice, BindingResult result, ModelMap model,
//			@RequestParam("upFiles") MultipartFile[] parts) throws IOException {
//
//		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
//		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
//		result = removeFieldError(roomPromotionPrice, result, "upFiles");
//
//		if (parts[0].isEmpty()) { // 使用者未選擇要上傳的新圖片時
//			// EmpService empSvc = new EmpService();
//			byte[] upFiles = roomPromotionPriceSvc.getOneRoomPromotionPrice(roomPromotionPrice.getRoomPromotionId()).getUpFiles();
//			roomPromotionPrice.setUpFiles(upFiles);
//		} else {
//			for (MultipartFile multipartFile : parts) {
//				byte[] upFiles = multipartFile.getBytes();
//				roomPromotionPrice.setUpFiles(upFiles);
//			}
//		}
//		if (result.hasErrors()) {
//			return "back-end/roomPromotionPrice/update_roomPromotionPrice_input";
//		}
//		/*************************** 2.開始修改資料 *****************************************/
//		// EmpService empSvc = new EmpService();
//		roomPromotionPriceSvc.updateRoomPromotionPrice(roomPromotionPrice);
//
//		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
//		model.addAttribute("success", "- (修改成功)");
//		roomPromotionPrice = roomPromotionPriceSvc.getOneRoomPromotionPrice(Integer.valueOf(roomPromotionPrice.getRoomPromotionId()));
//		model.addAttribute("roomPromotionPrice", roomPromotionPrice);
//		return "back-end/roomPromotionPrice/listOneRoomPromotionPrice"; // 修改成功後轉交listOneEmp.html
//	}
//
//	/*
//	 * This method will be called on listAllEmp.html form submission, handling POST request
//	 */
//	@PostMapping("delete")
//	public String delete(@RequestParam("roomPromotionId") String roomPromotionId, ModelMap model) {
//		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
//		/*************************** 2.開始刪除資料 *****************************************/
//		// EmpService empSvc = new EmpService();
//		roomPromotionPriceSvc.deleteRoomPromotionPrice(Integer.valueOf(roomPromotionId));
//		/*************************** 3.刪除完成,準備轉交(Send the Success view) **************/
//		List<RroomPromotionPrice> list = roomPromotionPriceSvc.getAll();
//		model.addAttribute("roomPromotionPriceListData", list); // for listAllEmp.html 第85行用
//		model.addAttribute("success", "- (刪除成功)");
//		return "back-end/roomPromotionPrice/listAllRroomPromotionPrice"; // 刪除完成後轉交listAllEmp.html
//	}
//
//	/*
//	 * 第一種作法 Method used to populate the List Data in view. 如 : 
//	 * <form:select path="deptno" id="deptno" items="${deptListData}" itemValue="deptno" itemLabel="dname" />
//	 */
//	@ModelAttribute("roomPromotionPriceListData")
//	protected List<RoomPromotionPrice> referenceListData() {
//		// DeptService deptSvc = new DeptService();
//		List<RoomPromotionPrice> list = roomPromotionPriceSvc.getAll();
//		return list;
//	}
//
//	/*
//	 * 【 第二種作法 】 Method used to populate the Map Data in view. 如 : 
//	 * <form:select path="deptno" id="deptno" items="${depMapData}" />
//	 */
//	@ModelAttribute("roomPromotionPriceMapData") //
//	protected Map<Integer, String> referenceMapData() {
//		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
//		map.put(10, "財務部");
//		map.put(20, "研發部");
//		map.put(30, "業務部");
//		map.put(40, "生管部");
//		return map;
//	}
//
//	// 去除BindingResult中某個欄位的FieldError紀錄
//	public BindingResult removeFieldError(RoomPromotionPrice roomPromotionPrice, BindingResult result, String removedFieldname) {
//		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
//				.filter(fieldname -> !fieldname.getField().equals(removedFieldname))
//				.collect(Collectors.toList());
//		result = new BeanPropertyBindingResult(roomPromotionPrice, "roomPromotionPrice");
//		for (FieldError fieldError : errorsListToKeep) {
//			result.addError(fieldError);
//		}
//		return result;
//	}
//	
//	/*
//	 * This method will be called on select_page.html form submission, handling POST request
//	 */
//	@PostMapping("listRoomPromotionPrice_ByCompositeQuery")
//	public String listAllEmp(HttpServletRequest req, Model model) {
//		Map<String, String[]> map = req.getParameterMap();
//		List<RoomPromotionPrice> list = roomPromotionPriceSvc.getAll(map);
//		model.addAttribute("roomPromotionPriceListData", list); // for listAllEmp.html 第85行用
//		return "back-end/roomPromotionPrice/listAllRoomPromotionPrice";
//	}
//
//}