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
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        // 計算一個月前的日期
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        // 查詢條件：該會員的訂單中，實際退房時間在過去一個月內，且訂單狀態為已退房
        List<RoomRVOrder> orderList = roomRVOrderRepository.findEligibleOrdersForFeedback(
                member.getMemberId(),
                oneMonthAgo,
                2 // 訂單狀態
        );

        // 轉換為 DTO 並過濾掉已有評價的訂單
        // 初始化結果清單
        List<RoomRVOrderDTO> resultList = new ArrayList<>();
        for (RoomRVOrder order : orderList) {
            // 如果該訂單尚未有評價
            if (!hasExistingFeedback(order.getRoomReservationId())) {
                // 手動轉換為 DTO
                RoomRVOrderDTO dto = new RoomRVOrderDTO();
                dto.setRoomReservationId(order.getRoomReservationId());
                dto.setCheckInDate(order.getCheckInDate());
                dto.setCheckOutDate(order.getCheckOutDate());
                dto.setActualCheckOutDate(order.getActualCheckOutDate());

                resultList.add(dto);
            }
        }
        return resultList;
    }

    @Override
    public boolean hasExistingFeedback(Integer roomReservationId) {
        // 檢查是否已存在該訂單的評價
        return feedbackRepository.existsByRoomRVOrder_RoomReservationId(roomReservationId);
    }

    @Override
    public List<Feedback> findPublicAndActiveFeedbacks() {
        return feedbackRepository.findByFbPublicAndFbStatus(1, 1);
    }

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
