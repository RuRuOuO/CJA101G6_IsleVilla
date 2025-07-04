package com.islevilla.wei.feedback.dto;

import lombok.Data;

import java.util.Date;

@Data
public class FeedbackDetailDTO {
    private Integer fbId;
    private Integer roomReservationId;
    private Date fbCreatedAt;
    private Date fbUpdatedAt;
    private Integer fbOverallRating;
    private Integer fbRecommend;
    private Integer fbRevisit;
    private Integer fbShuttleRating;
    private Integer fbReceptionRating;
    private Integer fbRoomRating;
    private Integer fbFacilityRating;
    private Integer fbEnvRating;
    private Integer fbValueRating;
    private Integer fbWebsiteRating;
    private String fbCompliment;
    private String fbSuggestion;
    private String fbImageBase64; // base64 字串
    private Integer fbPublic;
    private Integer fbStatus;

    // 新增會員相關欄位
    private String customerName;     // 顧客姓名
    private String customerAvatar;   // 顧客頭像 (Base64)
    private Integer customerId;      // 顧客ID (選用)

}
