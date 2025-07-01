package com.islevilla.patty.booking.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.islevilla.patty.booking.model.BookingService;

@Controller
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/booking")
    public String showBookingPage() {
        return "front-end/booking/online-booking";
    }

    @GetMapping("/booking/form")
    public String showBookingForm() {
        return "front-end/booking/booking-form";
    }

    @PostMapping("/booking/search")
    public String searchAvailableRooms(
            @RequestParam("checkin") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate checkin,
            @RequestParam("checkout") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate checkout,
            @RequestParam("roomCount") int roomCount,
            @RequestParam(value = "roomAdults", required = false) String roomAdultsJson,
            @RequestParam(value = "children", required = false) Integer children,
            Model model) {

        System.out.println("接收到查詢請求:");
        System.out.println("入住日期: " + checkin);
        System.out.println("退房日期: " + checkout);
        System.out.println("需要房間數: " + roomCount);
        System.out.println("房間大人數: " + roomAdultsJson);
        System.out.println("孩童數: " + children);

        List<Integer> roomAdults = null;
        if (roomAdultsJson != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                roomAdults = mapper.readValue(roomAdultsJson, new TypeReference<List<Integer>>() {});
                System.out.println("解析後的房間大人數: " + roomAdults);
            } catch (Exception e) {
                System.err.println("解析房間大人數失敗: " + e.getMessage());
                roomAdults = List.of(2); // 預設每間房2位成人
            }
        } else {
            // 沒有帶參數，預設一間房2位成人
            roomAdults = List.of(2);
        }

        // 查詢可用房型（即使查無資料也會回傳空集合）
        var availableRoomTypesWithPromotionsAndPhoto = bookingService.findAvailableRoomTypesWithPromotionsAndPhoto(checkin, checkout, roomCount, roomAdults);
        System.out.println("查詢結果房型數量: " + (availableRoomTypesWithPromotionsAndPhoto != null ? availableRoomTypesWithPromotionsAndPhoto.size() : "null"));

        // 將查詢結果加入 model
        model.addAttribute("availableRooms", availableRoomTypesWithPromotionsAndPhoto != null ? availableRoomTypesWithPromotionsAndPhoto : List.of());
        model.addAttribute("checkin", checkin);
        model.addAttribute("checkout", checkout);
        model.addAttribute("roomCount", roomCount);
        model.addAttribute("roomAdults", roomAdults);
        model.addAttribute("children", children);

        return "front-end/booking/online-booking";
    }

    @PostMapping("/booking/submit")
    public String submitBooking(
            @RequestParam("guestName") String guestName,
            @RequestParam("guestPhone") String guestPhone,
            @RequestParam("guestEmail") String guestEmail,
            @RequestParam("guestIdNumber") String guestIdNumber,
            @RequestParam(value = "guestAddress", required = false) String guestAddress,
            @RequestParam(value = "specialRequests", required = false) String specialRequests,
            @RequestParam("paymentMethod") String paymentMethod,
            Model model) {
        
        // 這裡可以處理訂房資料的儲存邏輯
        System.out.println("收到訂房資料:");
        System.out.println("訂房人: " + guestName);
        System.out.println("電話: " + guestPhone);
        System.out.println("Email: " + guestEmail);
        System.out.println("身分證: " + guestIdNumber);
        System.out.println("地址: " + guestAddress);
        System.out.println("特殊需求: " + specialRequests);
        System.out.println("付款方式: " + paymentMethod);
        
        // 可以加入成功訊息
        model.addAttribute("message", "訂房成功！我們會盡快與您聯繫確認。");
        
        return "front-end/booking/booking-success";
    }

} 