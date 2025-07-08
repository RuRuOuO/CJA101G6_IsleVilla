package com.islevilla.patty.roompromotionprice.model;

import java.beans.Transient;
import java.time.LocalDate;

import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.patty.promotion.model.Promotion;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "room_promotion_price")
public class RoomPromotionPrice {
	
	@EmbeddedId
	private RoomPromotionPriceId id;
	
	// 關聯到 Promotion（多對一）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_promotion_id", insertable = false, updatable = false)
    private Promotion promotion;

    // 關聯到 RoomType（多對一）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", insertable = false, updatable = false)
    private RoomType roomType;

    @Column(name = "room_discount_rate", columnDefinition = "DECIMAL(3,2) DEFAULT 1.0")
    private Double roomDiscountRate;
    
    @Transient
    public Double getDiscountedPrice() {
        if (roomType != null && roomType.getRoomTypePrice() != null && roomDiscountRate != null) {
            return (double) Math.round(roomType.getRoomTypePrice() * roomDiscountRate);
        }
        return null;
    }
    
 // 給 Thymeleaf 使用的便捷 getter
    @Transient
    public Integer getRoomPromotionId() {
        return id != null ? id.getRoomPromotionId() : null;
    }

    @Transient
    public Integer getRoomTypeId() {
        return id != null ? id.getRoomTypeId() : null;
    }
}
