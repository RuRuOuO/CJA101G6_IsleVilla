package com.islevilla.lai.members.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MembersRepository extends JpaRepository<Members, Integer> {
	
	/* --------------------------------查詢-------------------------------- */
	

	// 根據電子信箱查找會員
	@Query("SELECT m FROM Members m WHERE m.memberEmail = :memberEmail")
	Optional<Members> findByMemberEmail(@Param("memberEmail") String memberEmail);
	

    // 根據email模糊查詢
    List<Members> findByMemberEmailContaining(String memberEmail);
    
    // 根據email模糊查詢（分頁）
    Page<Members> findByMemberEmailContaining(String memberEmail, Pageable pageable);
    
    // 根據姓名模糊查詢
    List<Members> findByMemberNameContaining(String memberName);
    
    // 根據姓名模糊查詢（分頁）
    Page<Members> findByMemberNameContaining(String memberName, Pageable pageable);
    
	// 根據電子信箱和狀態查找會員
	Optional<Members> findByMemberEmailAndMemberStatus(String memberEmail, Integer memberStatus);

	// 根據會員性別查詢
	List<Members> findByMemberGender(Integer gender);
	
	// 根據會員狀態查詢
	List<Members> findByMemberStatus(Integer memberStatus);
	
	// 根據會員性別和會員狀態查詢
	List<Members> findByMemberGenderAndMemberStatus(Integer memberGender, Integer memberStatus);
	
	// 統計各狀態的會員數量
    @Query("SELECT m.memberStatus, COUNT(m) FROM Members m GROUP BY m.memberStatus")
    List<Object[]> countByMemberStatus();

	
	// 複合查詢：根據多個條件查詢
    @Query("SELECT m FROM Members m WHERE " +
           "(:memberName IS NULL OR m.memberName LIKE %:memberName%) AND " +
           "(:memberEmail IS NULL OR m.memberEmail LIKE %:memberEmail%) AND " +
           "(:memberStatus IS NULL OR m.memberStatus = :memberStatus) AND " +
           "(:memberGender IS NULL OR m.memberGender = :memberGender)")
    Page<Members> findByMultipleConditions(@Param("memberName") String memberName,
                                         @Param("memberEmail") String memberEmail,
                                         @Param("memberStatus") Integer memberStatus,
                                         @Param("memberGender") Integer memberGender,
                                         Pageable pageable);
    
    // 複合搜尋：根據姓名或電子信箱模糊查詢
    @Query("SELECT m FROM Members m WHERE m.memberName LIKE %:keyword% OR m.memberEmail LIKE %:keyword%")
    List<Members> findByKeyword(@Param("keyword") String keyword);

    /* --------------------------------存在-------------------------------- */
    
	// 檢查電子信箱是否存在
	boolean existsByMemberEmail(String memberEmail);

	// 檢查電話號碼是否存在
	boolean existsByMemberPhone(String memberPhone);
	/* --------------------------------更新-------------------------------- */
	
	// 更新最後登入時間
	@Transactional
	@Modifying
	@Query("UPDATE Members m SET m.memberLastLoginTime = :memberLastLoginTime WHERE m.memberId = :memberId")
	void updateMemberLastLoginTime(@Param("memberId") Integer memberId,
			@Param("memberLastLoginTime") LocalDateTime memberLastLoginTime);


	// 更新會員狀態
	@Transactional
	@Modifying
	@Query("UPDATE Members m SET m.memberStatus = :memberStatus, m.memberUpdatedAt = :memberUpdatedAt WHERE m.memberId = :memberId")
	void updateMemberStatus(@Param("memberId") Integer memberId, @Param("memberStatus") Integer memberStatus,
			@Param("memberUpdatedAt") LocalDateTime memberUpdatedAt);

	/* --------------------------------刪除-------------------------------- */
	
	// 根據會員編號刪除會員
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM Members m WHERE m.memberId = :memberId")
	void deleteByMemberId(@Param("memberId") Integer memberId);




	
}
