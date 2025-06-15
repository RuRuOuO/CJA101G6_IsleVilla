package com.islevilla.lai.members.model;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MembersRepository extends JpaRepository<Members, Integer> {
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM members WHERE memberId =?1", nativeQuery = true)
	void deleteByEmpno(int memberId);
	
	@Transactional
	@Modifying
	@Query(value = "SELECT memberEmail FROM members WHERE memberEmail =?1", nativeQuery = true)
	Members findByEmail(String memberEmail);
	
	// 根據電子信箱和狀態查找會員
    Optional<Members> findByMemberEmailAndMemberStatus(String memberEmail, Integer memberStatus);
    
 // 根據電子信箱查找會員
    Optional<Members> findByMemberEmail(String memberEmail);
    
    // 更新最後登入時間
    @Modifying
    @Query("UPDATE Members m SET m.memberLastLoginTime = :memberLastLoginTime WHERE m.memberId = :memberId")
    void updateMemberLastLoginTime(@Param("memberId") Integer memberId, 
                            	   @Param("memberLastLoginTime") LocalDateTime memberLastLoginTime);
    
    // 檢查電子信箱是否存在
    boolean existsByMemberEmail(String memberEmail);

    // 檢查電話號碼是否存在
	boolean existsByMemberPhone(String memberPhone);
}
