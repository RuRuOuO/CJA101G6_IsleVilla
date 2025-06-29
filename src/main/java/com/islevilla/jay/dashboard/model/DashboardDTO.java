package com.islevilla.jay.dashboard.model;

import java.util.List;
import com.islevilla.jay.productOrder.model.ProductOrder;
import com.islevilla.wei.room.model.RoomRVOrder;

public class DashboardDTO {
    private long todayProductOrders;
    private double todayProductRevenue;
    private long pendingProductOrders;
    private List<ProductOrder> confirmedProductOrders;
    
    // 新增房間訂單相關欄位
    private long todayRoomOrders;
    private double todayRoomRevenue;
    private List<RoomRVOrder> todayRoomOrdersList;
    
    private double roomOccupancyRate;
    
    // 建構子
    public DashboardDTO() {}
    
    public DashboardDTO(long todayProductOrders, double todayProductRevenue, 
                       long pendingProductOrders, List<ProductOrder> confirmedProductOrders,
                       long todayRoomOrders, double todayRoomRevenue, List<RoomRVOrder> todayRoomOrdersList) {
        this.todayProductOrders = todayProductOrders;
        this.todayProductRevenue = todayProductRevenue;
        this.pendingProductOrders = pendingProductOrders;
        this.confirmedProductOrders = confirmedProductOrders;
        this.todayRoomOrders = todayRoomOrders;
        this.todayRoomRevenue = todayRoomRevenue;
        this.todayRoomOrdersList = todayRoomOrdersList;
    }
    
    // Getter 和 Setter
    public long getTodayProductOrders() {
        return todayProductOrders;
    }
    
    public void setTodayProductOrders(long todayProductOrders) {
        this.todayProductOrders = todayProductOrders;
    }
    
    public double getTodayProductRevenue() {
        return todayProductRevenue;
    }
    
    public void setTodayProductRevenue(double todayProductRevenue) {
        this.todayProductRevenue = todayProductRevenue;
    }
    
    public long getPendingProductOrders() {
        return pendingProductOrders;
    }
    
    public void setPendingProductOrders(long pendingProductOrders) {
        this.pendingProductOrders = pendingProductOrders;
    }
    
    public List<ProductOrder> getConfirmedProductOrders() {
        return confirmedProductOrders;
    }
    
    public void setConfirmedProductOrders(List<ProductOrder> confirmedProductOrders) {
        this.confirmedProductOrders = confirmedProductOrders;
    }
    
    // 新增房間訂單的getter和setter
    public long getTodayRoomOrders() {
        return todayRoomOrders;
    }
    
    public void setTodayRoomOrders(long todayRoomOrders) {
        this.todayRoomOrders = todayRoomOrders;
    }
    
    public double getTodayRoomRevenue() {
        return todayRoomRevenue;
    }
    
    public void setTodayRoomRevenue(double todayRoomRevenue) {
        this.todayRoomRevenue = todayRoomRevenue;
    }
    
    public List<RoomRVOrder> getTodayRoomOrdersList() {
        return todayRoomOrdersList;
    }
    
    public void setTodayRoomOrdersList(List<RoomRVOrder> todayRoomOrdersList) {
        this.todayRoomOrdersList = todayRoomOrdersList;
    }

    public double getRoomOccupancyRate() {
        return roomOccupancyRate;
    }
    public void setRoomOccupancyRate(double roomOccupancyRate) {
        this.roomOccupancyRate = roomOccupancyRate;
    }
} 