package com.islevilla.chen.util.schedule;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.islevilla.chen.room.model.RoomService;
import com.islevilla.chen.room.model.RoomTypeCount;
import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeService;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailability;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailabilityId;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailabilityRepository;

import jakarta.annotation.PostConstruct;

@Component
public class DailyDataInsert {

    @Autowired
    private RoomTypeAvailabilityRepository roomTypeAvailabilityRepository;
    
    @Autowired
    private RoomTypeService roomTypeService;

    @Autowired
    private RoomService roomService;

    /**
     * 伺服器啟動後馬上執行一次
     */
    @PostConstruct
    public void init() {
    	//每日新增
        insertDailyData(); 
        
        //新增三個月
        LocalDate today = LocalDate.now();
        LocalDate threeMonthsLater = today.plusMonths(3);
        
        //取得房型總量
        List<RoomTypeCount> list = roomService.getRoomCountsByType();
        
        //每日庫存清單
        List<RoomTypeAvailability> dataList = new ArrayList<>();
        
            LocalDate date = today;
            while (!date.isAfter(threeMonthsLater)) {
            	//寫入每日庫存
            	for(int i=0 ; i<list.size() ; i++) {
            		RoomTypeAvailability daliyRoomTypeAvailability = new RoomTypeAvailability();
            		RoomTypeAvailabilityId roomTypeAvailabilityId = new RoomTypeAvailabilityId();
            		
            		RoomTypeCount rtc = list.get(i);
            		
            		// 獲取 RoomType 實體
            		RoomType roomType = roomTypeService.findById(rtc.getRoomTypeIdDTO());
            		if (roomType != null) {
                        roomTypeAvailabilityId.setRoomTypeId(rtc.getRoomTypeIdDTO());
                        roomTypeAvailabilityId.setRoomTypeAvailabilityDate(date);
                        
                        daliyRoomTypeAvailability.setRoomTypeAvailabilityId(roomTypeAvailabilityId);
                        daliyRoomTypeAvailability.setRoomType(roomType); // 設定 RoomType 實體
                        daliyRoomTypeAvailability.setRoomTypeAvailabilityCount(rtc.getRoomCountDTO());

                        dataList.add(daliyRoomTypeAvailability);
            		}
            	}
            	date = date.plusDays(1);
            }

            roomTypeAvailabilityRepository.saveAll(dataList);
            System.out.println("初始化完成，已建立 " + dataList.size() + " 筆資料");
    }

    /**
     * 每天早上 00:00 執行
     * cron 格式: 秒 分 時 日 月 星期
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void insertDailyData() {
    	
    	LocalDate newday = LocalDate.now();
    	
    	//取得房型總量、創建每日房型庫存
        List<RoomTypeCount> list = roomService.getRoomCountsByType();
    	List<RoomTypeAvailability> dataList = new ArrayList<>();
    	
    	for(int i=0 ; i<list.size() ; i++) {
    		RoomTypeAvailability daliyRoomTypeAvailability = new RoomTypeAvailability();
    		RoomTypeAvailabilityId roomTypeAvailabilityId = new RoomTypeAvailabilityId();
    		
    		RoomTypeCount rtc = list.get(i);
    		
    		// 獲取 RoomType 實體
    		RoomType roomType = roomTypeService.findById(rtc.getRoomTypeIdDTO());
    		if (roomType != null) {
                roomTypeAvailabilityId.setRoomTypeId(rtc.getRoomTypeIdDTO());
                roomTypeAvailabilityId.setRoomTypeAvailabilityDate(newday);
                
                daliyRoomTypeAvailability.setRoomTypeAvailabilityId(roomTypeAvailabilityId);
                daliyRoomTypeAvailability.setRoomType(roomType); // 設定 RoomType 實體
                daliyRoomTypeAvailability.setRoomTypeAvailabilityCount(rtc.getRoomCountDTO());

                dataList.add(daliyRoomTypeAvailability);
    		}
    	}

        System.out.println("已於每日排程新增資料 ");
    }
}
