package com.islevilla.chen.roomTypeAvailability.model;

import org.springframework.stereotype.Component;

import com.islevilla.chen.roomType.model.RoomType;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="room_type_availability")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class RoomTypeAvailability {
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private RoomTypeAvailabilityId roomTypeAvailabilityId;
	
	@MapsId("roomTypeId")              // 指向 id.roomTypeId
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;
	
	@Column(name="room_type_availability_count")
	@NotNull(message = "房型可用數量不能為空")
	@Min(value = 0, message = "房型可用數量不能小於0")
	private Integer roomTypeAvailabilityCount;
}
