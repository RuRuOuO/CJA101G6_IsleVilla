package com.islevilla.patty.roompromotionprice.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service("roomPromotionPriceService")
public class RoomPromotionPriceService {

	@Autowired
	RoomPromotionPriceRepository repository;
	
	@Autowired
    private SessionFactory sessionFactory;

	public void addPromotion(RoomPromotionPrice promotion) {
		repository.save(promotion);
	}

	public void updatePromotion(RoomPromotionPrice promotion) {
		repository.save(promotion);
	}
	
	public void deletePromotion(Integer id) {
	    if (repository.existsById(id)) {
	        repository.deleteById(id);
	    } else {
	        throw new RuntimeException("找不到指定的優惠專案 ID：" + id);
	    }
	}
}
//	
//
//
//
//	public Promotion getOnePromotion(Integer roomPromotionId) {
//		Optional<Promotion> optional = repository.findById(roomPromotionId);
////		return optional.get();
//		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
//	}
//
//	public List<Promotion> getAll() {
//		return repository.findAll();
//	}
//
//}