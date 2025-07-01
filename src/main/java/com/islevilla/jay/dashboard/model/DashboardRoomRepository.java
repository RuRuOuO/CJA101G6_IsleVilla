package com.islevilla.jay.dashboard.model;

import com.islevilla.chen.room.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardRoomRepository extends JpaRepository<Room, Integer> {
    long countByRoomStatus(Byte roomStatus);
} 