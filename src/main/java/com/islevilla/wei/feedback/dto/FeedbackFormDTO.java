package com.islevilla.wei.feedback.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FeedbackFormDTO {
    @NotNull
    private Integer roomReservationId; // 由前端傳入
    @NotNull
    private Integer fbOverallRating;
    @NotNull
    private Integer fbRecommend;
    @NotNull
    private Integer fbRevisit;
    @NotNull
    private Integer fbShuttleRating;
    @NotNull
    private Integer fbReceptionRating;
    @NotNull
    private Integer fbRoomRating;
    @NotNull
    private Integer fbFacilityRating;
    @NotNull
    private Integer fbEnvRating;
    @NotNull
    private Integer fbValueRating;
    @NotNull
    private Integer fbWebsiteRating;
    private String fbCompliment;
    private String fbSuggestion;
    private MultipartFile fbImage;
    @NotNull
    private Integer fbPublic;
}
