package com.islevilla.wei.feedback.model;

import com.islevilla.wei.room.model.RoomRVOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "feedback")
@Data
public class Feedback {
    @Column(name = "fb_id") // 對應欄位
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 此欄位為auto increment
    @Id
    private Integer fbId;

    @OneToOne // 一個問卷對應一張訂房訂單
    @JoinColumn(name = "room_reservation_id", nullable = false) // 參照的欄位
    @NotNull(message = "訂單不得為空")
    private RoomRVOrder roomRVOrder; // 訂單物件

    @Column(name = "fb_created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "請輸入填寫時間")
    private Date fbCreatedAt;

    @Column(name = "fb_updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "請輸入最後更新時間")
    private Date fbUpdatedAt;

    @Column(name = "fb_overall_rating")
    @NotNull(message = "請選擇綜合評價")
    private Integer fbOverallRating;

    @Column(name = "fb_recommend")
    @NotNull(message = "請選擇是否推薦")
    private Integer fbRecommend;

    @Column(name = "fb_revisit")
    @NotNull(message = "請選擇再次光臨意願")
    private Integer fbRevisit;

    @Column(name = "fb_shuttle_rating")
    @NotNull(message = "請選擇接駁評價")
    private Integer fbShuttleRating;

    @Column(name = "fb_reception_rating")
    @NotNull(message = "請選擇櫃台評價")
    private Integer fbReceptionRating;

    @Column(name = "fb_room_rating")
    @NotNull(message = "請選擇房間評價")
    private Integer fbRoomRating;

    @Column(name = "fb_facility_rating")
    @NotNull(message = "請選擇設施評價")
    private Integer fbFacilityRating;

    @Column(name = "fb_env_rating")
    @NotNull(message = "請選擇環境評價")
    private Integer fbEnvRating;

    @Column(name = "fb_value_rating")
    @NotNull(message = "請選擇性價比評價")
    private Integer fbValueRating;

    @Column(name = "fb_website_rating")
    @NotNull(message = "請選擇網站使用評價")
    private Integer fbWebsiteRating;

    @Column(name = "fb_compliment")
    private String fbCompliment;

    @Column(name = "fb_suggestion")
    private String fbSuggestion;

    @Lob // 更明確標示為二進位大物件
    @Column(name = "fb_image")
    private byte[] fbImage;

    @Column(name = "fb_public")
    @NotNull(message = "請選擇是否願意公開評論")
    private Integer fbPublic;

    @Column(name = "fb_status")
    @NotNull(message = "請選擇是否上架評論")
    private Integer fbStatus;
}