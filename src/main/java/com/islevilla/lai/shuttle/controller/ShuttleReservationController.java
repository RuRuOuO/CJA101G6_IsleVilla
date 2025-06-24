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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.islevilla.lai.members.model.Members;
import com.islevilla.lai.members.model.MembersService;
import com.islevilla.lai.shuttle.model.SeatService;
import com.islevilla.lai.shuttle.model.ShuttleRVFrontEndService;
import com.islevilla.lai.shuttle.model.ShuttleReservationSeatService;
import com.islevilla.lai.shuttle.model.ShuttleReservationService;
import com.islevilla.lai.shuttle.model.ShuttleScheduleService;
import com.islevilla.lai.shuttle.model.ShuttleScheduleWithAvailabilityDTO;
import com.islevilla.lai.shuttle.model.ShuttleSeatAvailabilityService;
import com.islevilla.lai.shuttle.model.TempShuttleRVRequestDTO;
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

        // 開發環境下顯示測試資料
//        if (isDevelopmentEnvironment()) {
//            model.addAttribute("testRoomData", getTestRoomData());
//        }
        
        return "front-end/shuttle/shuttle-reservation";
    }
    /**
     * 驗證會員和訂房資料 - 步驟1 -> 步驟2
     */
    @PostMapping("/shuttle/reservation/validate")
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
        System.out.println("- 接駁方向: " + reservationRequest.getShuttleDirection());

        // 檢查驗證錯誤
//        if (bindingResult.hasErrors()) {
//        	System.out.println(bindingResult.getFieldError().getDefaultMessage());
//        	System.out.println("抓蟲2");
//            model.addAttribute("reservationRequest", reservationRequest);
//            model.addAttribute("currentStep", 1);
//            try {
//                // 查詢會員的訂房記錄
//                List<RoomRVOrder> memberRoomReservations = roomRVOrderService.getRoomRVOrderByMember(member);
//                System.out.println(memberRoomReservations);
//                model.addAttribute("memberRoomReservations", memberRoomReservations);
//            } catch (Exception e) {
//                System.out.println("查詢會員訂房記錄時發生錯誤: " + e.getMessage());
//                model.addAttribute("memberRoomReservations", new ArrayList<>());
//            }
////            if (isDevelopmentEnvironment()) {
////                model.addAttribute("testRoomData", getTestRoomData());
////            }
//            return "front-end/shuttle/shuttle-reservation";
//        }
        
        if (bindingResult.hasErrors()) {
            System.out.println("表單驗證失敗:");
            bindingResult.getFieldErrors().forEach(error -> {
                System.out.println("- " + error.getField() + ": " + error.getDefaultMessage());
            });
            
            return prepareFormPageWithError(model, member, reservationRequest);
        }

        System.out.println("表單驗證通過！");

        try {
        	System.out.println("抓蟲3");
            // 驗證會員和訂房資料
            boolean isValid = shuttleRVFrontEndService.validateMemberAndRoomReservation(
                reservationRequest.getMemberId(), 
                reservationRequest.getRoomReservationId(),
                reservationRequest.getShuttleDate(),
                reservationRequest.getShuttleDirection()
            );
        	System.out.println("抓蟲4");

            if (!isValid) {
            	System.out.println("抓蟲5");
                model.addAttribute("error", "會員編號、訂房編號不匹配，或接駁日期不在住宿期間內，或訂房已取消");
                model.addAttribute("reservationRequest", reservationRequest);
                model.addAttribute("currentStep", 1);
//                if (isDevelopmentEnvironment()) {
//                    model.addAttribute("testRoomData", getTestRoomData());
//                }
                return "front-end/shuttle/shuttle-reservation";
            }

            // 儲存預約請求並取得ID
        	System.out.println("抓蟲5");
            Integer reservationRequestId = shuttleRVFrontEndService.saveReservationRequest(reservationRequest);
            
            // 查詢可用班次
            List<ShuttleScheduleWithAvailabilityDTO> availableSchedules = 
                shuttleRVFrontEndService.getAvailableSchedules(
                    reservationRequest.getShuttleDate(),
                    reservationRequest.getShuttleDirection()
                );

            if (availableSchedules.isEmpty()) {
            	System.out.println("抓蟲6");
                model.addAttribute("error", "該日期沒有可用的班次");
                model.addAttribute("reservationRequest", reservationRequest);
                model.addAttribute("currentStep", 1);
//                if (isDevelopmentEnvironment()) {
//                    model.addAttribute("testRoomData", getTestRoomData());
//                }
                return "front-end/shuttle/shuttle-reservation";
            }
        	System.out.println("抓蟲7");

            model.addAttribute("currentStep", 2);
            model.addAttribute("reservationRequestId", reservationRequestId);
            model.addAttribute("availableSchedules", availableSchedules);
            
        } catch (Exception e) {
        	System.out.println("抓蟲8");
            model.addAttribute("error", "系統錯誤：" + e.getMessage());
            model.addAttribute("reservationRequest", reservationRequest);
            model.addAttribute("currentStep", 1);
//            if (isDevelopmentEnvironment()) {
//                model.addAttribute("testRoomData", getTestRoomData());
//            }
            return "front-end/shuttle/shuttle-reservation";
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
