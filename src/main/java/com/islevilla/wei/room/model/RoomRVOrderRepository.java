package com.islevilla.wei.room.model;

import com.islevilla.lai.members.model.Members;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface RoomRVOrderRepository extends JpaRepository<RoomRVOrder, Integer> {
    Page<RoomRVOrder> findAll(Pageable pageable);

    List<RoomRVOrder> findByMembers(Members member);
}