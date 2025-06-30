package com.islevilla.wei.feedback.model;

import com.islevilla.lai.members.model.Members;
import com.islevilla.wei.feedback.dto.FeedbackFormDTO;
import com.islevilla.wei.feedback.dto.RoomRVOrderDTO;
import com.islevilla.wei.room.model.RoomRVOrder;

import java.util.List;
import java.util.Optional;

// 介面定義資料有哪些操作; 實作類別寫如何操作
public interface FeedbackService_interface {
    Feedback save(Feedback feedback);

    Optional<Feedback> findById(Integer id); // 代表回傳值可能為空，避免null pointer exception(NPE)

    List<Feedback> findAll();

    void deleteById(Integer id);

    List<RoomRVOrderDTO> getAvailableOrders(Members member);

    boolean saveFeedback(FeedbackFormDTO dto, Members loginMember);

    List<Feedback> findPublicAndActiveFeedbacks();

    // 檢查對應訂單是否已經被評價
    boolean hasExistingFeedback(Integer roomReservationId);
}