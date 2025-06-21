package com.islevilla.wei.room.model.temp;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="room")
@Data
public class Room1 {
    @Column(name = "room_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer roomId;
}
