package com.islevilla.wei.news.model;

import com.islevilla.patty.promotion.model.Promotion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Entity
@Table(name = "news") // 對應的Table
@Data
public class News {
    @Column(name = "news_id") // 對應的欄位
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 此欄為auto increment
    @Id // 對應到Table的主鍵
    private Integer newsId;

    @Column(name = "news_title")
    @NotEmpty(message = "請輸入標題") // @NotNull:不能為Null @NotEmpty:不能為Null也不能是空字串
    private String newsTitle;

    @Column(name = "news_content")
    @NotEmpty(message = "請輸入內文")
    @Size(max = 1000, message = "內容長度不可超過1000字")
    private String newsContent;

    @Column(name = "news_image")
    private byte[] newsImage;

    @Column(name = "news_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP") // 預設值為新增消息當下時間
    private LocalDateTime newsTime;

    @Column(name = "news_status")
    private Integer newsStatus; // 0=下架, 1=上架

    @ManyToOne
    @JoinColumn(name = "room_promotion_id")
    private Promotion promotion;

    // ✅ 不要存進資料庫的欄位，加上 @Transient
    @Transient
    private MultipartFile upFiles; // ✅ 一定要有這行！
}