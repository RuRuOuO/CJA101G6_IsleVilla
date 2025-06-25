package com.islevilla.chen.util.compoundQuery;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.islevilla.chen.room.model.Room;
import com.islevilla.chen.roomTypePhoto.model.RoomTypePhoto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
@Transactional(readOnly = true)
public class RoomTypePhotoInterfaceImpl implements RoomTypePhotoInterface{
	 @PersistenceContext
	    //JPA 的核心類別
	    private EntityManager entityManager;

	    @Override
	    public List<RoomTypePhoto> search(Integer roomTypePhotoId, Integer roomTypeId) {
	    	//建立基本查詢語句
	        StringBuilder jpql = new StringBuilder("SELECT rtp FROM RoomTypePhoto rtp WHERE 1=1");

	        //根據條件是否為 null，動態拼接 JPQL
	        if (roomTypePhotoId != null) {
	            jpql.append(" AND rtp.roomTypePhotoId = :roomTypePhotoId");  // ⚠️ 注意這裡要看你的關聯命名
	        }
	        if (roomTypeId != null) {
	            jpql.append(" AND rtp.roomTypeId = :roomTypeId");
	        }

	        // 建立一個 JPA 查詢物件，這個查詢會回傳「Room 實體」的清單
	        TypedQuery<RoomTypePhoto> query = entityManager.createQuery(jpql.toString(), RoomTypePhoto.class); //告訴 JPA：這個查詢會回傳哪種實體類別

	        if (roomTypePhotoId != null) {
	        	query.setParameter("roomTypePhotoId",roomTypePhotoId);
	        }
	        if (roomTypeId != null) {
	            query.setParameter("roomTypeId", roomTypeId);
	        }

	        return query.getResultList();
	    }
}
