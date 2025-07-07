package com.islevilla.patty.booking.model;

import java.time.LocalDate;
import lombok.Data;

@Data
public class Booking {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String roomType;
    private Integer roomCount;
    private Integer totalAmount;
    private String email; // 訂房時填寫的聯絡信箱
} 