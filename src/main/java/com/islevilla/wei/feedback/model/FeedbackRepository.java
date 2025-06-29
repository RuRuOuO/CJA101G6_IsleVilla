package com.islevilla.wei.feedback.model;

import com.islevilla.wei.room.model.RoomRVOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    //    public abstract Optional<Feedback> findByRoomRVOrder_RoomReservationId(Integer roomReservationId);
    CharSequence findByRoomRVOrder(RoomRVOrder order);
}
