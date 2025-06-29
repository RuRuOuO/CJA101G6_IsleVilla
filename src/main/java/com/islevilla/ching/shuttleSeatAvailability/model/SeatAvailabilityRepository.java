package com.islevilla.ching.shuttleSeatAvailability.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.islevilla.ching.shuttleSeatAvailability.model.SeatAvailability.ShuttleSeatAvailabilityId;



public interface SeatAvailabilityRepository extends JpaRepository<SeatAvailability,ShuttleSeatAvailabilityId>{

	List<SeatAvailability> findAllByOrderByShuttleDateAsc();

}
