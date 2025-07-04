package com.islevilla.patty.roompromotionprice.model;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.*;
import com.islevilla.chen.roomType.model.RoomType;
import lombok.Data;

@Data
@Entity
@Table(name = "room_promotion_price")
public class RoomPromotionPrice implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "room_promotion_id")
    private Integer roomPromotionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    @Column(name = "promotion_start_date")
    private LocalDate promotionStartDate;

    @Column(name = "promotion_end_date")
    private LocalDate promotionEndDate;

    @Column(name = "room_discount_rate")
    private Double roomDiscountRate;

    @Column(name = "promotion_remark")
    private String promotionRemark;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_promotion_id", referencedColumnName = "room_promotion_id", insertable = false, updatable = false)
    private com.islevilla.patty.promotion.model.Promotion promotion;

    public RoomPromotionPrice() {}
}
