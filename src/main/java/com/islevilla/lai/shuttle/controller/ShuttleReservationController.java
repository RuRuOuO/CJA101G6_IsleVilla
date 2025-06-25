package com.islevilla.lai.shuttle.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.islevilla.lai.members.model.Members;
import com.islevilla.lai.members.model.MembersService;
import com.islevilla.lai.shuttle.model.SeatDTO;
import com.islevilla.lai.shuttle.model.SeatService;
import com.islevilla.lai.shuttle.model.ShuttleRVFrontEndService;
import com.islevilla.lai.shuttle.model.ShuttleReservationSeatService;
import com.islevilla.lai.shuttle.model.ShuttleReservationService;
import com.islevilla.lai.shuttle.model.ShuttleScheduleService;
import com.islevilla.lai.shuttle.model.ShuttleScheduleWithAvailabilityDTO;
import com.islevilla.lai.shuttle.model.ShuttleSeatAvailabilityService;
import com.islevilla.lai.shuttle.model.TempShuttleRVRequestDTO;
import com.islevilla.lai.shuttle.model.TempShuttleRVSummaryDTO;
import com.islevilla.wei.room.model.RoomRVOrder;
import com.islevilla.wei.room.model.RoomRVOrderService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ShuttleReservationController {
	
	@Autowired
	private MembersService membersService;
	
	@Autowired
	private RoomRVOrderService roomRVOrderService;

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
	
	@Autowired
	private ShuttleRVFrontEndService shuttleRVFrontEndService;

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
	
	/**
     * 顯示預約首頁 - 步驟1：申請預約
     */
    @GetMapping("/shuttle/reservation")
    public String showReservationForm(HttpSession session, Model model) {
        // 檢查是否已登入
		Members member = (Members) session.getAttribute("member");
		if (member == null) {
			// 如果沒有登入，直接跳轉到登入頁面
			System.out.println("找不到登入紀錄，即將跳轉到登入頁面......");
			return "redirect:/member/login";
		}
		System.out.println("進入接駁預約頁面，會員ID: " + member.getMemberId());
		
		// 初始化表單物件
        TempShuttleRVRequestDTO reservationRequest = new TempShuttleRVRequestDTO();
        // 預設設置會員ID
        reservationRequest.setMemberId(member.getMemberId());
        
        model.addAttribute("reservationRequest", reservationRequest);
        model.addAttribute("currentStep", 1);
        
        try {
            // 查詢會員的訂房記錄
            List<RoomRVOrder> memberRoomReservations = roomRVOrderService.getRoomRVOrderByMember(member);
            System.out.println("找到 " + memberRoomReservations.size() + " 筆訂房記錄");
            model.addAttribute("memberRoomReservations", memberRoomReservations);
        } catch (Exception e) {
            System.out.println("查詢會員訂房記錄時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("memberRoomReservations", new ArrayList<>());
        }
        
        return "front-end/shuttle/shuttle-reservation";
    }
    
    /**
     * 步驟1 - 驗證會員和選擇訂房資料 -> 步驟2 - 選擇班次
     */
    @PostMapping("/shuttle/reservation/select-schedule")
    public String validateReservation(@Valid @ModelAttribute TempShuttleRVRequestDTO reservationRequest,
                                    BindingResult bindingResult,
                                    HttpSession session,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        
    	System.out.println("=== 開始處理接駁預約驗證 ===");
    	
        // 檢查登入狀態
        Members member = (Members) session.getAttribute("member");
        if (member == null) {
        	System.out.println("會員未登入，跳轉至登入頁面......");
            return "redirect:/member/login";
        }
        
        // 確保會員ID正確設置
        // 自動設置會員ID
        reservationRequest.setMemberId(member.getMemberId());
        System.out.println("會員ID: " + member.getMemberId());
        
        // 打印接收到的數據進行調試
        System.out.println("接收到的預約資料:");
        System.out.println("- 會員ID: " + reservationRequest.getMemberId());
        System.out.println("- 訂房編號: " + reservationRequest.getRoomReservationId());
        System.out.println("- 接駁日期: " + reservationRequest.getShuttleDate());
        System.out.println("- 接駁人數: " + reservationRequest.getShuttleNumber());
        System.out.println("- 接駁方向: " + (reservationRequest.getShuttleDirection()==0 ? "0: 去程" : "1: 回程"));

        if (bindingResult.hasErrors()) {
            System.out.println("表單驗證失敗:");
            bindingResult.getFieldErrors().forEach(error -> {
                System.out.println("- " + error.getField() + ": " + error.getDefaultMessage());
            });
            
            return prepareFormPageWithError(model, member, reservationRequest);
        }

        System.out.println("表單驗證通過！");

        try {
        	System.out.println("檢查點1");
            // 驗證會員和訂房資料
            boolean isValid = shuttleRVFrontEndService.validateMemberAndRoomReservation(
                reservationRequest.getMemberId(), 
                reservationRequest.getRoomReservationId(),
                reservationRequest.getShuttleDate(),
                reservationRequest.getShuttleDirection()
            );
        	System.out.println("檢查點2");

            if (!isValid) {
            	System.out.println("會員編號、訂房編號不匹配，或接駁日期不在住宿期間內，或訂房已取消");
                model.addAttribute("error", "會員編號、訂房編號不匹配，或接駁日期不在住宿期間內，或訂房已取消");
                model.addAttribute("reservationRequest", reservationRequest);
                model.addAttribute("currentStep", 1);
//                if (isDevelopmentEnvironment()) {
//                    model.addAttribute("testRoomData", getTestRoomData());
//                }
                return "front-end/shuttle/shuttle-reservation";
            }

            // 儲存預約請求並取得ID
            Integer reservationRequestId = shuttleRVFrontEndService.saveReservationRequest(reservationRequest);
            
            // 查詢可用班次
            List<ShuttleScheduleWithAvailabilityDTO> availableSchedules = 
                shuttleRVFrontEndService.getAvailableSchedules(
                    reservationRequest.getShuttleDate(),
                    reservationRequest.getShuttleDirection()
                );

            if (availableSchedules.isEmpty()) {
            	System.out.println("該日期沒有可用的班次");
                model.addAttribute("error", "該日期沒有可用的班次");
                model.addAttribute("reservationRequest", reservationRequest);
                model.addAttribute("currentStep", 1);
                return "front-end/shuttle/shuttle-reservation";
            }
        	System.out.println("檢查點3");

            model.addAttribute("currentStep", 2);
            model.addAttribute("reservationRequestId", reservationRequestId);
            model.addAttribute("availableSchedules", availableSchedules);
            
        } catch (Exception e) {
        	System.out.println("進入Exception");
            model.addAttribute("error", "系統錯誤：" + e.getMessage());
            model.addAttribute("reservationRequest", reservationRequest);
            model.addAttribute("currentStep", 1);
            return "front-end/shuttle/shuttle-reservation";
        }

        return "front-end/shuttle/shuttle-reservation";
    }
    
    /**
     * 步驟2 - 選擇班次 -> 步驟3 - 選擇座位
     */
    @PostMapping("/shuttle/reservation/select-seats")
    public String selectSchedule(@RequestParam Integer reservationRequestId,
                               @RequestParam Integer selectedScheduleId,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        // 檢查登入狀態
        Members member = (Members) session.getAttribute("member");
        if (member == null) {
            return "redirect:/member/login";
        }
        
        try {
            // 取得預約請求資訊
            TempShuttleRVRequestDTO reservationRequest = shuttleRVFrontEndService.getReservationRequest(reservationRequestId);
            
            System.out.println("接收到的預約資料:");
            System.out.println("- 會員ID: " + reservationRequest.getMemberId());
            System.out.println("- 訂房編號: " + reservationRequest.getRoomReservationId());
            System.out.println("- 接駁日期: " + reservationRequest.getShuttleDate());
            System.out.println("- 接駁人數: " + reservationRequest.getShuttleNumber());
            System.out.println("- 接駁方向: " + (reservationRequest.getShuttleDirection()==0 ? "0: 去程" : "1: 回程"));
            System.out.println("- 接駁班次: " + selectedScheduleId);
            
            // 驗證預約請求是否屬於當前會員
            if (!reservationRequest.getMemberId().equals(member.getMemberId())) {
                redirectAttributes.addFlashAttribute("error", "無效的預約請求");
                return "redirect:/shuttle/reservation";
            }

            // 更新預約請求的班次資訊
            shuttleRVFrontEndService.updateScheduleSelection(reservationRequestId, selectedScheduleId);
            
            // 查詢座位資訊
            List<SeatDTO> seats = shuttleRVFrontEndService.getSeatsWithAvailability(
                selectedScheduleId, 
                reservationRequest.getShuttleDate()
            );

            model.addAttribute("currentStep", 3);
            model.addAttribute("reservationRequestId", reservationRequestId);
            model.addAttribute("seats", seats);
            model.addAttribute("requiredSeats", reservationRequest.getShuttleNumber());
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "選擇班次時發生錯誤：" + e.getMessage());
            return "redirect:/shuttle/reservation";
        }

        return "front-end/shuttle/shuttle-reservation";
    }
    
    /**
     * 步驟3 - 選擇座位 -> 步驟4 - 確認預約資訊
     */
    @PostMapping("/shuttle/reservation/check-details")
    public String selectSeats(@RequestParam Integer reservationRequestId,
                            @RequestParam List<Integer> selectedSeatIds,
                            HttpSession session,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        // 檢查登入狀態
        Members member = (Members) session.getAttribute("member");
        if (member == null) {
            return "redirect:/member/login";
        }

        try {
            // 取得預約請求資訊
            TempShuttleRVRequestDTO reservationRequest = shuttleRVFrontEndService.getReservationRequest(reservationRequestId);
            
            System.out.println("接收到的預約資料:");
            System.out.println("- 會員ID: " + reservationRequest.getMemberId());
            System.out.println("- 訂房編號: " + reservationRequest.getRoomReservationId());
            System.out.println("- 接駁日期: " + reservationRequest.getShuttleDate());
            System.out.println("- 接駁人數: " + reservationRequest.getShuttleNumber());
            System.out.println("- 接駁方向: " + (reservationRequest.getShuttleDirection()==0 ? "0: 去程" : "1: 回程"));
            System.out.println("- 接駁班次: " + reservationRequest.getSelectedScheduleId());
            
            // 驗證預約請求是否屬於當前會員
            if (!reservationRequest.getMemberId().equals(member.getMemberId())) {
                redirectAttributes.addFlashAttribute("error", "無效的預約請求");
                return "redirect:/shuttle/reservation";
            }
            
            // 驗證選擇的座位數量
            if (selectedSeatIds.size() != reservationRequest.getShuttleNumber()) {
                redirectAttributes.addFlashAttribute("error", 
                    "選擇的座位數量不正確，需要選擇 " + reservationRequest.getShuttleNumber() + " 個座位");
                return "redirect:/shuttle/reservation/select-seats?id=" + reservationRequestId;
            }

            // 儲存座位選擇
            shuttleRVFrontEndService.updateSeatSelection(reservationRequestId, selectedSeatIds);
            
            // 取得預約摘要資訊
            TempShuttleRVSummaryDTO reservationSummary = shuttleRVFrontEndService.getReservationSummary(reservationRequestId);

            model.addAttribute("currentStep", 4);
            model.addAttribute("reservationRequestId", reservationRequestId);
            model.addAttribute("reservationSummary", reservationSummary);
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "選擇座位時發生錯誤：" + e.getMessage());
            return "redirect:/shuttle/reservation/select-seats?id=" + reservationRequestId;
        }

        return "front-end/shuttle/shuttle-reservation";
    }
    
    /**
     * 步驟4 - 確認預約資訊 -> 完成預約流程
     */
    @PostMapping("/shuttle/reservation/submit")
    public String submitReservation(@RequestParam Integer reservationRequestId,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            // 建立正式預約
            Integer shuttleReservationId = shuttleRVFrontEndService.createReservation(reservationRequestId);
            
            model.addAttribute("reservationSuccess", true);
            model.addAttribute("currentStep", 5);
            model.addAttribute("newShuttleReservationId", shuttleReservationId);
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "提交預約時發生錯誤：" + e.getMessage());
            return "redirect:/shuttle/reservation/submit";
        }

        return "/front-end/shuttle/shuttle-reservation";
    }

    /**
     * 回到步驟2 - 重新選擇班次
     */
    @GetMapping("/shuttle/reservation/select-schedule")
    public String backToStep2(@RequestParam("id") Integer reservationRequestId, Model model) {
        try {
            TempShuttleRVRequestDTO reservationRequest = shuttleRVFrontEndService.getReservationRequest(reservationRequestId);
            
            List<ShuttleScheduleWithAvailabilityDTO> availableSchedules = 
                shuttleRVFrontEndService.getAvailableSchedules(
                    reservationRequest.getShuttleDate(),
                    reservationRequest.getShuttleDirection()
                );

            model.addAttribute("currentStep", 2);
            model.addAttribute("reservationRequestId", reservationRequestId);
            model.addAttribute("availableSchedules", availableSchedules);
            
        } catch (Exception e) {
            model.addAttribute("error", "載入班次資訊時發生錯誤：" + e.getMessage());
            return "redirect:/shuttle/reservation";
        }

        return "front-end/shuttle/shuttle-reservation";
    }

    /**
     * 回到步驟3 - 重新選擇座位
     */
    @GetMapping("/shuttle/reservation/select-seat")
    public String backToStep3(@RequestParam("id") Integer reservationRequestId, Model model) {
        try {
        	TempShuttleRVRequestDTO reservationRequest = shuttleRVFrontEndService.getReservationRequest(reservationRequestId);
            
            List<SeatDTO> seats = shuttleRVFrontEndService.getSeatsWithAvailability(
                reservationRequest.getSelectedScheduleId(), 
                reservationRequest.getShuttleDate()
            );

            model.addAttribute("currentStep", 3);
            model.addAttribute("reservationRequestId", reservationRequestId);
            model.addAttribute("seats", seats);
            model.addAttribute("requiredSeats", reservationRequest.getShuttleNumber());
            
        } catch (Exception e) {
            model.addAttribute("error", "載入座位資訊時發生錯誤：" + e.getMessage());
            return "redirect:/shuttle/reservation";
        }

        return "front-end/shuttle/shuttle-reservation";
    }

    /**
     * 回到步驟4 - 重新確認預約
     */
    @GetMapping("/shuttle/reservation/check-detail")
    public String backToStep4(@RequestParam("id") Integer reservationRequestId, Model model) {
        try {
            TempShuttleRVSummaryDTO reservationSummary = shuttleRVFrontEndService.getReservationSummary(reservationRequestId);

            model.addAttribute("currentStep", 4);
            model.addAttribute("reservationRequestId", reservationRequestId);
            model.addAttribute("reservationSummary", reservationSummary);
            
        } catch (Exception e) {
            model.addAttribute("error", "載入預約摘要時發生錯誤：" + e.getMessage());
            return "redirect:/shuttle/reservation";
        }

        return "front-end/shuttle/shuttle-reservation";
    }
    
    /**
     * 準備錯誤頁面的輔助方法
     */
    private String prepareFormPageWithError(Model model, Members member, TempShuttleRVRequestDTO reservationRequest) {
        model.addAttribute("reservationRequest", reservationRequest);
        model.addAttribute("currentStep", 1);
        
        try {
            List<RoomRVOrder> memberRoomReservations = roomRVOrderService.getRoomRVOrderByMember(member);
            model.addAttribute("memberRoomReservations", memberRoomReservations);
        } catch (Exception e) {
            System.out.println("載入會員訂房記錄時發生錯誤: " + e.getMessage());
            model.addAttribute("memberRoomReservations", new ArrayList<>());
        }
        
        return "front-end/shuttle/shuttle-reservation";
    }
	
}
