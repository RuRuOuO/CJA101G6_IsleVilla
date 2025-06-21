package com.islevilla.patty.promotion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.io.Serializable;
import java.sql.Date;

@Data
@Entity
@Table(name = "promotion")
public class Promotion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_promotion_id")
    private Integer roomPromotionId;

    @NotEmpty(message = "優惠專案名稱: 請勿空白")
    @Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9)]{2,10}$", message = "優惠專案名稱: 只能是中、英文字母、數字 , 且長度必需在2到10之間")
    @Column(name = "room_promotion_title")
    private String roomPromotionTitle;

    @NotNull(message = "開始日期請勿空白")
    @Column(name = "promotion_start_date")
    private Date promotionStartDate;

    @Column(name = "promotion_end_date")
    private Date promotionEndDate;

    @Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9)]{2,10}$", message = "備註只能是中、英文或數字，長度2~10")
    @Column(name = "promotion_remark")
    private String promotionRemark;
}
