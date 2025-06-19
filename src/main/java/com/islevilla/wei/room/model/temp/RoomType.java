package com.islevilla.wei.room.model.temp;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="room_type")
@Data
public class RoomType {
    @Column(name = "room_type_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer roomTypeId;
}
