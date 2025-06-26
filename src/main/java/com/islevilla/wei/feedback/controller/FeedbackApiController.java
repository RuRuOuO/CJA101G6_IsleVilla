package com.islevilla.wei.feedback.controller;

import com.islevilla.lai.members.model.Members;
import com.islevilla.wei.feedback.dto.FeedbackDetailDTO;
import com.islevilla.wei.feedback.dto.FeedbackFormDTO;
import com.islevilla.wei.feedback.dto.RoomRVOrderDTO;
import com.islevilla.wei.feedback.model.Feedback;
import com.islevilla.wei.feedback.model.FeedbackService;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FeedbackApiController {
    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/feedbacks")
    public List<FeedbackDetailDTO> getAllFeedbacks() {
        List<Feedback> feedbackList = feedbackService.findAll();
        List<FeedbackDetailDTO> dtoList = new ArrayList<>();
        for (Feedback fb : feedbackList) {
            dtoList.add(convertToDTO(fb));
        }
        return dtoList;
    }

    @GetMapping("/member/orders")
    public List<RoomRVOrderDTO> getMemberOrders(HttpSession session) {
        Members loginMember = (Members) session.getAttribute("member");

        if (loginMember == null) {
            return List.of(); // 或回傳錯誤訊息
        }

        return feedbackService.getAvailableOrders(loginMember);
    }

    @PostMapping("/feedbacks")
    public ResponseEntity<?> submitFeedback(@ModelAttribute FeedbackFormDTO dto, HttpSession session) {
        Members loginMember = (Members) session.getAttribute("member");
        boolean success = feedbackService.saveFeedback(dto, loginMember);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().body("提交失敗");
    }

    // 使用ModelMapper
    // Entity(在這裡指Feedback)通常包含其他物件(如RoomRVOrder)
    // 轉換為DTO的話可以指傳輸需要的欄位、轉換資料格式、避免資料循環(如@OneToMany造成JSON無限迴圈)
    private FeedbackDetailDTO convertToDTO(Feedback fb) {
        FeedbackDetailDTO dto = modelMapper.map(fb, FeedbackDetailDTO.class);

        // 手動設定嵌套欄位（roomReservationId）
        if (fb.getRoomRVOrder() != null) {
            dto.setRoomReservationId(fb.getRoomRVOrder().getRoomReservationId());
        }

        // 手動設定圖片欄位
        if (fb.getFbImage() != null) {
            String base64Image = Base64.getEncoder().encodeToString(fb.getFbImage());
            dto.setFbImageBase64("data:image/jpeg;base64," + base64Image);
        }

        return dto;
    }
}
