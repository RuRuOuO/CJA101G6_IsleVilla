package com.islevilla.chen.roomTypeAvailability.model;

import java.io.Serializable;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
//管理複合主鍵的實體
public class RoomTypeAvailabilityId implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "room_type_id")
	@NotNull(message = "房型ID不能為空")
    private Integer roomTypeId;

    @Column(name = "room_type_availability_date")
    @NotNull(message = "房型可用日期不能為空")
    private LocalDate roomTypeAvailabilityDate;

    // equals() 和 hashCode() 方法由 @Data 自動生成
}
