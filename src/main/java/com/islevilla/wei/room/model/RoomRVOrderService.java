package com.islevilla.wei.room.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class RoomRVOrderService {
    @Autowired
    private RoomRVOrderRepository roomRVOrderRepository;

    public Page<RoomRVOrder> getAll(Pageable pageable) {
        return roomRVOrderRepository.findAll(pageable);
    }
}
