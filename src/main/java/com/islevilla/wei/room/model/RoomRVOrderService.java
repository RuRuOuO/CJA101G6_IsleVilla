package com.islevilla.wei.room.model;

import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeRepository;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailability;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailabilityId;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailabilityRepository;
import com.islevilla.lai.email.model.ShuttleEmailService;
import com.islevilla.lai.members.model.Members;
import com.islevilla.lai.shuttle.model.ShuttleReservationSeatService;
import com.islevilla.lai.shuttle.model.ShuttleReservationService;
import com.islevilla.lai.shuttle.model.ShuttleSeatAvailabilityService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    @Autowired
    private ShuttleReservationService shuttleReservationService;
    @Autowired
    private ShuttleReservationSeatService shuttleReservationSeatService;
    @Autowired
    private ShuttleSeatAvailabilityService shuttleSeatAvailabilityService;
    @Autowired
    private ShuttleEmailService shuttleEmailService;

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

    // 用會員id查詢該會員所有訂單（依照訂單編號倒序排列）
    public List<RoomRVOrder> getRoomRVOrderByMemberDesc(Members member) {
        return roomRVOrderRepository.findByMembersOrderByRoomReservationIdDesc(member);
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
        double refundRate = calculateRefundRate(order.getCheckInDate());
        int refundAmount = (int) Math.round(order.getRvPaidAmount() * refundRate);
        if (order != null) {
            if (order.getRoomOrderStatus() != 0) {
                throw new RuntimeException("只有已確認的訂單才能取消");
            }
            // 更新訂單狀態為取消申請中
            order.setRoomOrderStatus(3);
            order.setRvRefundAmount(refundAmount);      // 設定退款金額
            order.setRvCancelTime(LocalDateTime.now()); // 設定取消時間
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

            // 1. 取消所有關聯的接駁預約
            List<com.islevilla.lai.shuttle.model.ShuttleReservation> reservations =
                    shuttleReservationService.getReservationsByRoomReservation(order);

            for (com.islevilla.lai.shuttle.model.ShuttleReservation reservation : reservations) {
                Integer shuttleReservationId = reservation.getShuttleReservationId();
                try {
                    // 1.1 發送取消郵件
                    shuttleEmailService.sendShuttleReservationCancellation(shuttleReservationId);

                    // 1.2 將預約狀態設為取消
                    shuttleReservationService.cancelReservation(shuttleReservationId);

                    // 1.3 刪除座位預約
                    shuttleReservationSeatService.deleteByShuttleReservationId(shuttleReservationId);

                    // 1.4 釋放座位
                    shuttleSeatAvailabilityService.cancelReservation(
                            reservation.getShuttleSchedule().getShuttleScheduleId(),
                            reservation.getShuttleDate(),
                            reservation.getShuttleNumber()
                    );
                } catch (Exception e) {
                    // 記錄log或處理例外
                    System.out.println("取消接駁預約失敗: " + e.getMessage());
                }
            }

            // 2. 訂單取消與回補庫存
            List<RoomRVDetail> orderDetails = roomRVDetailService.getDetailsByRoomRVOrderId(orderId);
            restoreRoomTypeAvailability(orderDetails, order.getCheckInDate(), order.getCheckOutDate());
            order.setRoomOrderStatus(4); // 4: 後台取消
            updateRoomRVOrder(order);

            System.out.println("訂單已取消並回補庫存 - 訂單ID: " + orderId);
        }
    }

    // 依照當日離checkin的天數計算退款比例
    public double calculateRefundRate(LocalDate checkInDate) {
        long daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), checkInDate);
        if (daysBetween >= 14) return 1.0;
        if (daysBetween >= 10) return 0.7;
        if (daysBetween >= 7) return 0.5;
        if (daysBetween >= 4) return 0.4;
        if (daysBetween >= 2) return 0.3;
        if (daysBetween >= 1) return 0.2;
        return 0.0;
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
