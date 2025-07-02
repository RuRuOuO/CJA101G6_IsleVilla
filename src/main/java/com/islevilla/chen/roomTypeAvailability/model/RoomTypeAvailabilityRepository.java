package com.islevilla.chen.roomTypeAvailability.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface RoomTypeAvailabilityRepository extends JpaRepository<RoomTypeAvailability, RoomTypeAvailabilityId>{

    // 根據房型ID查詢所有可用性記錄
    List<RoomTypeAvailability> findByRoomTypeAvailabilityId_RoomTypeId(Integer roomTypeId);
    
    // 根據日期查詢所有房型可用性
    List<RoomTypeAvailability> findByRoomTypeAvailabilityId_RoomTypeAvailabilityDate(LocalDate availabilityDate);
    
    // 根據房型ID和日期範圍查詢
    @Query("SELECT rta FROM RoomTypeAvailability rta WHERE rta.roomTypeAvailabilityId.roomTypeId = :roomTypeId " +
           "AND rta.roomTypeAvailabilityId.roomTypeAvailabilityDate BETWEEN :startDate AND :endDate")
    List<RoomTypeAvailability> findByRoomTypeIdAndDateRange(
        @Param("roomTypeId") Integer roomTypeId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // 根據房型ID和特定日期查詢
    Optional<RoomTypeAvailability> findByRoomTypeAvailabilityId_RoomTypeIdAndRoomTypeAvailabilityId_RoomTypeAvailabilityDate(
        Integer roomTypeId, LocalDate availabilityDate
    );
    
    // 查詢特定日期範圍內的所有可用性記錄
    @Query("SELECT rta FROM RoomTypeAvailability rta WHERE rta.roomTypeAvailabilityId.roomTypeAvailabilityDate BETWEEN :startDate AND :endDate")
    List<RoomTypeAvailability> findByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // 檢查特定房型和日期的記錄是否存在
    boolean existsByRoomTypeAvailabilityId_RoomTypeIdAndRoomTypeAvailabilityId_RoomTypeAvailabilityDate(
        Integer roomTypeId, LocalDate availabilityDate
    );
    
    // 查詢可用數量大於指定值的記錄
    @Query("SELECT rta FROM RoomTypeAvailability rta WHERE rta.roomTypeAvailabilityCount > :minCount")
    List<RoomTypeAvailability> findByAvailabilityCountGreaterThan(@Param("minCount") Integer minCount);
}
