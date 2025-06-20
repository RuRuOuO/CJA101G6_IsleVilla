package com.islevilla.wei.room.model.temp;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="promotion")
@Data
public class Promotion {
    @Column(name = "room_promotion_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer promotionId;

}
