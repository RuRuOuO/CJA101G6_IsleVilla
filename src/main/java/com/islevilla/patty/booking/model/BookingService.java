package com.islevilla.patty.booking.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.islevilla.chen.room.model.Room;
import com.islevilla.chen.room.model.RoomRepository;
import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeRepository;
import com.islevilla.wei.room.model.RoomRVDetail;
import com.islevilla.wei.room.model.RoomRVOrder;
import com.islevilla.patty.roompromotionprice.model.RoomPromotionPriceRepository;
import com.islevilla.patty.roompromotionprice.model.RoomPromotionPrice;
import com.islevilla.chen.roomTypePhoto.model.RoomTypePhotoRepository;
import com.islevilla.chen.roomTypePhoto.model.RoomTypePhoto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailabilityService;

@Service
public class BookingService {

    
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private RoomPromotionPriceRepository roomPromotionPriceRepository;

    @Autowired
    private RoomTypePhotoRepository roomTypePhotoRepository;

    @Autowired
    private RoomTypeAvailabilityService roomTypeAvailabilityService;

    @PersistenceContext
    private EntityManager entityManager;

    public List<RoomType> findAvailableRoomTypes(LocalDate checkin, LocalDate checkout, int requiredRooms) {
        System.out.println("開始查詢空房...");
        System.out.println("入住日期: " + checkin);
        System.out.println("退房日期: " + checkout);
        System.out.println("需要房間數: " + requiredRooms);

        // 1. 找出指定日期區間內所有重疊的訂單
        List<RoomRVOrder> overlappingOrders = bookingRepository.findOverlappingOrders(
            checkin, 
            checkout
        );

        System.out.println("找到重疊訂單數量: " + overlappingOrders.size());

        if (overlappingOrders.isEmpty()) {
            // 如果沒有任何重疊訂單，代表所有房間都是空的
            System.out.println("沒有重疊訂單，所有房間皆可預訂");
            List<RoomType> allRoomTypes = roomTypeRepository.findAll();
            System.out.println("總房型數量: " + allRoomTypes.size());
            return allRoomTypes;
        }

        // 2. 根據重疊的訂單，找出所有被佔用的房間 ID
        List<RoomRVDetail> occupiedDetails = bookingRepository.findDetailsByOrders(overlappingOrders);
        List<Integer> occupiedRoomIds = occupiedDetails.stream()
                .map(detail -> detail.getRoom().getRoomId())
                .collect(Collectors.toList());
        
        System.out.println("被佔用的房間 ID: " + occupiedRoomIds);

        // 3. 找出所有未被佔用的空閒房間
        List<Room> availableRooms;
        if (occupiedRoomIds.isEmpty()) {
            availableRooms = roomRepository.findAll();
        } else {
            // 使用 JPA 標準方法：找出所有房間，然後在 Java 中過濾
            availableRooms = roomRepository.findAll().stream()
                    .filter(room -> !occupiedRoomIds.contains(room.getRoomId()))
                    .collect(Collectors.toList());
        }
        
        System.out.println("找到的空閒房間數量: " + availableRooms.size());
        System.out.println("空閒房間 ID: " + availableRooms.stream()
                .map(Room::getRoomId)
                .collect(Collectors.toList()));

        // 4. 根據空閒房間，統計可提供的房型
        Map<Integer, Long> roomTypeCounts = availableRooms.stream()
                .collect(Collectors.groupingBy(Room::getRoomTypeId, Collectors.counting()));
        
        System.out.println("可提供房型統計: " + roomTypeCounts);

        // 5. 找出有足夠空房的房型（數量 >= 需要的房間數）
        List<RoomType> availableRoomTypes = roomTypeCounts.entrySet().stream()
                .filter(entry -> entry.getValue() >= requiredRooms)
                .map(entry -> roomTypeRepository.findById(entry.getKey()).orElse(null))
                .filter(roomType -> roomType != null)
                .collect(Collectors.toList());

        System.out.println("符合需求的房型數量: " + availableRoomTypes.size());
        return availableRoomTypes;
    }

    public List<RoomType> findAvailableRoomTypesByCapacity(LocalDate checkin, LocalDate checkout, int requiredRooms, int totalAdults) {
        System.out.println("開始查詢空房（根據容納人數）...");
        System.out.println("入住日期: " + checkin);
        System.out.println("退房日期: " + checkout);
        System.out.println("需要房間數: " + requiredRooms);
        System.out.println("總大人數: " + totalAdults);

        // 1. 找出指定日期區間內所有重疊的訂單
        List<RoomRVOrder> overlappingOrders = bookingRepository.findOverlappingOrders(
            checkin, 
            checkout
        );

        System.out.println("找到重疊訂單數量: " + overlappingOrders.size());

        if (overlappingOrders.isEmpty()) {
            // 如果沒有任何重疊訂單，代表所有房間都是空的
            System.out.println("沒有重疊訂單，所有房間皆可預訂");
            List<RoomType> allRoomTypes = roomTypeRepository.findAll();
            // 根據容納人數篩選
            List<RoomType> filteredRoomTypes = allRoomTypes.stream()
                    .filter(roomType -> roomType.getRoomTypeCapacity() >= totalAdults)
                    .collect(Collectors.toList());
            System.out.println("總房型數量: " + allRoomTypes.size() + ", 符合容納人數的房型數量: " + filteredRoomTypes.size());
            return filteredRoomTypes;
        }

        // 2. 根據重疊的訂單，找出所有被佔用的房間 ID
        List<RoomRVDetail> occupiedDetails = bookingRepository.findDetailsByOrders(overlappingOrders);
        List<Integer> occupiedRoomIds = occupiedDetails.stream()
                .map(detail -> detail.getRoom().getRoomId())
                .collect(Collectors.toList());
        
        System.out.println("被佔用的房間 ID: " + occupiedRoomIds);

        // 3. 找出所有未被佔用的空閒房間
        List<Room> availableRooms;
        if (occupiedRoomIds.isEmpty()) {
            availableRooms = roomRepository.findAll();
        } else {
            // 使用 JPA 標準方法：找出所有房間，然後在 Java 中過濾
            availableRooms = roomRepository.findAll().stream()
                    .filter(room -> !occupiedRoomIds.contains(room.getRoomId()))
                    .collect(Collectors.toList());
        }
        
        System.out.println("找到的空閒房間數量: " + availableRooms.size());
        System.out.println("空閒房間 ID: " + availableRooms.stream()
                .map(Room::getRoomId)
                .collect(Collectors.toList()));

        // 4. 根據空閒房間，統計可提供的房型
        Map<Integer, Long> roomTypeCounts = availableRooms.stream()
                .collect(Collectors.groupingBy(Room::getRoomTypeId, Collectors.counting()));
        
        System.out.println("可提供房型統計: " + roomTypeCounts);

        // 5. 找出有足夠空房且容納人數足夠的房型
        List<RoomType> availableRoomTypes = roomTypeCounts.entrySet().stream()
                .filter(entry -> entry.getValue() >= requiredRooms)
                .map(entry -> roomTypeRepository.findById(entry.getKey()).orElse(null))
                .filter(roomType -> roomType != null && roomType.getRoomTypeCapacity() >= totalAdults)
                .collect(Collectors.toList());

        System.out.println("符合需求和容納人數的房型數量: " + availableRoomTypes.size());
        return availableRoomTypes;
    }

    public List<RoomType> findAvailableRoomTypesByRoomAdults(LocalDate checkin, LocalDate checkout, int requiredRooms, List<Integer> roomAdults) {
        System.out.println("開始查詢空房（根據各房間大人數）...");
        System.out.println("入住日期: " + checkin);
        System.out.println("退房日期: " + checkout);
        System.out.println("需要房間數: " + requiredRooms);
        System.out.println("各房間大人數: " + roomAdults);

        // 1. 找出指定日期區間內所有重疊的訂單
        List<RoomRVOrder> overlappingOrders = bookingRepository.findOverlappingOrders(
            checkin, 
            checkout
        );

        System.out.println("找到重疊訂單數量: " + overlappingOrders.size());

        if (overlappingOrders.isEmpty()) {
            // 如果沒有任何重疊訂單，代表所有房間都是空的
            System.out.println("沒有重疊訂單，所有房間皆可預訂");
            List<RoomType> allRoomTypes = roomTypeRepository.findAll();
            
            // 根據各房間大人數篩選房型
            List<RoomType> filteredRoomTypes = filterRoomTypesByAdults(allRoomTypes, roomAdults);
            System.out.println("總房型數量: " + allRoomTypes.size() + ", 符合各房間大人數的房型數量: " + filteredRoomTypes.size());
            return filteredRoomTypes;
        }

        // 2. 根據重疊的訂單，找出所有被佔用的房間 ID
        List<RoomRVDetail> occupiedDetails = bookingRepository.findDetailsByOrders(overlappingOrders);
        List<Integer> occupiedRoomIds = occupiedDetails.stream()
                .map(detail -> detail.getRoom().getRoomId())
                .collect(Collectors.toList());
        
        System.out.println("被佔用的房間 ID: " + occupiedRoomIds);

        // 3. 找出所有未被佔用的空閒房間
        List<Room> availableRooms;
        if (occupiedRoomIds.isEmpty()) {
            availableRooms = roomRepository.findAll();
        } else {
            // 使用 JPA 標準方法：找出所有房間，然後在 Java 中過濾
            availableRooms = roomRepository.findAll().stream()
                    .filter(room -> !occupiedRoomIds.contains(room.getRoomId()))
                    .collect(Collectors.toList());
        }
        
        System.out.println("找到的空閒房間數量: " + availableRooms.size());
        System.out.println("空閒房間 ID: " + availableRooms.stream()
                .map(Room::getRoomId)
                .collect(Collectors.toList()));

        // 4. 根據空閒房間，統計可提供的房型
        Map<Integer, Long> roomTypeCounts = availableRooms.stream()
                .collect(Collectors.groupingBy(Room::getRoomTypeId, Collectors.counting()));
        
        System.out.println("可提供房型統計: " + roomTypeCounts);

        // 5. 找出有足夠空房且符合各房間大人數需求的房型
        List<RoomType> availableRoomTypes = roomTypeCounts.entrySet().stream()
                .filter(entry -> entry.getValue() >= requiredRooms)
                .map(entry -> roomTypeRepository.findById(entry.getKey()).orElse(null))
                .filter(roomType -> roomType != null)
                .collect(Collectors.toList());

        // 6. 根據各房間大人數進一步篩選
        List<RoomType> finalRoomTypes = filterRoomTypesByAdults(availableRoomTypes, roomAdults);

        System.out.println("符合需求和各房間大人數的房型數量: " + finalRoomTypes.size());
        return finalRoomTypes;
    }

    // 根據各房間大人數篩選房型的輔助方法
    private List<RoomType> filterRoomTypesByAdults(List<RoomType> roomTypes, List<Integer> roomAdults) {
        List<RoomType> filtered = roomTypes.stream()
                .filter(roomType -> {
                    return roomAdults.stream().allMatch(adults -> {
                        int capacity = roomType.getRoomTypeCapacity();
                        if (adults <= 2) {
                            return capacity == 2 || capacity == 4 || capacity == 6;
                        } else if (adults <= 4) {
                            return capacity == 4 || capacity == 6;
                        } else {
                            return capacity == 6;
                        }
                    });
                })
                .collect(Collectors.toList());
        System.out.println("篩選後房型ID: " + filtered.stream().map(RoomType::getRoomTypeId).toList());
        return filtered;
    }

    public List<Room> findAvailableRooms(LocalDate checkin, LocalDate checkout, int requiredRooms) {
        System.out.println("開始查詢空房...");
        System.out.println("入住日期: " + checkin);
        System.out.println("退房日期: " + checkout);
        System.out.println("需要房間數: " + requiredRooms);

        // 1. 找出指定日期區間內所有重疊的訂單
        List<RoomRVOrder> overlappingOrders = bookingRepository.findOverlappingOrders(
            checkin, 
            checkout
        );

        System.out.println("找到重疊訂單數量: " + overlappingOrders.size());

        if (overlappingOrders.isEmpty()) {
            // 如果沒有任何重疊訂單，代表所有房間都是空的
            System.out.println("沒有重疊訂單，所有房間皆可預訂");
            List<Room> allRooms = roomRepository.findAll();
            System.out.println("總房間數量: " + allRooms.size());
            return allRooms;
        }

        // 2. 根據重疊的訂單，找出所有被佔用的房間 ID
        List<RoomRVDetail> occupiedDetails = bookingRepository.findDetailsByOrders(overlappingOrders);
        List<Integer> occupiedRoomIds = occupiedDetails.stream()
                .map(detail -> detail.getRoom().getRoomId())
                .collect(Collectors.toList());
        
        System.out.println("被佔用的房間 ID: " + occupiedRoomIds);

        // 3. 找出所有未被佔用的空閒房間
        List<Room> availableRooms;
        if (occupiedRoomIds.isEmpty()) {
            availableRooms = roomRepository.findAll();
        } else {
            // 使用 JPA 標準方法：找出所有房間，然後在 Java 中過濾
            availableRooms = roomRepository.findAll().stream()
                    .filter(room -> !occupiedRoomIds.contains(room.getRoomId()))
                    .collect(Collectors.toList());
        }
        
        System.out.println("找到的空閒房間數量: " + availableRooms.size());
        System.out.println("空閒房間 ID: " + availableRooms.stream()
                .map(Room::getRoomId)
                .collect(Collectors.toList()));

        return availableRooms;
    }

    public List<RoomTypeWithPromotions> findAvailableRoomTypesWithPromotions(LocalDate checkin, LocalDate checkout, int requiredRooms, List<Integer> roomAdults) {
        List<RoomType> roomTypes = findAvailableRoomTypesByRoomAdults(checkin, checkout, requiredRooms, roomAdults);
        List<RoomTypeWithPromotions> result = new java.util.ArrayList<>();
        for (RoomType roomType : roomTypes) {
            List<RoomPromotionPrice> promotions = roomPromotionPriceRepository.findAllValidPromotionPrices(roomType.getRoomTypeId(), checkin);
            // 過濾掉 promotion 為 null 的資料
            promotions = promotions.stream()
                .filter(promo -> promo.getPromotion() != null)
                .collect(Collectors.toList());
            result.add(new RoomTypeWithPromotions(roomType, promotions));
        }
        return result;
    }

    public List<RoomTypeWithPromotionsAndPhoto> findAvailableRoomTypesWithPromotionsAndPhoto(LocalDate checkin, LocalDate checkout, int requiredRooms, List<Integer> roomAdults) {
        List<RoomType> roomTypes = findAvailableRoomTypesByRoomAdults(checkin, checkout, requiredRooms, roomAdults);
        List<RoomTypeWithPromotionsAndPhoto> result = new java.util.ArrayList<>();
        for (RoomType roomType : roomTypes) {
            List<RoomPromotionPrice> promotions = roomPromotionPriceRepository.findAllValidPromotionPrices(roomType.getRoomTypeId(), checkin);
            // 過濾掉 promotion 為 null 的資料
            promotions = promotions.stream()
                .filter(promo -> promo.getPromotion() != null)
                .collect(Collectors.toList());
            
            // 轉換為 PromotionWithDiff 物件
            List<PromotionWithDiff> promotionWithDiffs = promotions.stream()
                .map(promo -> {
                    int originalPrice = roomType.getRoomTypePrice();
                    // 使用折扣率計算折扣後價格
                    int discountedPrice = (int) (originalPrice * promo.getRoomDiscountRate());
                    int diff = originalPrice - discountedPrice;
                    return new PromotionWithDiff(promo, diff, discountedPrice);
                })
                .collect(Collectors.toList());
            
            // 取得所有照片ID
            List<Integer> photoIds = roomTypePhotoRepository.findWithPhotos(roomType.getRoomTypeId())
                .stream().map(RoomTypePhoto::getRoomTypePhotoId).collect(Collectors.toList());
            
            RoomTypeWithPromotionsAndPhoto vo = new RoomTypeWithPromotionsAndPhoto(roomType, promotionWithDiffs, photoIds);
            // === 新增：查詢可用庫存 ===
            int availableCount = roomTypeAvailabilityService.calculateAvailableRoomsInRange(roomType.getRoomTypeId(), checkin, checkout);
            vo.setAvailableCount(availableCount);
            result.add(vo);
        }
        return result;
    }

    // JPQL 查詢 room_type_photo 第一張圖 id
    public Integer findFirstPhotoIdByRoomTypeId(int roomTypeId) {
        List<Integer> ids = entityManager.createQuery(
            "SELECT p.roomTypePhotoId FROM RoomTypePhoto p WHERE p.roomType.roomTypeId = :roomTypeId ORDER BY p.roomTypePhotoId ASC", Integer.class)
            .setParameter("roomTypeId", roomTypeId)
            .setMaxResults(1)
            .getResultList();
        return ids.isEmpty() ? null : ids.get(0);
    }

    public Booking createOrderAndReturnBooking(String guestName, String guestPhone, String guestEmail, String guestAddress, String specialRequests, String paymentMethod, java.util.Map bookingData, com.islevilla.lai.members.model.Members member) {
        // 解析 bookingData 取得必要資訊
        java.time.LocalDate checkin = java.time.LocalDate.parse((String)bookingData.get("checkin"));
        java.time.LocalDate checkout = java.time.LocalDate.parse((String)bookingData.get("checkout"));
        int adults = Integer.parseInt(bookingData.getOrDefault("adults", "1").toString());
        int children = Integer.parseInt(bookingData.getOrDefault("children", "0").toString());
        int totalPrice = Integer.parseInt(bookingData.getOrDefault("totalPrice", "0").toString());
        java.util.List<java.util.Map> selectedRooms = (java.util.List<java.util.Map>)bookingData.get("selectedRooms");

        // 建立 RoomRVOrder
        com.islevilla.wei.room.model.RoomRVOrder order = new com.islevilla.wei.room.model.RoomRVOrder();
        order.setMembers(member);
        order.setRoomOrderDate(java.time.LocalDateTime.now());
        order.setRoomOrderStatus(0); // 0:成立
        order.setCheckInDate(checkin);
        order.setCheckOutDate(checkout);
        order.setRemark(specialRequests);
        order.setRoomTotalAmount(totalPrice);
        order.setRvDiscountAmount(0); // 先預設 0
        order.setRvPaidAmount(totalPrice);
        // 處理優惠專案
        com.islevilla.patty.promotion.model.Promotion selectedPromotion = null;
        int totalDiscountAmount = 0;
        // 明細
        java.util.List<com.islevilla.wei.room.model.RoomRVDetail> details = new java.util.ArrayList<>();
        for (java.util.Map room : selectedRooms) {
            com.islevilla.wei.room.model.RoomRVDetail detail = new com.islevilla.wei.room.model.RoomRVDetail();
            Integer roomId = room.get("roomId") != null ? Integer.parseInt(room.get("roomId").toString()) : null;
            Integer roomTypeId = room.get("roomTypeId") != null ? Integer.parseInt(room.get("roomTypeId").toString()) : null;
            
            // 處理 promotionId，可能是空字串或 null
            Integer promotionId = null;
            Object promotionIdObj = room.get("promotionId");
            if (promotionIdObj != null && !promotionIdObj.toString().trim().isEmpty()) {
                try {
                    promotionId = Integer.parseInt(promotionIdObj.toString());
                } catch (NumberFormatException e) {
                    // 如果無法解析為整數，設為 null
                    promotionId = null;
                }
            }
            
            // 設置房型（必須）
            if (roomTypeId != null) {
                detail.setRoomType(roomTypeRepository.findById(roomTypeId).orElse(null));
            }
            
            // 設置具體房間（可選，原價預訂時可能沒有具體房間ID）
            // 注意：原價預訂時，roomId 實際上是 roomTypeId，所以需要檢查是否有 promotionId
            if (roomId != null && roomId > 0 && promotionId != null) {
                // 只有優惠價格預訂時才設置具體房間
                detail.setRoom(roomRepository.findById(roomId).orElse(null));
            }
            detail.setGuestCount(adults);
            int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkin, checkout);
            // 取得房型原價
            int roomTypePrice = 0;
            if (detail.getRoomType() != null && detail.getRoomType().getRoomTypePrice() != null) {
                roomTypePrice = detail.getRoomType().getRoomTypePrice();
            }
            int actualUnitPrice = Integer.parseInt(room.getOrDefault("price", "0").toString());
            // 原價 = 房型原價 x 夜數
            detail.setRoomPrice(roomTypePrice * nights);
            // 折扣金額 = (房型原價 - 實際單價) x 夜數
            int discountAmount = (roomTypePrice - actualUnitPrice) * nights;
            detail.setRvDiscountAmount(discountAmount > 0 ? discountAmount : 0);
            // 實付金額 = 實際單價 x 夜數
            detail.setRvPaidAmount(actualUnitPrice * nights);
            detail.setRoomRVOrder(order);
            details.add(detail);

            if (promotionId != null) {
                com.islevilla.patty.promotion.model.Promotion promotion = 
                    entityManager.find(com.islevilla.patty.promotion.model.Promotion.class, promotionId);
                if (promotion != null) {
                    selectedPromotion = promotion;
                    if (detail.getRoomType() != null) {
                        int originalPrice = detail.getRoomType().getRoomTypePrice();
                        discountAmount = (originalPrice - actualUnitPrice) * nights;
                        detail.setRvDiscountAmount(discountAmount > 0 ? discountAmount : 0);
                    }
                    // 設定 promotionTitle
                    try {
                        java.lang.reflect.Method setPromotionTitle = detail.getClass().getMethod("setPromotionTitle", String.class);
                        setPromotionTitle.invoke(detail, promotion.getRoomPromotionTitle());
                    } catch(Exception e) {
                        // RoomRVDetail 沒有 promotionTitle 屬性就略過
                    }
                }
            }
        }
        if (selectedPromotion != null) {
            order.setPromotion(selectedPromotion);
            order.setRvDiscountAmount(totalDiscountAmount);
        }
        order.setRoomRVDetails(details);
        bookingRepository.save(order);
        for (com.islevilla.wei.room.model.RoomRVDetail detail : details) {
            Integer roomTypeId = detail.getRoomType().getRoomTypeId();
            java.time.LocalDate checkinDate = order.getCheckInDate();
            java.time.LocalDate checkoutDate = order.getCheckOutDate();
            java.time.LocalDate current = checkinDate;
            while (current.isBefore(checkoutDate)) {
                roomTypeAvailabilityService.decreaseAvailability(roomTypeId, current, 1);
                current = current.plusDays(1);
            }
        }
        // 封裝Booking物件回傳
        Booking booking = new Booking();
        booking.setId(order.getRoomReservationId() != null ? order.getRoomReservationId().longValue() : null);
        booking.setCheckInDate(checkin);
        booking.setCheckOutDate(checkout);
        booking.setRoomType(selectedRooms.size() > 0 && selectedRooms.get(0).get("name") != null ? selectedRooms.get(0).get("name").toString() : "");
        booking.setRoomCount(selectedRooms.size());
        booking.setTotalAmount(totalPrice);
        booking.setEmail(guestEmail);
        booking.setRemark(specialRequests);
        // 訂房明細
        java.util.List<Booking.Detail> detailList = new java.util.ArrayList<>();
        int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkin, checkout);
        for (java.util.Map room : selectedRooms) {
            Booking.Detail d = new Booking.Detail();
            d.setRoomTypeName(room.get("name") != null ? room.get("name").toString() : "");
            d.setPromotionTitle(room.get("promotionTitle") != null ? room.get("promotionTitle").toString() : "");
            int unitPrice = room.get("price") != null ? Integer.parseInt(room.get("price").toString()) : 0;
            d.setPrice(unitPrice * nights); // 直接存小計
            detailList.add(d);
        }
        booking.setDetails(detailList);
        // 新增房型x間數 summary
        java.util.Map<String, Integer> roomTypeCountMap = new java.util.LinkedHashMap<>();
        for (java.util.Map room : selectedRooms) {
            String name = room.get("name") != null ? room.get("name").toString() : "";
            roomTypeCountMap.put(name, roomTypeCountMap.getOrDefault(name, 0) + 1);
        }
        StringBuilder summary = new StringBuilder();
        for (java.util.Map.Entry<String, Integer> entry : roomTypeCountMap.entrySet()) {
            if (summary.length() > 0) summary.append("、");
            summary.append(entry.getKey()).append(" x").append(entry.getValue());
        }
        booking.setRoomTypeSummary(summary.toString());
        return booking;
    }

    public static class RoomTypeWithPromotions {
        private RoomType roomType;
        private List<RoomPromotionPrice> promotions;
        public RoomTypeWithPromotions(RoomType roomType, List<RoomPromotionPrice> promotions) {
            this.roomType = roomType;
            this.promotions = promotions;
        }
        public RoomType getRoomType() { return roomType; }
        public List<RoomPromotionPrice> getPromotions() { return promotions; }
    }

    public static class RoomTypeWithPromotionsAndPhoto {
        private RoomType roomType;
        private List<PromotionWithDiff> promotions;
        private List<Integer> photoIds;
        private int availableCount; // 新增
        public RoomTypeWithPromotionsAndPhoto(RoomType roomType, List<PromotionWithDiff> promotions, List<Integer> photoIds) {
            this.roomType = roomType;
            this.promotions = promotions;
            this.photoIds = photoIds;
            this.availableCount = 0; // 預設 0，可後續 set
        }
        public RoomType getRoomType() { return roomType; }
        public List<PromotionWithDiff> getPromotions() { return promotions; }
        public List<Integer> getPhotoIds() { return photoIds; }
        public int getAvailableCount() { return availableCount; }
        public void setAvailableCount(int availableCount) { this.availableCount = availableCount; }
    }

    public static class PromotionWithDiff {
        private RoomPromotionPrice promotion;
        private int diff;
        private int discountedPrice;
        
        public PromotionWithDiff(RoomPromotionPrice promotion, int diff, int discountedPrice) {
            this.promotion = promotion;
            this.diff = diff;
            this.discountedPrice = discountedPrice;
        }
        
        public RoomPromotionPrice getPromotion() { return promotion; }
        public int getDiff() { return diff; }
        public int getDiscountedPrice() { return discountedPrice; }
    }
} 