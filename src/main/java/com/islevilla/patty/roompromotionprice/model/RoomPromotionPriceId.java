package com.islevilla.patty.roompromotionprice.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomPromotionPriceId implements Serializable {

    @Column(name = "room_promotion_id")
    private Integer roomPromotionId;

    @Column(name = "room_type_id")
    private Integer roomTypeId;
}