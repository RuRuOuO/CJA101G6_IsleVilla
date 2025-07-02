package com.islevilla.jay.dashboard.model;

import com.islevilla.lai.shuttle.model.ShuttleReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DashboardShuttleReservationRepository extends JpaRepository<ShuttleReservation, Integer> {
    @Query("SELECT COALESCE(SUM(sr.shuttleNumber), 0) FROM ShuttleReservation sr WHERE sr.shuttleDate = :date AND sr.shuttleReservationStatus = :status")
    Long sumShuttleNumberByShuttleDateAndStatus(@Param("date") LocalDate date, @Param("status") Integer status);
} 