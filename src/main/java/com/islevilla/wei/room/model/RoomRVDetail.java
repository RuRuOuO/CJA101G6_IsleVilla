package com.islevilla.wei.room.model;


import com.islevilla.chen.room.model.Room;
import com.islevilla.chen.roomType.model.RoomType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "room_reservation_detail")
@Data
public class RoomRVDetail {
    @Column(name = "room_reservation_detail_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer roomReservationDetailId;

    @ManyToOne // 一個訂單能有多筆明細
    @JoinColumn(name = "room_reservation_id", nullable = false)
    private RoomRVOrder roomRVOrder;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    @Column(name = "guest_count")
    @NotNull(message = "請輸入入住人數")
    private Integer guestCount;

    @Column(name = "room_price")
    @NotNull(message = "請輸入購買時金額")
    private Integer roomPrice;

    @Column(name = "rv_discount_amount")
    @NotNull(message = "請輸入折扣金額")
    private Integer rvDiscountAmount;

    @Column(name = "rv_paid_amount")
    @NotNull(message = "請輸入實際付款金額")
    private Integer rvPaidAmount;
}