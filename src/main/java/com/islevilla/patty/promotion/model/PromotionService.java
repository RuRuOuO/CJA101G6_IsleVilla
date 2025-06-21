package com.islevilla.patty.promotion.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service("promotionService")
public class PromotionService {

	@Autowired
	PromotionRepository repository;
	
	@Autowired
    private SessionFactory sessionFactory;

	public void addPromotion(Promotion promotion) {
		repository.save(promotion);
	}

	public void updatePromotion(Promotion promotion) {
		repository.save(promotion);
	}


	public Promotion getOnePromotion(Integer roomPromotionId) {
		Optional<Promotion> optional = repository.findById(roomPromotionId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<Promotion> getAll() {
		return repository.findAll();
	}

//	public List<Promotion> getAll(Map<String, String[]> map) {
//		return HibernateUtil_CompositeQuery_Promotion.getAllC(map,sessionFactory.openSession());
//	}

}