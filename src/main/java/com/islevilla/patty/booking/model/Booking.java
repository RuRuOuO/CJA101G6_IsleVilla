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
    private String roomTypeSummary; // 房型x間數顯示用
    private String remark; // 備註
    private java.util.List<Detail> details; // 訂房明細

    @lombok.Data
    public static class Detail {
        private String roomTypeName;
        private String promotionTitle;
        private Integer price;
    }
} 