package com.islevilla.wei.room.model;

import com.islevilla.lai.members.model.Members;
import com.islevilla.lai.members.model.MembersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomRVOrderService {
    @Autowired
    private RoomRVOrderRepository roomRVOrderRepository;
    @Autowired
    private MembersRepository membersRepository;

    // 查詢全部
    public Page<RoomRVOrder> getAll(Pageable pageable) {
        return roomRVOrderRepository.findAll(pageable);
    }

    // 用id查詢單筆
    public RoomRVOrder getById(Integer id) {
        Optional<RoomRVOrder> roomRVOrder = roomRVOrderRepository.findById(id);
        return roomRVOrder.orElse(null);
    }

    // 用會員id查詢該會員所有訂單
    public List<RoomRVOrder> getRoomRVOrderByMember(Members member) {
        return roomRVOrderRepository.findByMembers(member);
    }

    // 更新訂單
    public void updateRoomRVOrder(RoomRVOrder roomRVOrder) {
        roomRVOrderRepository.save(roomRVOrder);
    }

    // 取消訂單
    public void cancelOrderFront(Integer orderId) {
        RoomRVOrder order = getById(orderId);
        if (order != null) {
            order.setRoomOrderStatus("3");
            updateRoomRVOrder(order);
        }
    }

    // 取消訂單
    public void cancelOrderBack(Integer orderId) {
        RoomRVOrder order = getById(orderId);
        if (order != null) {
            order.setRoomOrderStatus("4");
            updateRoomRVOrder(order);
        }
    }
}
