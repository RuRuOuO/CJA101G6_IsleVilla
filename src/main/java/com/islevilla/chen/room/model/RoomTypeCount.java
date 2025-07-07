package com.islevilla.chen.room.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeCount {
    private Integer roomTypeIdDTO;
    private Integer roomCountDTO;
    private Integer roomUnableDTO;
    private Integer roomAvailableDTO;

}
