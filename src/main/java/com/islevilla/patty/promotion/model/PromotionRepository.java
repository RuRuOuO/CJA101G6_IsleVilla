// https://docs.spring.io/spring-data/jpa/docs/current/reference/html/

package com.islevilla.patty.promotion.model;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import com.islevilla.patty.roompromotionprice.model.RoomPromotionPrice;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

	@Transactional
	@Modifying
	@Query(value = "delete from promotion where roomPromotionId =?1", nativeQuery = true)
	void deleteByRoomPromotionId(int roomPromotionId);

}