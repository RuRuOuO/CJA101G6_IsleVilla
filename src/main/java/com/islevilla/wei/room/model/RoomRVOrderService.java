package com.islevilla.wei.room.model;

import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeRepository;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailability;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailabilityId;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailabilityRepository;
import com.islevilla.lai.members.model.Members;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class RoomRVOrderService {
    @Autowired
    private RoomRVOrderRepository roomRVOrderRepository;
    @Autowired
    private RoomRVDetailService roomRVDetailService;
    @Autowired
    private RoomTypeAvailabilityRepository roomTypeAvailabilityRepository;
    @Autowired
    private RoomTypeRepository roomTypeRepository;

    // 查詢全部 // 分頁
    public Page<RoomRVOrder> getAll(Pageable pageable) {
        return roomRVOrderRepository.findAll(pageable);
    }

    // 查詢全部
    public List<RoomRVOrder> findAll() {
        return roomRVOrderRepository.findAll();
    }

    // 查詢全部且按日期排序
    public List<RoomRVOrder> getAllOrders() {
        return roomRVOrderRepository.findAllByOrderByRoomOrderDateDesc();
    }

    // 查詢可操作(成立或入住中)的訂單
    public List<RoomRVOrder> getOperableOrders() {
        return roomRVOrderRepository.findByRoomOrderStatusIn(Arrays.asList(0, 1));
    }

    // 查詢今日要入住或退房的訂單
    public List<RoomRVOrder> getTodayCheckInOrOutOrders() {
        LocalDate today = LocalDate.now();
        return roomRVOrderRepository.findByCheckInDateOrCheckOutDate(today, today);
    }

    // 用id查詢單筆
    public RoomRVOrder getById(Integer id) {
        Optional<RoomRVOrder> roomRVOrder = roomRVOrderRepository.findById(id);
        return roomRVOrder.orElse(null);
    }

    // 用會員id查詢該會員所有訂單
    public List<RoomRVOrder> getRoomRVOrderByMember(Members member) {
        return roomRVOrderRepository.findByMembers(member);
    }

    // 用會員id查詢該會員今天以後的訂單
    public List<RoomRVOrder> getFutureRoomRVOrderByMember(Members member) {
        LocalDate today = LocalDate.now();
        return roomRVOrderRepository.findByMembersAndCheckInDateGreaterThanAndRoomOrderStatus(member, today, 0);
    }

    // 更新訂單
    public void updateRoomRVOrder(RoomRVOrder roomRVOrder) {
        roomRVOrderRepository.save(roomRVOrder);
    }

    // 前台取消訂單
    public void cancelOrderFront(Integer orderId) {
        RoomRVOrder order = getById(orderId);
        if (order != null) {
            if (order.getRoomOrderStatus() != 0) {
                throw new RuntimeException("只有已確認的訂單才能取消");
            }
            // 更新訂單狀態為取消申請中
            order.setRoomOrderStatus(3);
            updateRoomRVOrder(order);
        }
    }

    // 後台取消訂單
    @Transactional
    public void cancelOrderBack(Integer orderId) {
        RoomRVOrder order = getById(orderId);
        if (order != null) {
            // 檢查訂單狀態，只有已確認或已入住的訂單才能取消
            if (order.getRoomOrderStatus() != 3) {
                throw new RuntimeException("只有申請取消中的訂單才能取消");
            }

            // 獲取訂單明細List
            List<RoomRVDetail> orderDetails = roomRVDetailService.getDetailsByRoomRVOrderId(orderId);

            // 回補每個明細中對應的房型庫存
            restoreRoomTypeAvailability(orderDetails, order.getCheckInDate(), order.getCheckOutDate());

            // 更新訂單狀態
            order.setRoomOrderStatus(4); // 4: 後台取消
            updateRoomRVOrder(order);

            System.out.println("訂單已取消並回補庫存 - 訂單ID: " + orderId);
        }
    }

    // 回補訂房明細中所有房型庫存量
    @Transactional
    public void restoreRoomTypeAvailability(List<RoomRVDetail> orderDetails, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new RuntimeException("開始日期不能晚於結束日期");
        }

        // 按房型分組統計數量
        Map<Integer, Integer> roomTypeQuantities = new HashMap<Integer, Integer>();

        for (RoomRVDetail detail : orderDetails) {
            Integer roomTypeId = detail.getRoomType().getRoomTypeId();
            Integer count = roomTypeQuantities.get(roomTypeId);
            if (count == null) {
                roomTypeQuantities.put(roomTypeId, 1);
            } else {
                roomTypeQuantities.put(roomTypeId, count + 1);
            }
        }

        // 為每個房型回補庫存
        for (Map.Entry<Integer, Integer> entry : roomTypeQuantities.entrySet()) {
            Integer roomTypeId = entry.getKey();
            Integer quantity = entry.getValue();

            restoreSingleRoomTypeAvailability(roomTypeId, startDate, endDate, quantity);
        }
    }

    // 回補單個房型在日期區段中庫存量的方法
    @Transactional
    public void restoreSingleRoomTypeAvailability(Integer roomTypeId, LocalDate startDate, LocalDate endDate, Integer quantity) {
        LocalDate currentDate = startDate;

        while (currentDate.isBefore(endDate)) {
            // 檢查是否已存在記錄
            RoomTypeAvailabilityId id = new RoomTypeAvailabilityId(roomTypeId, currentDate);
            Optional<RoomTypeAvailability> existingOpt = roomTypeAvailabilityRepository.findById(id);

            if (existingOpt.isPresent()) {
                // 更新現有記錄，增加可用數量
                RoomTypeAvailability availability = existingOpt.get();
                Integer currentCount = availability.getRoomTypeAvailabilityCount();
                availability.setRoomTypeAvailabilityCount(currentCount + quantity);
                roomTypeAvailabilityRepository.save(availability);

                System.out.println("已回補房型可用性 - 房型ID: " + roomTypeId +
                        ", 日期: " + currentDate +
                        ", 原數量: " + currentCount +
                        ", 回補數量: " + quantity +
                        ", 新數量: " + availability.getRoomTypeAvailabilityCount());
            } else {
                // 如果記錄不存在，創建新記錄
                Optional<RoomType> roomType = roomTypeRepository.findById(roomTypeId);
                if (!roomType.isPresent()) {
                    throw new RuntimeException("房型不存在，ID: " + roomTypeId);
                }

                RoomTypeAvailability availability = new RoomTypeAvailability();
                availability.setRoomTypeAvailabilityId(id);
                availability.setRoomType(roomType.get());
                availability.setRoomTypeAvailabilityCount(quantity);
                roomTypeAvailabilityRepository.save(availability);

                System.out.println("已創建房型可用性記錄 - 房型ID: " + roomTypeId +
                        ", 日期: " + currentDate +
                        ", 數量: " + quantity);
            }
            currentDate = currentDate.plusDays(1);
        }
    }
}
