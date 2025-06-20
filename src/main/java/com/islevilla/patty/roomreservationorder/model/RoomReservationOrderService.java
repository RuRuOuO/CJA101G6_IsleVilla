//package com.islevilla.patty.roomreservationorder.model;
//
////import com.islevilla.yin.roomtypephoto.RoomtypePhoto;
////import com.islevilla.yin.roomtypephoto.RoomtypePhotoRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class RoomReservationOrderService {
//    @Autowired
//    private RoomReservationOrderRepository roomReservationOrderRepository;
//    @Autowired
//    private RoomTypePhotoRepository roomTypePhotoRepository;
//
//    public void addRoomReservationOrder(RoomReservationOrder roomReservationOrder) {
//    	roomReservationOrderRepository.save(roomReservationOrder);
//    }
//    public void updateRoomReservationOrder(RoomReservationOrder roomReservationOrder) {
//    	roomReservationOrderRepository.save(roomReservationOrder);
//    }
//    public void deleteRoomReservationOrder(Integer roomTypeId) {
//        if(roomReservationOrderRepository.existsById(roomTypeId)){
//        	roomReservationOrderRepository.deleteById(roomTypeId);
//        }
//    }
//    public RoomReservationOrder getRoomReservationOrderById(Integer roomTypeId) {
//        return roomReservationOrderRepository.findById(roomTypeId)
//                .orElse(null);
//    }
//    public List<RoomReservationOrder> getAllRoomReservationOrder() {
//        return roomReservationOrderRepository.findAll();
//    }
////    public List<RoomReservationOrder> getRoomReservationOrderByPromotionId(Integer promotionId) {
////        return roomReservationOrderRepository.findByPromotionPromotionId(promotionId);
////    }
//
//
//}
