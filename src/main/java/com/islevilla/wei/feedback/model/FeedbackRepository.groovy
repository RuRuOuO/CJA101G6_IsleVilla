package com.islevilla.wei.feedback.model

import org.springframework.data.jpa.repository.JpaRepository

interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    // 依照訂單編號查詢feedback
    Optional<Feedback> findByRoomRVOrder_RoomReservationId(Integer roomReservationId);
}