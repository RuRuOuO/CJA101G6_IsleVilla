package com.islevilla.wei.news.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="news") // 對應的Table
@Data
public class NewsVO {
    @Column(name="news_id") // 對應的欄位
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 此欄為auto increment
    @Id // 對應到Table的主鍵
    private Integer newsId;

    @Column(name="news_title")
    @NotEmpty(message="請輸入標題") // @NotNull:不能為Null @NotEmpty:不能為Null也不能是空字串
    private String newsTitle;

    @Column(name="news_content")
    @NotEmpty(message="請輸入內文")
    private String newsContent;

    @Column(name="news_image")
    private byte[] newsImage;

    @Column(name="news_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime newsTime;
}