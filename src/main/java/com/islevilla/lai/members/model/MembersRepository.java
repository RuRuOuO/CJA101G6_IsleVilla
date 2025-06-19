package com.islevilla.lai.members.model;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MembersRepository extends JpaRepository<Members, Integer> {
	// 根據會員編號刪除會員
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM Members m WHERE m.memberId = :memberId")
	void deleteByMemberId(@Param("memberId") Integer memberId);

	// 根據電子信箱查找會員
	@Transactional
	@Modifying
	@Query(value = "SELECT memberEmail FROM members WHERE memberEmail =?1", nativeQuery = true)
	Members findByEmail(String memberEmail);

	// 根據電子信箱查找會員
	@Query("SELECT m FROM Members m WHERE m.memberEmail = :memberEmail")
	Optional<Members> findByMemberEmail(@Param("memberEmail") String memberEmail);

	// 根據電子信箱和狀態查找會員
	Optional<Members> findByMemberEmailAndMemberStatus(String memberEmail, Integer memberStatus);

	// 更新最後登入時間
	@Transactional
	@Modifying
	@Query("UPDATE Members m SET m.memberLastLoginTime = :memberLastLoginTime WHERE m.memberId = :memberId")
	void updateMemberLastLoginTime(@Param("memberId") Integer memberId,
			@Param("memberLastLoginTime") LocalDateTime memberLastLoginTime);

	// 檢查電子信箱是否存在
	boolean existsByMemberEmail(String memberEmail);

	// 檢查電話號碼是否存在
	boolean existsByMemberPhone(String memberPhone);

	// 根據會員狀態查找會員列表
	@Query("SELECT m FROM Members m WHERE m.memberStatus = :memberStatus")
	Optional<Members> findByMemberStatus(@Param("memberStatus") Integer memberStatus);

	// 更新會員狀態
	@Transactional
	@Modifying
	@Query("UPDATE Members m SET m.memberStatus = :memberStatus, m.memberUpdatedAt = :memberUpdatedAt WHERE m.memberId = :memberId")
	void updateMemberStatus(@Param("memberId") Integer memberId, @Param("memberStatus") Integer memberStatus,
			@Param("memberUpdatedAt") LocalDateTime memberUpdatedAt);
}
