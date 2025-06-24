package com.islevilla.chen.roomType.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer>{


}

