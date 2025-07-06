package com.islevilla.patty.roompromotionprice.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service("roompromotionpriceService")
public class RoomPromotionPriceService {

	@Autowired
	RoomPromotionPriceRepository repository;
	
	@Autowired
    private SessionFactory sessionFactory;

	public void addRoomPromotionPrice(RoomPromotionPrice roomPromotionPrice) {
		repository.save(roomPromotionPrice);
	}

	public void updateRoomPromotionPrice(RoomPromotionPrice roomPromotionPrice) {
		repository.save(roomPromotionPrice);
	}
	
	public void deleteRoomPromotionPrice(Integer id) {
	    if (repository.existsById(id)) {
	        repository.deleteById(id);
	    } else {
	        throw new RuntimeException("找不到指定的優惠專案 ID：" + id);
	    }
	}

	



	public RoomPromotionPrice getOneRoomPromotionPrice(Integer roomPromotionId) {
		Optional<RoomPromotionPrice> optional = repository.findById(roomPromotionId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<RoomPromotionPrice> getAll() {
		return repository.findAll();
	}

}