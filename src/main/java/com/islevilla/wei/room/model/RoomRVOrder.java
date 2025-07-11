package com.islevilla.wei.room.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.islevilla.lai.members.model.Members;
import com.islevilla.patty.promotion.model.Promotion;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "room_reservation_order")
@Data
public class RoomRVOrder {
    @Column(name = "room_reservation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer roomReservationId;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
    @NotNull(message = "請輸入會員編號")
    private Members members;

    @Column(name = "room_order_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @NotNull(message = "請輸入訂單日期")
    private LocalDateTime roomOrderDate;

    @Column(name = "room_order_status")
    @NotNull(message = "請選擇訂單狀態")
    private Integer roomOrderStatus;

    @Column(name = "check_in_date")
    @NotNull(message = "請選擇住宿開始日期")
    private LocalDate checkInDate;

    @Column(name = "actual_check_in_date")
    private LocalDateTime actualCheckInDate;

    @Column(name = "check_out_date")
    @NotNull(message = "請選擇住宿結束日期")
    private LocalDate checkOutDate;

    @Column(name = "actual_check_out_date")
    private LocalDateTime actualCheckOutDate;

    @ManyToOne
    @JoinColumn(name = "room_promotion_id")
    private Promotion promotion;

    @Column(name = "rv_remark")
    private String remark;

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

    @Column(name = "rv_cancel_time")
    private LocalDateTime rvCancelTime; // 取消時間

    @Column(name = "rv_refund_amount")
    private Integer rvRefundAmount; // 實際退款金額

    // @OneToMamy
    @OneToMany(mappedBy = "roomRVOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // 排除此屬性避免循環引用
    private List<RoomRVDetail> roomRVDetails;
}
