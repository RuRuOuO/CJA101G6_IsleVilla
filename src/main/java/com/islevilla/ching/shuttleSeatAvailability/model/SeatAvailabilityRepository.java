package com.islevilla.ching.shuttleSeatAvailability.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;



public interface SeatAvailabilityRepository extends JpaRepository<SeatAvailability,SeatAvailabilityId>{

	List<SeatAvailability> findAllByOrderByDateAsc();

}
