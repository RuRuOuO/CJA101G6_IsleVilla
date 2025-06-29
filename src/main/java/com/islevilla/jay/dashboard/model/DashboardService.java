package com.islevilla.jay.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.islevilla.jay.dashboard.DashboardDTO;
import com.islevilla.jay.productOrder.model.ProductOrder;
import com.islevilla.jay.productOrder.model.ProductOrderRepository;
import com.islevilla.wei.room.model.RoomRVOrder;
import com.islevilla.chen.room.model.RoomRepository;
import com.islevilla.chen.room.model.Room;
import com.islevilla.jay.dashboard.DashboardRoomRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DashboardService {

    @Autowired
    private ProductOrderRepository productOrderRepository;
    
    @Autowired
    private DashboardRepository dashboardRepository;
    
    @Autowired
    private DashboardRoomRepository dashboardRoomRepository;
    
    /**
     * 取得今日的dashboard統計資料
     * @return DashboardDTO 包含所有統計資料
     */
    public DashboardDTO getTodayDashboardData() {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(23, 59, 59);
        
        // 統計今日商品訂單數量
        long todayProductOrders = productOrderRepository.countByOrderTimeBetween(todayStart, todayEnd);
        
        // 計算今日商品訂單營收
        List<ProductOrder> todayProductOrdersList = productOrderRepository.findByOrderTimeBetween(todayStart, todayEnd);
        double todayProductRevenue = todayProductOrdersList.stream()
                .mapToDouble(order -> order.getProductOrderAmount() != null ? order.getProductOrderAmount() : 0.0)
                .sum();
        
        // 統計待處理商品訂單數量（訂單成立狀態）
        long pendingProductOrders = productOrderRepository.countByOrderStatus((byte) 0);
        
        // 取得訂單成立的購物訂單
        List<ProductOrder> confirmedProductOrders = productOrderRepository.findByOrderStatusOrderByOrderTimeDesc((byte) 0);
        
        // 統計今日房間訂單數量
        long todayRoomOrders = countTodayRoomOrders(todayStart, todayEnd);
        
        // 計算今日房間訂單營收
        double todayRoomRevenue = calculateTodayRoomRevenue(todayStart, todayEnd);
        
        // 取得今日成立的房間訂單
        List<RoomRVOrder> todayRoomOrdersList = findTodayRoomOrders(todayStart, todayEnd);
        
        double roomOccupancyRate = calculateRoomOccupancyRate();
        DashboardDTO dto = new DashboardDTO(
            todayProductOrders,
            todayProductRevenue,
            pendingProductOrders,
            confirmedProductOrders,
            todayRoomOrders,
            todayRoomRevenue,
            todayRoomOrdersList
        );
        dto.setRoomOccupancyRate(roomOccupancyRate);
        return dto;
    }
    
    /**
     * 取得指定日期範圍的dashboard統計資料
     * @param startDate 開始日期
     * @param endDate 結束日期
     * @return DashboardDTO 包含所有統計資料
     */
    public DashboardDTO getDashboardDataByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        long productOrders = productOrderRepository.countByOrderTimeBetween(startDateTime, endDateTime);
        
        List<ProductOrder> productOrdersList = productOrderRepository.findByOrderTimeBetween(startDateTime, endDateTime);
        double productRevenue = productOrdersList.stream()
                .mapToDouble(order -> order.getProductOrderAmount() != null ? order.getProductOrderAmount() : 0.0)
                .sum();
        
        long pendingOrders = productOrderRepository.countByOrderStatus((byte) 0);
        List<ProductOrder> confirmedOrders = productOrderRepository.findByOrderStatusOrderByOrderTimeDesc((byte) 0);
        
        // 房間訂單統計
        long roomOrders = countTodayRoomOrders(startDateTime, endDateTime);
        double roomRevenue = calculateTodayRoomRevenue(startDateTime, endDateTime);
        List<RoomRVOrder> roomOrdersList = findTodayRoomOrders(startDateTime, endDateTime);
        
        return new DashboardDTO(
            productOrders,
            productRevenue,
            pendingOrders,
            confirmedOrders,
            roomOrders,
            roomRevenue,
            roomOrdersList
        );
    }
    
    /**
     * 統計指定時間範圍內的房間訂單數量
     * @param startTime 開始時間
     * @param endTime 結束時間
     * @return 房間訂單數量
     */
    private long countTodayRoomOrders(LocalDateTime startTime, LocalDateTime endTime) {
        return dashboardRepository.countRoomOrdersByDateRange(startTime, endTime);
    }
    
    /**
     * 計算指定時間範圍內的房間訂單營收
     * @param startTime 開始時間
     * @param endTime 結束時間
     * @return 房間訂單營收
     */
    private double calculateTodayRoomRevenue(LocalDateTime startTime, LocalDateTime endTime) {
        List<RoomRVOrder> roomOrders = dashboardRepository.findRoomOrdersByDateRange(startTime, endTime);
        return roomOrders.stream()
                .mapToDouble(order -> order.getRvPaidAmount() != null ? order.getRvPaidAmount() : 0.0)
                .sum();
    }
    
    /**
     * 取得指定時間範圍內的房間訂單列表
     * @param startTime 開始時間
     * @param endTime 結束時間
     * @return 房間訂單列表
     */
    private List<RoomRVOrder> findTodayRoomOrders(LocalDateTime startTime, LocalDateTime endTime) {
        return dashboardRepository.findRoomOrdersByDateRangeOrderByDateDesc(startTime, endTime);
    }
    
    /**
     * 查詢今日所有商品訂單（不限制狀態）
     */
    public List<ProductOrder> findTodayAllProductOrders(LocalDateTime todayStart, LocalDateTime todayEnd) {
        return productOrderRepository.findByOrderTimeBetween(todayStart, todayEnd);
    }

    public double calculateRoomOccupancyRate() {
        long totalRooms = dashboardRoomRepository.count();
        long occupiedRooms = dashboardRoomRepository.countByRoomStatus((byte)1); // 1:入住中
        System.out.println("總房間數：" + totalRooms);
        System.out.println("已入住房間數：" + occupiedRooms);
        if (totalRooms == 0) return 0;
        return Math.round((occupiedRooms * 1000.0 / totalRooms)) / 10.0;
    }
} 