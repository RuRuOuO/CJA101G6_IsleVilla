package com.islevilla.wei.feedback.controller;

import com.islevilla.jay.coupon.model.Coupon;
import com.islevilla.jay.coupon.model.CouponService;
import com.islevilla.lai.members.model.Members;
import com.islevilla.wei.feedback.dto.FeedbackDetailDTO;
import com.islevilla.wei.feedback.dto.FeedbackFormDTO;
import com.islevilla.wei.feedback.dto.RoomRVOrderDTO;
import com.islevilla.wei.feedback.model.Feedback;
import com.islevilla.wei.feedback.model.FeedbackEmailService;
import com.islevilla.wei.feedback.model.FeedbackService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CouponService couponService;

    @Autowired
    private FeedbackEmailService feedbackEmailService;

    // ========== API ==========//
    @GetMapping("/api/feedbacks")
    @ResponseBody
    public List<FeedbackDetailDTO> getAllFeedbacks() {
        List<Feedback> feedbackList = feedbackService.findAll();
        List<FeedbackDetailDTO> dtoList = new ArrayList<>();
        for (Feedback fb : feedbackList) {
            dtoList.add(convertToDTO(fb));
        }
        return dtoList;
    }

    @GetMapping("/api/member/room-reservations")
    @ResponseBody
    public List<RoomRVOrderDTO> getMemberOrders(HttpSession session) {
        Members loginMember = (Members) session.getAttribute("member");

        if (loginMember == null) {
            return List.of(); // 或回傳錯誤訊息
        }

        return feedbackService.getAvailableOrders(loginMember);
    }

    @PostMapping("/api/feedbacks")
    @ResponseBody
    public ResponseEntity<?> submitFeedback(@ModelAttribute FeedbackFormDTO dto, HttpSession session) {
        Members loginMember = (Members) session.getAttribute("member");
        boolean success = feedbackService.saveFeedback(dto, loginMember);

        if (success && loginMember != null) {
            // 查詢效期內的 coupon
            List<Coupon> validCoupons = couponService.getAll().stream()
                .filter(c -> c.getCouponStatus() == 1
                    && !c.getStartDate().isAfter(java.time.LocalDate.now())
                    && !c.getEndDate().isBefore(java.time.LocalDate.now()))
                .toList();

            try {
                feedbackEmailService.sendThankYouEmail(
                    loginMember.getMemberEmail(),
                    loginMember.getMemberName(),
                    validCoupons
                );
            } catch (MessagingException e) {
                e.printStackTrace();
                // 可以選擇記錄錯誤，但不影響主流程
            }
        }

        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().body("提交失敗");
    }

    @GetMapping("/api/feedbacks/public")
    @ResponseBody
    public List<FeedbackDetailDTO> getPublicFeedbacks() {
        List<Feedback> feedbackList = feedbackService.findPublicAndActiveFeedbacks();
        return feedbackList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/api/feedbacks/{id}/status")
    @ResponseBody
    public ResponseEntity<String> updateStatus(@PathVariable Integer id, @RequestBody Map<String, Integer> body) {
        Optional<Feedback> optionalFeedback = feedbackService.findById(id);

        if (optionalFeedback.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到指定的評價");
        }

        Feedback feedback = optionalFeedback.get();

        Integer status = body.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return ResponseEntity.badRequest().body("無效的狀態值");
        }

        feedback.setFbStatus(status);
        feedbackService.save(feedback);
        return ResponseEntity.ok("評價狀態已更新為：" + (status == 1 ? "上架" : "下架"));
    }

    // ========== 路由 ========== //
    // 前台顯示全部feedback
    @GetMapping("/feedback/list")
    public String feedbackList() {
        return "front-end/feedback/listAllFeedback";
    }

    // 前台會員頁面顯示表單
    @GetMapping("/member/feedback/list")
    public String memberFeedbackList(HttpSession session) {
        Members loginMember = (Members) session.getAttribute("member");
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        return "front-end/member/member-feedback-list";
    }

    // 後台顯示全部feedback (thymeleaf版)
    @GetMapping("/backend/thymeleaf/feedback/list")
    public String backThyleafFeedbackList(Model model) {
        List<Feedback> feedbackList = feedbackService.findAll();
        model.addAttribute("feedbackList", feedbackList);
        return "back-end/feedback/listAllFeedback_Thymeleaf";
    }

    // 後台顯示全部feedback
    @GetMapping("/backend/feedback/list")
    public String backFeedbackList(Model model) {
        model.addAttribute("sidebarActive", "feeback-list");
        return "back-end/feedback/listAllFeedback";
    }

    // ========== 輔助方法 ========== //
    // 使用ModelMapper
    // Entity(在這裡指Feedback)通常包含其他物件(如RoomRVOrder)
    // 轉換為DTO的話可以指傳輸需要的欄位、轉換資料格式、避免資料循環(如@OneToMany造成JSON無限迴圈)
    private FeedbackDetailDTO convertToDTO(Feedback fb) {
        FeedbackDetailDTO dto = modelMapper.map(fb, FeedbackDetailDTO.class);

        // 手動設定嵌套欄位（roomReservationId）
        if (fb.getRoomRVOrder() != null) {
            dto.setRoomReservationId(fb.getRoomRVOrder().getRoomReservationId());

            // 獲取會員資料
            Members member = fb.getRoomRVOrder().getMembers();
            if (member != null) {
                dto.setCustomerName(member.getMemberName());

                // 設定會員頭像
                if (member.getMemberPhoto() != null) {
                    String base64Avatar = Base64.getEncoder().encodeToString(member.getMemberPhoto());
                    dto.setCustomerAvatar("data:image/jpeg;base64," + base64Avatar);
                }
            }
        }

        // 手動設定圖片欄位
        if (fb.getFbImage() != null) {
            String base64Image = Base64.getEncoder().encodeToString(fb.getFbImage());
            dto.setFbImageBase64("data:image/jpeg;base64," + base64Image);
        }
        return dto;
    }
}