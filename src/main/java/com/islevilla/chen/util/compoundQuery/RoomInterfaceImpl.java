package com.islevilla.chen.util.compoundQuery;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.islevilla.chen.room.model.Room;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
@Transactional(readOnly = true)
public class RoomInterfaceImpl implements RoomInterface {

    @PersistenceContext
    //JPA 的核心類別
    private EntityManager entityManager;

    @Override
    public List<Room> searchRooms(Integer roomId, Integer roomTypeId, Byte roomStatus) {
    	//建立基本查詢語句
        StringBuilder jpql = new StringBuilder("SELECT r FROM Room r WHERE 1=1");

        //根據條件是否為 null，動態拼接 JPQL
        if (roomId != null) {
            jpql.append(" AND r.roomId = :roomId");  // ⚠️ 注意這裡要看你的關聯命名
        }
        if (roomTypeId != null) {
            jpql.append(" AND r.roomTypeId = :roomTypeId");
        }
        if (roomStatus != null) {
        	jpql.append(" AND r.roomStatus = :roomStatus");
        }

        // 建立一個 JPA 查詢物件，這個查詢會回傳「Room 實體」的清單
        TypedQuery<Room> query = entityManager.createQuery(jpql.toString(), Room.class); //告訴 JPA：這個查詢會回傳哪種實體類別

        if (roomId != null) {
        	query.setParameter("roomId", roomId);
        }
        if (roomTypeId != null) {
            query.setParameter("roomTypeId", roomTypeId);
        }
        if (roomStatus != null) {
            query.setParameter("roomStatus", roomStatus);
        }

        return query.getResultList();
    }
}
