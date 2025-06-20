//package com.islevilla.patty.roomreservationorder.controller;
//
//import com.islevilla.patty.roomreservationorder.model.RoomReservationOrder;
//import com.islevilla.patty.roomreservationorder.model.RoomReservationOrderService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/roomReservationOrder")
//public class RoomReservationOrderApiController {
//    @Autowired
//    private RoomReservationOrderService roomReservationOrderService;
//
//
//    // 送出新增產品
//    @PostMapping("/add")
//    public void addProduct(@RequestBody RoomReservationOrder roomReservationOrder) {
//    	roomReservationOrderService.addroomReservationOrder(roomReservationOrder);
//    }
////    // 送出修改商品
////    @PostMapping("/edit")
////    public void updateProduct(@Valid @RequestBody Product product) {
////        productService.updateProduct(product);
////    }
//    // 刪除商品
//    @DeleteMapping("/delete/{roomTypeId}")
//    public void deleteRoomReservationOrder(@PathVariable Integer roomTypeId) {
//    	roomReservationOrderService.deleteProduct(roomTypeId);
//    }
//}
