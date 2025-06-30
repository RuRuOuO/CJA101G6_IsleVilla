package com.islevilla.chen.roomType.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.islevilla.chen.util.compoundQuery.RoomTypeInterface;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer>,RoomTypeInterface{

	//在新增及更新功能中，判斷房型代碼是否重複
	public boolean existsByRoomTypeCode(String roomTypeCode);
	//在新增及更新功能中，判斷房型名稱是否重複
	public boolean existsByRoomTypeName(String roomTypeName);

	//查詢房型上下架狀態
	public List<RoomType> findByRoomTypeSaleStatus(Byte roomTypeSaleStatus);

	//複合查詢
	public List<RoomType> searchRoomTypes(Integer roomTypeId, Byte roomTypeSaleStatus);
}

