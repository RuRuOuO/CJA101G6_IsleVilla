package com.islevilla.patty.roompromotionprice.model;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.islevilla.patty.promotion.model.Promotion;
import com.islevilla.patty.promotion.model.PromotionRepository;



@Service("roompromotionpriceService")
public class RoomPromotionPriceService {

	@Autowired
	private RoomPromotionPriceRepository repository;
	
	@Autowired
    private SessionFactory sessionFactory;
	
	@Autowired
	private PromotionRepository promotionRepository;
	
	public List<RoomPromotionPrice> getAllWithDetails() {
        return repository.findAllWithDetails();
    }

	public void addRoomPromotionPrice(RoomPromotionPrice roomPromotionPrice) {
		repository.save(roomPromotionPrice);
	}

	public void updateRoomPromotionPrice(RoomPromotionPrice roomPromotionPrice) {
		repository.save(roomPromotionPrice);
	}

	/**
	 * 取得單一價格設定
	 */
	public RoomPromotionPrice getOneRoomPromotionPrice(RoomPromotionPriceId id) {
		return repository.findById(id).orElse(null);
	}

	public List<RoomPromotionPrice> getAllWithRoomTypeAndPromotion() {
	    return repository.findAllWithRoomTypeAndPromotion();
	}
	
	public List<RoomPromotionPrice> getAllWithDetailsSortedByPromotionId() {
	    List<RoomPromotionPrice> list = getAllWithDetails(); // 原本抓全部
	    return list.stream()
	               .sorted(Comparator.comparing(rpp -> rpp.getRoomPromotionId()))  // 按照 SQL 中的優惠專案編號排序
	               .collect(Collectors.toList());
	}

	/**
	 * 獲取去重後的優惠專案列表，每種專案只顯示一次
	 * @return 去重後的優惠專案列表
	 */
	public List<RoomPromotionPrice> getDistinctPromotions() {
	    List<RoomPromotionPrice> allPromotions = getAllWithDetails();
	    
	    // 先獲取所有有價格設定的優惠專案
	    List<RoomPromotionPrice> promotionsWithPrices = allPromotions.stream()
	               .filter(rpp -> rpp.getPromotion() != null) // 過濾掉 promotion 為 null 的資料
	               .collect(Collectors.toMap(
	                   rpp -> rpp.getRoomPromotionId(), // 以優惠專案ID作為key
	                   rpp -> rpp, // 值為 RoomPromotionPrice 物件
	                   (existing, replacement) -> existing // 如果有重複，保留第一個
	               ))
	               .values()
	               .stream()
	               .collect(Collectors.toList());
	    
	    // 獲取所有優惠專案（包括沒有價格設定的）
	    List<Promotion> allPromotionsFromPromotionTable = promotionRepository.findAll();
	    
	    // 為沒有價格設定的優惠專案創建空的 RoomPromotionPrice 物件
	    List<RoomPromotionPrice> promotionsWithoutPrices = allPromotionsFromPromotionTable.stream()
	        .filter(promotion -> promotionsWithPrices.stream()
	            .noneMatch(rpp -> rpp.getRoomPromotionId().equals(promotion.getRoomPromotionId())))
	        .map(promotion -> {
	            RoomPromotionPrice emptyRpp = new RoomPromotionPrice();
	            emptyRpp.setPromotion(promotion);
	            // 設定一個空的複合主鍵，只設定優惠專案ID
	            RoomPromotionPriceId emptyId = new RoomPromotionPriceId();
	            emptyId.setRoomPromotionId(promotion.getRoomPromotionId());
	            emptyId.setRoomTypeId(0); // 暫時設為0，表示沒有房型設定
	            emptyRpp.setId(emptyId);
	            return emptyRpp;
	        })
	        .collect(Collectors.toList());
	    
	    // 合併兩個列表並排序
	    List<RoomPromotionPrice> allPromotionsCombined = new java.util.ArrayList<>();
	    allPromotionsCombined.addAll(promotionsWithPrices);
	    allPromotionsCombined.addAll(promotionsWithoutPrices);
	    
	    return allPromotionsCombined.stream()
	        .sorted(Comparator.comparing(rpp -> rpp.getRoomPromotionId()))
	        .collect(Collectors.toList());
	}

	/**
	 * 獲取每個優惠專案的房型數量
	 * @return Map<優惠專案ID, 房型數量>
	 */
	public Map<Integer, Long> getPromotionRoomTypeCounts() {
	    List<RoomPromotionPrice> allPromotions = getAllWithDetails();
	    Map<Integer, Long> counts = allPromotions.stream()
	        .filter(rpp -> rpp.getPromotion() != null)
	        .collect(Collectors.groupingBy(
	            rpp -> rpp.getRoomPromotionId(),
	            Collectors.counting()
	        ));
	    
	    // 為沒有價格設定的優惠專案設定數量為0
	    List<Promotion> allPromotionsFromPromotionTable = promotionRepository.findAll();
	    for (Promotion promotion : allPromotionsFromPromotionTable) {
	        if (!counts.containsKey(promotion.getRoomPromotionId())) {
	            counts.put(promotion.getRoomPromotionId(), 0L);
	        }
	    }
	    
	    return counts;
	}

	/**
	 * 檢查是否已存在相同的優惠專案和房型組合
	 * @param roomPromotionId 優惠專案ID
	 * @param roomTypeId 房型ID
	 * @return true 如果已存在，false 如果不存在
	 */
	public boolean isDuplicate(Integer roomPromotionId, Integer roomTypeId) {
	    RoomPromotionPriceId id = new RoomPromotionPriceId();
	    id.setRoomPromotionId(roomPromotionId);
	    id.setRoomTypeId(roomTypeId);
	    return repository.existsById(id);
	}

	/**
	 * 刪除指定的價格設定
	 * @param id 複合主鍵
	 */
	public void deleteRoomPromotionPrice(RoomPromotionPriceId id) {
	    if (repository.existsById(id)) {
	        repository.deleteById(id);
	    } else {
	        throw new RuntimeException("找不到指定的價格設定");
	    }
	}

}