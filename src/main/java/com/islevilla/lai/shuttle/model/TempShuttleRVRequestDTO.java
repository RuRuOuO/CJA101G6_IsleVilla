package com.islevilla.lai.shuttle.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TempShuttleRVRequestDTO {
	private Integer id; // 預約請求ID (用於流程中的資料保存)
    
    @NotNull(message = "請輸入會員編號")
    @Min(value = 1, message = "會員編號必須大於0")
    private Integer memberId;
    
    @NotNull(message = "請輸入訂房編號！")
    @Min(value = 1, message = "訂房編號必須大於0")
    private Integer roomReservationId;
    
    @NotNull(message = "請選擇接駁日期")
    @Future(message = "接駁日期必須是未來日期")
    private LocalDate shuttleDate;
    
    @NotNull(message = "請選擇接駁人數")
    @Min(value = 1, message = "接駁人數不能少於1人")
    @Max(value = 6, message = "接駁人數不能超過6人")
    private Integer shuttleNumber;
    
    @NotNull(message = "請選擇接駁方向")
    @Min(value = 0, message = "接駁方向值錯誤")
    @Max(value = 1, message = "接駁方向值錯誤")
    private Integer shuttleDirection; // 0:去程 1:回程
    
    // 以下欄位在流程中會被設定
    private Integer selectedScheduleId;
    private List<Integer> selectedSeatIds;

}
