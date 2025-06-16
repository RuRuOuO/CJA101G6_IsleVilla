package com.islevilla.chen.roomTypePhoto.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypePhotoRepository extends JpaRepository<RoomTypePhotoVO, Integer> {

}
