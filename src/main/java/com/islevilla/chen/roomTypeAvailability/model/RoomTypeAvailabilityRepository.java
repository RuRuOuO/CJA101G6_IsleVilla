package com.islevilla.chen.roomTypeAvailability.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeAvailabilityRepository extends JpaRepository<RoomTypeAvailabilityVO, Integer>{

}
