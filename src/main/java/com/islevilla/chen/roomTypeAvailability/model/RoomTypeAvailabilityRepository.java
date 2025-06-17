package com.islevilla.chen.roomTypeAvailability.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface RoomTypeAvailabilityRepository extends JpaRepository<RoomTypeAvailabilityVO, Integer>{

}
