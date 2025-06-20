//package com.islevilla.patty.roomreservationorder.model;
//
//
//import com.islevilla.wei.room.model.temp.RoomType;
//
//import com.islevilla.patty.promotion.model.Promotion;
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotEmpty;
//import jakarta.validation.constraints.NotNull;
//import lombok.Data;
//
//
//@Entity
//@Table(name = "room_reservation_order")
//@Data
//public class RoomReservationOrder {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "room_reservation_id")
//    private Integer roomReservationId;
//
//    @ManyToOne
//    @JoinColumn(name = "room_type_id", nullable = false)
//    private RoomType roomType; // 關聯房型
//
//    @Column(name = "members")
//    private String memberName;
//
//    @Column(name = "check_in_date")
//    private CheckInDate checkInDate;
//
//    @Column(name = "check_out_date")
//    private CheckOutDate checkOutDate;
//
//    @Column(name = "room_order_status")
//    private Byte roomOrderStatus; // 0:訂單成立 1:付款中 2:已完成 3:已退款 4:已取消
//}
//
