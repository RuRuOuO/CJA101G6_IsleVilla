package com.islevilla.wei.checkinout.service;

import com.islevilla.chen.room.model.Room;
import com.islevilla.chen.room.model.RoomService;
import com.islevilla.wei.room.model.RoomRVDetail;
import com.islevilla.wei.room.model.RoomRVDetailService;
import com.islevilla.wei.room.model.RoomRVOrder;
import com.islevilla.wei.room.model.RoomRVOrderService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckInOutService {
    @Autowired
    private RoomService roomService;
    @Autowired
    private RoomRVOrderService roomRVOrderService;
    @Autowired
    private RoomRVDetailService roomRVDetailService;

    // 入住+分配房間
    @Transactional
    public boolean checkInWithRoomSelection(Integer roomRVOrderId, Map<Integer, Integer> detailRoomMap) {
        try {
            // 查詢訂房資料
            RoomRVOrder rv = roomRVOrderService.getById(roomRVOrderId);
            if (rv == null) {
                throw new IllegalArgumentException(roomRVOrderId + "號訂單不存在");
            }
            // 檢查訂單狀態為0:成立
            if (rv.getRoomOrderStatus() != 0) {
                throw new IllegalArgumentException("訂單狀態非「成立」，無法入住");
            }
            // 為每個明細分配房間
            List<RoomRVDetail> details = rv.getRoomRVDetails();
            for (RoomRVDetail detail : details) {
                Integer selectedRoomId = detailRoomMap.get(detail.getRoomReservationDetailId());
                if (selectedRoomId == null) {
                    throw new IllegalArgumentException("請為所有訂房明細選擇房間");
                }
                // 驗證房間是否可用
                Room selectedRoom = roomService.findById(selectedRoomId);
                if (selectedRoom == null) {
                    throw new IllegalArgumentException("選擇的房間不存在");
                }
                if (selectedRoom.getRoomStatus() != 0) {
                    throw new IllegalArgumentException("房間 " + selectedRoomId + " 狀態非空房，無法入住");
                }
                if (!selectedRoom.getRoomTypeId().equals(detail.getRoomType().getRoomTypeId())) {
                    throw new IllegalArgumentException("房間 " + selectedRoomId + " 房型不符合訂單要求");
                }

                // 儲存房間資訊到訂房明細
                detail.setRoom(selectedRoom);
                roomRVDetailService.updateRoomRVDetail(detail);

                // 更新房間狀態為入住中
                selectedRoom.setRoomStatus((byte) 1);
                roomService.updateRoom(selectedRoom);
            }
            // 更新訂單狀態為已入住
            rv.setRoomOrderStatus(1);
            rv.setActualCheckInDate(LocalDateTime.now());
            roomRVOrderService.updateRoomRVOrder(rv);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("入住流程失敗: " + e.getMessage(), e);
        }
    }

    // 退房
    @Transactional
    public boolean checkOut(Integer roomRVOrderId) {
        try {
            // 確認訂單存在
            RoomRVOrder rv = roomRVOrderService.getById(roomRVOrderId);
            if (rv == null) {
                throw new IllegalArgumentException("訂房資料不存在");
            }
            // 檢查訂單狀態為「已入住」
            if (rv.getRoomOrderStatus() != 1) {
                throw new IllegalStateException("訂單狀態非「已入住」，無法退房");
            }

            // 更新訂單中所有明細對應的房間狀態為待清潔
            List<RoomRVDetail> details = rv.getRoomRVDetails();
            for (RoomRVDetail detail : details) {
                Room room = detail.getRoom();
                if (room != null && room.getRoomStatus() == 1) { // 若房間存在且狀態為入住中
                    room.setRoomStatus((byte) 3); // 更新狀態為待清潔
                    roomService.updateRoom(room);
                }
            }
            // 更新訂單狀態為已退房
            rv.setRoomOrderStatus(2);
            rv.setActualCheckOutDate(LocalDateTime.now());
            roomRVOrderService.updateRoomRVOrder(rv);
            return true; // 退房成功
        } catch (Exception e) {
            throw new RuntimeException("退房流程失敗: " + e.getMessage(), e);
        }
    }

    // 取得訂單中每個明細指定房型的空房列表
    public Map<Integer, List<Room>> getAvailableRoomsForCheckIn(Integer roomRVOrderId) {
        RoomRVOrder order = roomRVOrderService.getById(roomRVOrderId);
        if (order == null) {
            throw new RuntimeException("訂單不存在");
        }

        List<RoomRVDetail> details = order.getRoomRVDetails();
        Map<Integer, List<Room>> resultMap = new HashMap<>();

        for (RoomRVDetail detail : details) {
            Integer roomTypeId = detail.getRoomType().getRoomTypeId();
            List<Room> availableRooms = roomService.compoundQuery(null, roomTypeId, (byte) 0); // 房號, 房型編號, 房間狀態
            resultMap.put(detail.getRoomReservationDetailId(), availableRooms);
        }
        return resultMap;
    }

    // 取得特定房型的空房列表
    private List<Room> getAvailableRoomsByType(Integer roomTypeId) {
        return roomService.compoundQuery(null, roomTypeId, (byte) 0); // 房號, 房型編號, 房間狀態
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

    // 檢查訂單是否需要選擇房間
    public boolean needsRoomSelection(Integer roomRVOrderId) {
        RoomRVOrder rv = roomRVOrderService.getById(roomRVOrderId);
        if (rv == null) return false;

        List<RoomRVDetail> details = rv.getRoomRVDetails();
        for (RoomRVDetail detail : details) {
            if (detail.getRoom() == null) {
                return true; // 明細中沒有房間資訊: 需要選擇房間
            }
        }
        return false; // 不需要選擇房間
    }
}