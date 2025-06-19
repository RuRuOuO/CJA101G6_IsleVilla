package com.islevilla.patty.roomreservationorder.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hibernate.util.CompositeQuery.HibernateUtil_CompositeQuery_Emp3;


@Service("roomreservationorderService")
public class RoomReservationOrderService {

	@Autowired
	RoomReservationOrderRepository repository;
	
	@Autowired
    private SessionFactory sessionFactory;

	public void addRoomReservationOrder(RoomReservationOrder roomreservationorder) {
		repository.save(roomreservationorder);
	}

	public void updateRoomReservationOrder(RoomReservationOrder roomreservationorder) {
		repository.save(roomreservationorder);
	}

	public void deleteRoomReservationOrder(Integer roomreservationorder) {
		if (repository.existsById(roomreservationorder))
			repository.deleteByEmpno(roomreservationorder);
//		    repository.deleteById(roomreservationorder);
	}

	public RoomReservationOrder getOneRoomReservationOrder(Integer roomreservationorder) {
		Optional<RoomReservationOrder> optional = repository.findById(roomreservationorder);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<RoomReservationOrder> getAll() {
		return repository.findAll();
	}

	public List<RoomReservationOrder> getAll(Map<String, String[]> map) {
		return HibernateUtil_CompositeQuery_Emp3.getAllC(map,sessionFactory.openSession());
	}

}