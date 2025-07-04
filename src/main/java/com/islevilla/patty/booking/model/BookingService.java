package com.islevilla.patty.booking.model;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeRepository;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailability;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailabilityRepository;
import com.islevilla.wei.room.model.RoomRVOrder;
import com.islevilla.wei.room.model.RoomRVOrderRepository;
import com.islevilla.wei.room.model.RoomRVDetail;
import com.islevilla.wei.room.model.RoomRVDetailRepository;
import com.islevilla.chen.room.model.RoomRepository;
import com.islevilla.chen.room.model.Room;
import com.islevilla.lai.members.model.Members;
import com.islevilla.patty.roompromotionprice.model.RoomPromotionPrice;
import com.islevilla.patty.roompromotionprice.model.RoomPromotionPriceRepository;

@Service
public class BookingService {

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private RoomTypeAvailabilityRepository roomTypeAvailabilityRepository;

    @Autowired
    private RoomRVOrderRepository roomRVOrderRepository;

    @Autowired
    private RoomRVDetailRepository roomRVDetailRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomPromotionPriceRepository roomPromotionPriceRepository;

    /**
     * 根據入住日、所需房間數、每間房大人人數，回傳可用房型（超過2大人不顯示雙人房，以此類推）
     */
    public List<RoomType> findAvailableRoomTypesByRoomAdultsWithAvailability(LocalDate checkin, int requiredRooms, List<Integer> roomAdults) {
        List<RoomType> allRoomTypes = roomTypeRepository.findAll();
        java.util.Date checkinDate = java.sql.Date.valueOf(checkin);
        // 先依大人人數過濾房型
        List<RoomType> filteredRoomTypes = allRoomTypes.stream()
            .filter(roomType -> roomAdults.stream().allMatch(adults -> roomType.getRoomTypeCapacity() >= adults))
            .collect(Collectors.toList());
        // 再依可用數量過濾
        List<RoomType> availableRoomTypes = filteredRoomTypes.stream()
            .filter(roomType -> {
                RoomTypeAvailability ava = roomTypeAvailabilityRepository.findByRoomTypeIdAndRoomTypeAvailabilityDate(roomType.getRoomTypeId(), checkinDate);
                return ava != null && ava.getRoomTypeAvailabilityCount() >= requiredRooms;
            })
            .collect(Collectors.toList());
        return availableRoomTypes;
    }

    /**
     * DTO: 房型+所有促銷方案+原價
     */
    public static class PromotionOption {
        public Integer promotionId; // null 代表原價
        public String title; // 促銷名稱或"原價"
        public int price; // 折扣價或原價
        public boolean isOriginal;
        public PromotionOption(Integer promotionId, String title, int price, boolean isOriginal) {
            this.promotionId = promotionId;
            this.title = title;
            this.price = price;
            this.isOriginal = isOriginal;
        }
    }
    public static class RoomTypeWithPromotionsAndOriginal {
        public RoomType roomType;
        public List<PromotionOption> promotions;
        public RoomTypeWithPromotionsAndOriginal(RoomType roomType, List<PromotionOption> promotions) {
            this.roomType = roomType;
            this.promotions = promotions;
        }
    }

    /**
     * 查詢所有可用房型，並組合原價+所有促銷方案
     */
    public List<RoomTypeWithPromotionsAndOriginal> findAvailableRoomTypesWithPromotionsAndOriginal(LocalDate checkin, int requiredRooms, List<Integer> roomAdults) {
        List<RoomType> availableRoomTypes = findAvailableRoomTypesByRoomAdultsWithAvailability(checkin, requiredRooms, roomAdults);
        List<RoomTypeWithPromotionsAndOriginal> result = new ArrayList<>();
        for (RoomType roomType : availableRoomTypes) {
            List<PromotionOption> options = new ArrayList<>();
            // 原價
            options.add(new PromotionOption(null, "原價", roomType.getRoomTypePrice(), true));
            // 查詢所有促銷
            List<RoomPromotionPrice> promos = roomPromotionPriceRepository.findAllValidPromotionPrices(roomType.getRoomTypeId(), checkin);
            if (promos != null) {
                for (RoomPromotionPrice promo : promos) {
                    if (promo.getRoomDiscountRate() != null) {
                        int discounted = (int)Math.round(roomType.getRoomTypePrice() * promo.getRoomDiscountRate());
                        String title = promo.getPromotion() != null ? promo.getPromotion().getRoomPromotionTitle() : "促銷專案";
                        options.add(new PromotionOption(promo.getRoomPromotionId(), title, discounted, false));
                    }
                }
            }
            result.add(new RoomTypeWithPromotionsAndOriginal(roomType, options));
        }
        return result;
    }

    /**
     * 建立訂單（簡化版，僅示範核心欄位，請依實際需求擴充）
     */
    public void createOrder(String guestName, String guestPhone, String guestEmail, String guestAddress, String specialRequests, String paymentMethod, Map bookingData, Members member) {
        try {
            // 解析 bookingData
            LocalDate checkin = LocalDate.parse((String)bookingData.get("checkin"));
            LocalDate checkout = LocalDate.parse((String)bookingData.get("checkout"));
            int totalPrice = Integer.parseInt(bookingData.getOrDefault("totalPrice", "0").toString());
            List<Map> selectedRooms = (List<Map>)bookingData.get("selectedRooms");

            // 建立 RoomRVOrder
            RoomRVOrder order = new RoomRVOrder();
            order.setMembers(member);
            order.setRoomOrderDate(LocalDate.now());
            order.setRoomOrderStatus(0); // 0:成立
            order.setCheckInDate(checkin);
            order.setCheckOutDate(checkout);
            order.setRemark(specialRequests);
            order.setRoomTotalAmount(totalPrice);
            order.setRvDiscountAmount(0); // 先預設 0
            order.setRvPaidAmount(totalPrice);

            List<RoomRVDetail> details = new ArrayList<>();
            for (Map room : selectedRooms) {
                RoomRVDetail detail = new RoomRVDetail();
                Integer roomId = room.get("roomId") != null ? Integer.parseInt(room.get("roomId").toString()) : null;
                Integer roomTypeId = room.get("roomTypeId") != null ? Integer.parseInt(room.get("roomTypeId").toString()) : null;
                Integer promotionId = null;
                if (room.get("promotionId") != null && !room.get("promotionId").toString().equals("null")) {
                    promotionId = Integer.parseInt(room.get("promotionId").toString());
                }
                if (roomId != null) {
                    Optional<Room> optRoom = roomRepository.findById(roomId);
                    optRoom.ifPresent(detail::setRoom);
                }
                if (roomTypeId != null) {
                    Optional<RoomType> optRoomType = roomTypeRepository.findById(roomTypeId);
                    optRoomType.ifPresent(detail::setRoomType);
                }
                detail.setGuestCount(Integer.parseInt(room.getOrDefault("adults", "1").toString()));
                int originalPrice = Integer.parseInt(room.getOrDefault("price", "0").toString());
                // 根據 promotionId 決定價格
                if (promotionId != null) {
                    RoomPromotionPrice promo = roomPromotionPriceRepository.findById(promotionId).orElse(null);
                    if (promo != null && promo.getRoomDiscountRate() != null) {
                        int discounted = (int)Math.round(originalPrice * promo.getRoomDiscountRate());
                        detail.setRoomPrice(originalPrice);
                        detail.setRvDiscountAmount(originalPrice - discounted);
                        detail.setRvPaidAmount(discounted);
                        detail.setRemark("促銷:" + (promo.getPromotion() != null ? promo.getPromotion().getRoomPromotionTitle() : "") + ", 折扣價:" + discounted);
                        System.out.println("[createOrder] 房型 " + roomTypeId + " 選擇促銷，原價:" + originalPrice + ", 折扣價:" + discounted);
                    } else {
                        detail.setRoomPrice(originalPrice);
                        detail.setRvDiscountAmount(0);
                        detail.setRvPaidAmount(originalPrice);
                        detail.setRemark("促銷資料異常，已用原價");
                    }
                } else {
                    detail.setRoomPrice(originalPrice);
                    detail.setRvDiscountAmount(0);
                    detail.setRvPaidAmount(originalPrice);
                    detail.setRemark("原價");
                }
                detail.setRoomRVOrder(order);
                details.add(detail);
            }
            order.setRoomRVDetails(details);
            roomRVOrderRepository.save(order); // cascade 寫入明細
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("訂單寫入失敗: " + e.getMessage());
        }
    }
} 