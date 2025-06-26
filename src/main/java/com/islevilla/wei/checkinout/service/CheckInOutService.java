package com.islevilla.wei.checkinout.service;

import com.islevilla.chen.room.model.Room;
import com.islevilla.chen.room.model.RoomService;
import com.islevilla.wei.room.model.RoomRVDetailService;
import com.islevilla.wei.room.model.RoomRVOrder;
import com.islevilla.wei.room.model.RoomRVOrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CheckInOutService {
    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRVOrderService roomRVOrderService;
    @Autowired
    private RoomRVDetailService roomRVDetailService;

    // 入住流程
    // 傳入訂房編號; 回傳是否成功入住
    @Transactional
    public boolean checkIn(Integer roomRVOrderId) {
        try {
            // 查詢訂房資料
            RoomRVOrder rv = roomRVOrderService.getById(roomRVOrderId);
            if (rv == null) {
                throw new IllegalArgumentException(roomRVOrderId + "號訂單不存在");
            }
            // 檢查訂單狀態 0:成立
            if (rv.getRoomOrderStatus() != 0) {
                throw new IllegalArgumentException("訂單狀態非成立，無法入住");
            }
            // 更新訂單狀態為已入住
            rv.setRoomOrderStatus(1);
            rv.setActualCheckInDate(LocalDateTime.now());
            roomRVOrderService.updateRoomRVOrder(rv);
            // 更新房間狀態
            updateRoomStatusToOccupied(roomRVOrderId);

            return true;

        } catch (Exception e) {
            throw new RuntimeException("入住流程失敗" + e.getMessage(), e);
        }
    }


    // 退房流程
    // 傳入訂房編號; 回傳是否成功退房
    @Transactional
    public boolean checkOut(Integer roomRVOrderId) {
        try {
            // 查詢訂房資料
            RoomRVOrder rv = roomRVOrderService.getById(roomRVOrderId);
            if (rv == null) {
                throw new IllegalArgumentException("訂房資料不存在");
            }
            // 檢查訂單狀態
            if (rv.getRoomOrderStatus() != 1) { // 訂單狀態非「已入住」
                throw new IllegalStateException("訂單狀態非「已入住」，無法退房");
            }
            // 更新房間狀態為待清潔
            updateRoomStatusToCleaning(roomRVOrderId);

            return true;

        } catch (Exception e) {
            throw new RuntimeException("退房流程失敗: " + e.getMessage(), e);
        }
    }

    // 更新房間狀態為入住中
    private void updateRoomStatusToOccupied(Integer roomRVOrderId) {
        Integer roomId = getRoomIdFromRvId(roomRVOrderId);

        Room room = roomService.findById(roomId);
        if (room != null && room.getRoomStatus() == 0) { // 房間存在且狀態為空房
            room.setRoomStatus((byte) 1); // 房間狀態改為入住中
            roomService.updateRoom(room); // 儲存變更
        } else {
            throw new IllegalStateException("房間狀態非「空房」，無法入住");
        }
    }

    // 更新房間狀態為待清潔
    private void updateRoomStatusToCleaning(Integer roomRVOrderId) {
        Integer roomId = getRoomIdFromRvId(roomRVOrderId);

        Room room = roomService.findById(roomId);
        if (room != null && room.getRoomStatus() == 1) { // 房間存在且狀態為入住中
            room.setRoomStatus((byte) 3); // 房間狀態改為待清潔
            roomService.updateRoom(room); // 儲存變更
        } else {
            throw new IllegalStateException("房間狀態非「入住中」，無法退房");
        }
    }

    // 從訂房訂單取得房間編號
    private Integer getRoomIdFromRvId(Integer roomRVOrderId) {
        return roomRVDetailService.getDetailsByRoomRVOrderId(roomRVOrderId).get(0).getRoom().getRoomId();
    }

    // 檢查是否可以入住
    public boolean canCheckIn(Integer roomRVOrderId) {
        RoomRVOrder rv = roomRVOrderService.getById(roomRVOrderId);
        return rv != null && rv.getRoomOrderStatus() == 0; // rv存在且狀態為0:成立
    }

    // 檢查是否可以退房
    public boolean canCheckOut(Integer roomRVOrderId) {
        RoomRVOrder rv = roomRVOrderService.getById(roomRVOrderId);
        return rv != null && rv.getRoomOrderStatus() == 1; // rv存在且狀態為1:已入住
    }

}