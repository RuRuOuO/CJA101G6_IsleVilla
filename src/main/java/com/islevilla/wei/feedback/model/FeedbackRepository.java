package com.islevilla.wei.feedback.model;

import com.islevilla.wei.room.model.RoomRVOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    //    public abstract Optional<Feedback> findByRoomRVOrder_RoomReservationId(Integer roomReservationId);
    CharSequence findByRoomRVOrder(RoomRVOrder order);

    List<Feedback> findByFbPublicAndFbStatus(Integer fbPublic, Integer fbStatus);

    boolean existsByRoomRVOrder_RoomReservationId(Integer roomReservationId);


}
