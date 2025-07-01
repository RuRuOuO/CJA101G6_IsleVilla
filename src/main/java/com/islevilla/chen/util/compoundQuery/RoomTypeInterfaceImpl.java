package com.islevilla.chen.util.compoundQuery;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.islevilla.chen.room.model.Room;
import com.islevilla.chen.roomType.model.RoomType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
@Transactional(readOnly = true)
public class RoomTypeInterfaceImpl implements RoomTypeInterface{

	 @PersistenceContext
    //JPA 的核心類別
    private EntityManager entityManager;

    @Override
    public List<RoomType> searchRoomTypes(Integer roomTypeId, Byte roomTypeSaleStatus) {
    	//建立基本查詢語句
        StringBuilder jpql = new StringBuilder("SELECT rt FROM RoomType rt WHERE 1=1");

        //根據條件是否為 null，動態拼接 JPQL
        if (roomTypeId != null) {
            jpql.append(" AND rt.roomTypeId = :roomTypeId");
        }
        if (roomTypeSaleStatus != null) {
        	jpql.append(" AND rt.roomTypeSaleStatus = :roomTypeSaleStatus");
        }

        // 建立一個 JPA 查詢物件，這個查詢會回傳「RoomType 實體」的清單
        TypedQuery<RoomType> query = entityManager.createQuery(jpql.toString(), RoomType.class); //告訴 JPA：這個查詢會回傳哪種實體類別

        if (roomTypeId != null) {
            query.setParameter("roomTypeId", roomTypeId);
        }
        if (roomTypeSaleStatus != null) {
            query.setParameter("roomTypeSaleStatus", roomTypeSaleStatus);
        }

        return query.getResultList();
    }
}
