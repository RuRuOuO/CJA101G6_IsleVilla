package com.islevilla.wei.feedback.model;

import java.util.List;
import java.util.Optional;

// 介面定義資料有哪些操作; 實作類別寫如何操作
public interface FeedbackService_interface {
    Feedback save(Feedback feedback);

    Optional<Feedback> findById(Integer id); // 代表回傳值可能為空，避免null pointer exception(NPE)

    List<Feedback> findAll();

    void deleteById(Integer id);

    // 根據訂房編號查詢feedback
    Optional<Feedback> findByRoomRvId(Integer roomReservationId);
}