package com.islevilla.chen.roomTypeAvailability.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface RoomTypeAvailabilityRepository extends JpaRepository<RoomTypeAvailability, RoomTypeAvailabilityId>{

	// 全部房型和單月份查詢
	@Query("SELECT rta FROM RoomTypeAvailability rta " +
		       "WHERE YEAR(rta.roomTypeAvailabilityId.roomTypeAvailabilityDate) = :year " +
		       "AND MONTH(rta.roomTypeAvailabilityId.roomTypeAvailabilityDate) = :month")
		List<RoomTypeAvailability> findAllByYearMonth(
		    @Param("year") int year,
		    @Param("month") int month
		);

    
    // 根據房型ID和單月份查詢
    @Query("SELECT rta FROM RoomTypeAvailability rta " +
    	       "WHERE rta.roomTypeAvailabilityId.roomTypeId = :roomTypeId " +
    	       "AND YEAR(rta.roomTypeAvailabilityId.roomTypeAvailabilityDate) = :year " +
    	       "AND MONTH(rta.roomTypeAvailabilityId.roomTypeAvailabilityDate) = :month")
    	List<RoomTypeAvailability> findByRoomTypeIdAndYearMonth(
    	    @Param("roomTypeId") Integer roomTypeId,
    	    @Param("year") int year,
    	    @Param("month") int month
    	);
    

}
