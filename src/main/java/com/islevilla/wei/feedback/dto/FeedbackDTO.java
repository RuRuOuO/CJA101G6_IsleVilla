package com.islevilla.wei.feedback.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

public class FeedbackDTO {
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
    private MultipartFile fbImage;
    private Integer fbPublic;
    private Integer fbStatus;
}
