package com.islevilla.wei.feedback.model;

import com.islevilla.lai.members.model.Members;
import com.islevilla.wei.feedback.dto.FeedbackFormDTO;
import com.islevilla.wei.feedback.dto.RoomRVOrderDTO;
import com.islevilla.wei.room.model.RoomRVOrder;
import com.islevilla.wei.room.model.RoomRVOrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeedbackService implements FeedbackService_interface {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RoomRVOrderRepository roomRVOrderRepository;

    @Override
    public Feedback save(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    @Override
    public Optional<Feedback> findById(Integer id) {
        return feedbackRepository.findById(id);
    }

    @Override
    public List<Feedback> findAll() {
        return feedbackRepository.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        feedbackRepository.deleteById(id);
    }

    @Override
    public List<RoomRVOrderDTO> getAvailableOrders(Members member) {
        List<RoomRVOrder> orders = roomRVOrderRepository.findByMembersAndRoomOrderStatus(member, "2");

        return orders.stream().map(order -> {
            RoomRVOrderDTO dto = new RoomRVOrderDTO();
            dto.setRoomReservationId(order.getRoomReservationId());
            dto.setCheckInDate(order.getCheckInDate());
            dto.setCheckOutDate(order.getCheckOutDate());
            return dto;
        }).collect(Collectors.toList());
    }

//    // 根據訂房編號查詢feedback
//    @Override
//    public Optional<Feedback> findByRoomRvId(Integer roomReservationId) {
//        return feedbackRepository.findByRoomRVOrder_RoomReservationId(roomReservationId);
//    }

    @Override
    public boolean saveFeedback(FeedbackFormDTO dto, Members member) {
        Optional<RoomRVOrder> optOrder = roomRVOrderRepository.findById(dto.getRoomReservationId());
        if (optOrder.isEmpty()) return false;

        RoomRVOrder order = optOrder.get();

        if (!order.getMembers().getMemberId().equals(member.getMemberId())) return false;

        Feedback feedback = modelMapper.map(dto, Feedback.class);
        feedback.setRoomRVOrder(order);
        feedback.setFbCreatedAt(new Date());
        feedback.setFbUpdatedAt(new Date());
        feedback.setFbStatus(0); // 預設未上架

        try {
            if (dto.getFbImage() != null && !dto.getFbImage().isEmpty()) {
                feedback.setFbImage(dto.getFbImage().getBytes());
            }
        } catch (IOException e) {
            return false;
        }

        feedbackRepository.save(feedback);
        return true;
    }
}
