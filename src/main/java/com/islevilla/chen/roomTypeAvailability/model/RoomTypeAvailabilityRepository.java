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
    
    //==================給patty用==================
    // 根據房型ID和特定日期查詢
    Optional<RoomTypeAvailability> findByRoomTypeAvailabilityId_RoomTypeIdAndRoomTypeAvailabilityId_RoomTypeAvailabilityDate(
        Integer roomTypeId, LocalDate availabilityDate
    );
    // 複合查詢：支援房型ID、特定日期、日期範圍的組合查詢
    @Query("SELECT rta FROM RoomTypeAvailability rta WHERE " +
           "(:roomTypeId IS NULL OR rta.roomTypeAvailabilityId.roomTypeId = :roomTypeId) " +
           "AND (:specificDate IS NULL OR rta.roomTypeAvailabilityId.roomTypeAvailabilityDate = :specificDate) " +
           "AND (:startDate IS NULL OR rta.roomTypeAvailabilityId.roomTypeAvailabilityDate >= :startDate) " +
           "AND (:endDate IS NULL OR rta.roomTypeAvailabilityId.roomTypeAvailabilityDate <= :endDate)")
    Page<RoomTypeAvailability> findByComplexQuery(
        @Param("roomTypeId") Integer roomTypeId,
        @Param("specificDate") LocalDate specificDate,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable
    );
    
    // 複合查詢：支援房型ID、特定日期、日期範圍的組合查詢 (不分頁版本)
    @Query("SELECT rta FROM RoomTypeAvailability rta WHERE " +
           "(:roomTypeId IS NULL OR rta.roomTypeAvailabilityId.roomTypeId = :roomTypeId) " +
           "AND (:specificDate IS NULL OR rta.roomTypeAvailabilityId.roomTypeAvailabilityDate = :specificDate) " +
           "AND (:startDate IS NULL OR rta.roomTypeAvailabilityId.roomTypeAvailabilityDate >= :startDate) " +
           "AND (:endDate IS NULL OR rta.roomTypeAvailabilityId.roomTypeAvailabilityDate <= :endDate) " +
           "ORDER BY rta.roomTypeAvailabilityId.roomTypeAvailabilityDate DESC")
    List<RoomTypeAvailability> findByComplexQueryAll(
        @Param("roomTypeId") Integer roomTypeId,
        @Param("specificDate") LocalDate specificDate,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    // 根據房型ID分頁查詢
    Page<RoomTypeAvailability> findByRoomTypeAvailabilityId_RoomTypeId(Integer roomTypeId, Pageable pageable);
    // 根據日期分頁查詢
    Page<RoomTypeAvailability> findByRoomTypeAvailabilityId_RoomTypeAvailabilityDate(LocalDate availabilityDate, Pageable pageable);
    // 根據日期範圍分頁查詢
    @Query("SELECT rta FROM RoomTypeAvailability rta WHERE rta.roomTypeAvailabilityId.roomTypeAvailabilityDate BETWEEN :startDate AND :endDate")
    Page<RoomTypeAvailability> findByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable
    );
    // 根據房型ID查詢所有可用性記錄
    List<RoomTypeAvailability> findByRoomTypeAvailabilityId_RoomTypeId(Integer roomTypeId);
    // 根據房型ID和日期範圍查詢
    @Query("SELECT rta FROM RoomTypeAvailability rta WHERE rta.roomTypeAvailabilityId.roomTypeId = :roomTypeId " +
           "AND rta.roomTypeAvailabilityId.roomTypeAvailabilityDate BETWEEN :startDate AND :endDate")
    List<RoomTypeAvailability> findByRoomTypeIdAndDateRange(
        @Param("roomTypeId") Integer roomTypeId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    // 查詢特定日期範圍內的所有可用性記錄
    @Query("SELECT rta FROM RoomTypeAvailability rta WHERE rta.roomTypeAvailabilityId.roomTypeAvailabilityDate BETWEEN :startDate AND :endDate")
    List<RoomTypeAvailability> findByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
  //==================================

}
