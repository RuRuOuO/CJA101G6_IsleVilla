package com.islevilla.chen.room.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.islevilla.chen.util.compoundQuery.RoomInterface;

@Repository
@Transactional
public interface RoomRepository extends JpaRepository<Room, Integer>,RoomInterface{
   
}
