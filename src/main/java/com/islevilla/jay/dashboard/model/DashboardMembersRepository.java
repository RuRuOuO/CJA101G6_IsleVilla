package com.islevilla.jay.dashboard.model;

import com.islevilla.lai.members.model.Members;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface DashboardMembersRepository extends JpaRepository<Members, Integer> {
    @Query("SELECT COUNT(m) FROM Members m WHERE m.memberCreatedAt BETWEEN :start AND :end")
    long countByMemberCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
} 