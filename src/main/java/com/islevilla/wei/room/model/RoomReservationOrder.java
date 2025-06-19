package com.islevilla.wei.room.model;

import com.islevilla.lai.members.model.Members;
import com.islevilla.wei.room.model.temp.Promotion;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_reservation_order")
@Data
public class RoomReservationOrder {
    @Column(name = "room_reservation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer roomReservationId;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    @NotNull(message = "請輸入會員編號")
    private Members members;

    @Column(name = "room_order_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @NotNull(message = "請輸入訂單日期")
    private LocalDateTime roomOrderDate;

    @Column(name = "room_order_status")
    @NotEmpty(message = "請選擇訂單狀態")
    private String roomOrderStatus;

    @Column(name = "check_in_date")
    @NotNull(message = "請選擇住宿開始日期")
    private LocalDateTime checkInDate;

    @Column(name = "check_out_date")
    @NotNull(message = "請選擇住宿結束日期")
    private LocalDateTime checkOutDate;

    @ManyToOne
    @JoinColumn(name = "room_promotion_id")
    private Promotion promotion;

    @Column(name = "rv_remake")
    private String remake;

    @Column(name = "room_total_amount")
    @Min(value = 0, message = "金額不能為負數")
    @NotNull(message = "請輸入訂單總金額")
    private Integer roomTotalAmount;

    @Column(name = "rv_discount_amount")
    @Min(value = 0, message = "金額不能為負數")
    @NotNull(message = "請輸入折扣金額")
    private Integer rvDiscountAmount;

    @Column(name = "rv_paid_amount")
    @Min(value = 0, message = "金額不能為負數")
    @NotNull(message = "請輸入實際付款金額")
    private Integer rvPaidAmount;
}
