package com.islevilla.wei.feedback.controller;

import com.islevilla.wei.feedback.dto.FeedbackDetailDTO;
import com.islevilla.wei.feedback.model.Feedback;
import com.islevilla.wei.feedback.model.FeedbackService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackApiController {
    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public List<FeedbackDetailDTO> getAllFeedbacks() {
        List<Feedback> feedbackList = feedbackService.findAll();
        List<FeedbackDetailDTO> dtoList = new ArrayList<>();
        for (Feedback fb : feedbackList) {
            dtoList.add(convertToDTO(fb));
        }
        return dtoList;
    }

    // 使用ModelMapper
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

//    // 全手動的方法
//    // Entity(在這裡指Feedback)通常包含其他物件(如RoomRVOrder)
//    // 轉換為DTO的話可以指傳輸需要的欄位、轉換資料格式、避免資料循環(如@OneToMany造成JSON無限迴圈)
//    private FeedbackDetailDTO convertToDTO(Feedback fb) {
//        FeedbackDetailDTO dto = new FeedbackDetailDTO();
//        dto.setFbId(fb.getFbId());
//        dto.setRoomReservationId(fb.getRoomRVOrder().getRoomReservationId());
//        dto.setFbCreatedAt(fb.getFbCreatedAt());
//        dto.setFbUpdatedAt(fb.getFbUpdatedAt());
//        dto.setFbOverallRating(fb.getFbOverallRating());
//        dto.setFbRecommend(fb.getFbRecommend());
//        dto.setFbRevisit(fb.getFbRevisit());
//        dto.setFbShuttleRating(fb.getFbShuttleRating());
//        dto.setFbReceptionRating(fb.getFbReceptionRating());
//        dto.setFbRoomRating(fb.getFbRoomRating());
//        dto.setFbFacilityRating(fb.getFbFacilityRating());
//        dto.setFbEnvRating(fb.getFbEnvRating());
//        dto.setFbValueRating(fb.getFbValueRating());
//        dto.setFbWebsiteRating(fb.getFbWebsiteRating());
//        dto.setFbCompliment(fb.getFbCompliment());
//        dto.setFbSuggestion(fb.getFbSuggestion());
//        dto.setFbPublic(fb.getFbPublic());
//        dto.setFbStatus(fb.getFbStatus());
//
//        // 圖片轉 base64 字串（若存在）
//        if (fb.getFbImage() != null) {
//            String base64Image = Base64.getEncoder().encodeToString(fb.getFbImage());
//            dto.setFbImageBase64("data:image/jpeg;base64," + base64Image); // 或判斷是 png/jpeg
//        }
//        return dto;
//    }
}
