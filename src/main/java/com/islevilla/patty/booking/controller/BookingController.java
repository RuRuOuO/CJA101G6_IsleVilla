package com.islevilla.patty.booking.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.islevilla.patty.booking.model.BookingService;
import com.islevilla.chen.roomTypePhoto.model.RoomTypePhotoService;
import com.islevilla.chen.roomTypePhoto.model.RoomTypePhoto;
import com.islevilla.chen.roomType.model.RoomTypeService;
import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailabilityService;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailability;
import com.islevilla.patty.roompromotionprice.model.RoomPromotionPriceRepository;
import com.islevilla.patty.roompromotionprice.model.RoomPromotionPrice;
import jakarta.servlet.http.HttpSession;

@Controller
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RoomTypePhotoService roomTypePhotoService;
    
    @Autowired
    private RoomTypeService roomTypeService;
    
    @Autowired
    private RoomTypeAvailabilityService roomTypeAvailabilityService;
    
    @Autowired
    private RoomPromotionPriceRepository roomPromotionPriceRepository;

    @GetMapping("/booking")
    public String showBookingPage() {
        return "front-end/booking/online-booking";
    }

    @GetMapping("/booking/form")
    public String showBookingForm(jakarta.servlet.http.HttpSession session) {
        Object member = session.getAttribute("member");
        if (member == null) {
            return "redirect:/member/login?redirect=/booking/form";
        }

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

        // 使用 chen 的房型資料和空房查詢，並加入優惠專案查詢
        try {
            // 1. 查詢所有上架中的房型
            List<RoomType> allRoomTypes = roomTypeService.findByRoomTypeSaleStatus((byte) 1);
            System.out.println("查詢到房型數量: " + allRoomTypes.size());
            
            // 依大人人數過濾房型（room_type_capacity >= adults）
            int adults = roomAdults.stream().mapToInt(Integer::intValue).max().orElse(1);
            List<RoomType> filteredRoomTypes = allRoomTypes.stream()
                .filter(rt -> rt.getRoomTypeCapacity() != null && rt.getRoomTypeCapacity() >= adults)
                .toList();
            System.out.println("依大人人數過濾後房型數量: " + filteredRoomTypes.size());
            
            // 2. 為每個房型查詢當天有效的優惠專案，並檢查空房狀態
            List<RoomTypeWithPromotions> roomTypesWithPromotions = new ArrayList<>();
            
            for (RoomType roomType : filteredRoomTypes) {
                System.out.println("處理房型: " + roomType.getRoomTypeId() + " - " + roomType.getRoomTypeName());
                
                Integer roomTypeId = roomType.getRoomTypeId();
                Integer requiredCount = roomCount; // 需要的房間數
                
                // 查詢該房型在入住日期有效的優惠專案
                List<RoomPromotionPrice> promotions = new ArrayList<>();
                try {
                    promotions = roomPromotionPriceRepository.findAllValidPromotionPrices(
                        roomType.getRoomTypeId(), checkin);
                    
                    // 過濾掉 promotion 為 null 的資料
                    promotions = promotions.stream()
                        .filter(promo -> promo.getPromotion() != null)
                        .collect(java.util.stream.Collectors.toList());
                } catch (Exception e) {
                    System.err.println("查詢房型 " + roomType.getRoomTypeName() + " 優惠專案時發生錯誤: " + e.getMessage());
                    promotions = new ArrayList<>();
                }
                
                // 查詢第一張圖片ID
                Integer firstPhotoId = null;
                try {
                    List<RoomTypePhoto> photos = roomTypePhotoService.roomTypeFindPhotos(roomTypeId);
                    if (photos != null && !photos.isEmpty()) {
                        firstPhotoId = photos.get(0).getRoomTypePhotoId();
                    }
                } catch (Exception e) {
                    System.err.println("查詢房型 " + roomType.getRoomTypeName() + " 圖片時發生錯誤: " + e.getMessage());
                }
                
                // 檢查在入住期間是否有足夠的空房
                boolean hasAvailableRooms = false;
                try {
                    hasAvailableRooms = roomTypeAvailabilityService.hasAvailableRoomsInRange(
                        roomTypeId, checkin, checkout, requiredCount);
                } catch (Exception e) {
                    System.err.println("檢查房型 " + roomType.getRoomTypeName() + " 空房時發生錯誤: " + e.getMessage());
                    hasAvailableRooms = false; // 預設為無空房
                }
                
                System.out.println("房型 " + roomType.getRoomTypeName() + " 有 " + promotions.size() + " 個有效優惠專案，空房狀態: " + (hasAvailableRooms ? "有空房" : "無空房"));
                
                roomTypesWithPromotions.add(new RoomTypeWithPromotions(roomType, promotions, hasAvailableRooms, firstPhotoId));
            }
            
            System.out.println("最終處理的房型數量: " + roomTypesWithPromotions.size());
            
            // 將查詢結果加入 model，使用 roomTypes 變數名稱
            model.addAttribute("roomTypes", roomTypesWithPromotions);
            
        } catch (Exception e) {
            System.err.println("查詢房型或空房失敗: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("roomTypes", List.of());
        }
        
        // 計算入住天數
        int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkin, checkout);
        model.addAttribute("nights", nights);

        model.addAttribute("checkin", checkin);
        model.addAttribute("checkout", checkout);
        model.addAttribute("roomCount", roomCount);
        model.addAttribute("roomAdults", roomAdults);
        model.addAttribute("children", children);

        return "front-end/booking/online-booking";
    }

    // 內部類別：房型與優惠專案的組合
    public static class RoomTypeWithPromotions {
        private RoomType roomType;
        private List<RoomPromotionPrice> promotions;
        private boolean hasAvailableRooms;
        private Integer firstPhotoId;
        
        public RoomTypeWithPromotions(RoomType roomType, List<RoomPromotionPrice> promotions, boolean hasAvailableRooms, Integer firstPhotoId) {
            this.roomType = roomType;
            this.promotions = promotions;
            this.hasAvailableRooms = hasAvailableRooms;
            this.firstPhotoId = firstPhotoId;
        }
        public RoomTypeWithPromotions(RoomType roomType, List<RoomPromotionPrice> promotions, boolean hasAvailableRooms) {
            this(roomType, promotions, hasAvailableRooms, null);
        }
        public RoomType getRoomType() { return roomType; }
        public List<RoomPromotionPrice> getPromotions() { return promotions; }
        public boolean hasAvailableRooms() { return hasAvailableRooms; }
        public Integer getFirstPhotoId() { return firstPhotoId; }
        
        // 計算折扣後價格的輔助方法
        public int getDiscountedPrice(RoomPromotionPrice promotion) {
            if (promotion.getRoomDiscountRate() != null) {
                return (int) (roomType.getRoomTypePrice() * promotion.getRoomDiscountRate());
            }
            return roomType.getRoomTypePrice();
        }
        
        // 計算折扣金額的輔助方法
        public int getDiscountAmount(RoomPromotionPrice promotion) {
            return roomType.getRoomTypePrice() - getDiscountedPrice(promotion);
        }
        
        // 獲取空房狀態文字
        public String getAvailabilityStatus() {
            return hasAvailableRooms ? "有空房" : "無空房";
        }
        
        // 獲取空房狀態的 CSS 類別
        public String getAvailabilityStatusClass() {
            return hasAvailableRooms ? "text-success" : "text-danger";
        }
    }

    /**
     * 查詢 chen 的房型資料
     */
    @GetMapping("/booking/chen/roomTypes")
    public String getRoomTypes(Model model) {
        try {
            // 查詢所有上架中的房型 (saleStatus = 1)
            List<RoomType> roomTypes = roomTypeService.findByRoomTypeSaleStatus((byte) 1);
            model.addAttribute("basicRoomTypes", roomTypes);
            System.out.println("查詢到房型數量: " + roomTypes.size());
        } catch (Exception e) {
            System.err.println("查詢房型失敗: " + e.getMessage());
            model.addAttribute("basicRoomTypes", List.of());
        }
        return "front-end/booking/online-booking";
    }

    /**
     * 根據房型ID查詢 chen 的單一房型資料
     */
    @GetMapping("/booking/chen/roomType/{roomTypeId}")
    public ResponseEntity<RoomType> getRoomTypeById(@PathVariable Integer roomTypeId) {
        try {
            RoomType roomType = roomTypeService.findById(roomTypeId);
            if (roomType != null) {
                return ResponseEntity.ok(roomType);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("查詢房型失敗: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 複合查詢 chen 的房型資料
     */
    @GetMapping("/booking/chen/roomTypes/search")
    public ResponseEntity<List<RoomType>> searchRoomTypes(
            @RequestParam(value = "roomTypeId", required = false) Integer roomTypeId,
            @RequestParam(value = "roomTypeSaleStatus", required = false) Byte roomTypeSaleStatus) {
        try {
            List<RoomType> roomTypes = roomTypeService.compoundQuery(roomTypeId, roomTypeSaleStatus);
            return ResponseEntity.ok(roomTypes);
        } catch (Exception e) {
            System.err.println("複合查詢房型失敗: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 查詢 chen 的空房資料
     */
    @GetMapping("/booking/chen/availability")
    public ResponseEntity<List<RoomTypeAvailability>> getAvailability(
            @RequestParam(value = "roomTypeId", required = false) Integer roomTypeId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate endDate) {
        try {
            List<RoomTypeAvailability> availability;
            if (roomTypeId != null && startDate != null && endDate != null) {
                // 查詢特定房型在日期範圍內的空房
                availability = roomTypeAvailabilityService.findByRoomTypeIdAndDateRange(roomTypeId, startDate, endDate);
            } else if (startDate != null && endDate != null) {
                // 查詢日期範圍內所有房型的空房
                availability = roomTypeAvailabilityService.findByDateRange(startDate, endDate);
            } else if (roomTypeId != null) {
                // 查詢特定房型的所有空房記錄
                availability = roomTypeAvailabilityService.findByRoomTypeId(roomTypeId);
            } else {
                // 查詢所有空房記錄
                availability = roomTypeAvailabilityService.findAll();
            }
            return ResponseEntity.ok(availability);
        } catch (Exception e) {
            System.err.println("查詢空房失敗: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 檢查特定房型在指定日期範圍內是否有足夠空房
     */
    @GetMapping("/booking/chen/availability/check")
    public ResponseEntity<Map<String, Object>> checkAvailability(
            @RequestParam("roomTypeId") Integer roomTypeId,
            @RequestParam("startDate") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate endDate,
            @RequestParam("requiredCount") Integer requiredCount) {
        try {
            boolean hasAvailableRooms = roomTypeAvailabilityService.hasAvailableRoomsInRange(
                roomTypeId, startDate, endDate, requiredCount);
            
            Map<String, Object> result = new HashMap<>();
            result.put("hasAvailableRooms", hasAvailableRooms);
            result.put("roomTypeId", roomTypeId);
            result.put("startDate", startDate);
            result.put("endDate", endDate);
            result.put("requiredCount", requiredCount);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("檢查空房失敗: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/booking/submit")
    public String submitBooking(
            @RequestParam("guestName") String guestName,
            @RequestParam("guestPhone") String guestPhone,
            @RequestParam("guestEmail") String guestEmail,
            @RequestParam(value = "guestAddress", required = false) String guestAddress,
            @RequestParam(value = "specialRequests", required = false) String specialRequests,
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam("bookingData") String bookingDataJson,
            HttpSession session,
            Model model) {
        try {
            // 解析 bookingData
            ObjectMapper mapper = new ObjectMapper();
            var bookingData = mapper.readValue(bookingDataJson, java.util.Map.class);
            // 取得會員
            var member = session.getAttribute("member");
            if (member == null) {
                model.addAttribute("message", "請先登入會員");
                return "front-end/booking/booking-success";
            }
            // 建立訂單
            bookingService.createOrder(guestName, guestPhone, guestEmail, guestAddress, specialRequests, paymentMethod, bookingData, (com.islevilla.lai.members.model.Members)member);
            model.addAttribute("message", "訂房成功！我們會盡快與您聯繫確認。");
            return "front-end/booking/booking-success";
        } catch (Exception e) {
            model.addAttribute("message", "訂房失敗：" + e.getMessage());
            return "front-end/booking/booking-success";
        }
    }

    @GetMapping("/booking/roomTypePhoto/image/{roomTypePhotoId}")
    public ResponseEntity<byte[]> getRoomTypePhotoImageForBooking(@PathVariable Integer roomTypePhotoId) {
        RoomTypePhoto roomTypePhoto = roomTypePhotoService.findById(roomTypePhotoId);
        if (roomTypePhoto != null && roomTypePhoto.getRoomTypePhoto() != null) {
            Byte[] imageBytes = roomTypePhoto.getRoomTypePhoto();
            byte[] primitiveBytes = new byte[imageBytes.length];
            for (int i = 0; i < imageBytes.length; i++) {
                primitiveBytes[i] = imageBytes[i];
            }
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .body(primitiveBytes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

} 