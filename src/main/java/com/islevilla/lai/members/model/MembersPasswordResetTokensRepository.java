package com.islevilla.lai.members.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MembersPasswordResetTokensRepository extends JpaRepository<MembersPasswordResetTokens, Integer> {

	// 根據token查找
	Optional<MembersPasswordResetTokens> findByToken(String token);

	// 根據email查找所有token
	List<MembersPasswordResetTokens> findByMemberEmail(String memberEmail);

	// 根據email查找有效的token（未使用且未過期）
	@Query("SELECT t FROM MembersPasswordResetTokens t WHERE t.memberEmail = :email AND t.used = false AND t.expiryTime > :now")
	List<MembersPasswordResetTokens> findValidTokensByEmail(@Param("email") String email,
			@Param("now") LocalDateTime now);

	// 將特定email的所有token標記為已使用
	@Modifying
	@Query("UPDATE MembersPasswordResetTokens t SET t.used = true WHERE t.memberEmail = :email")
	void invalidateAllTokensByEmail(@Param("email") String email);

	// 刪除過期的token
	@Modifying
	@Query("DELETE FROM MembersPasswordResetTokens t WHERE t.expiryTime < :now")
	void deleteExpiredTokens(@Param("now") LocalDateTime now);

	// 刪除特定email的所有token
	@Modifying
	void deleteByMemberEmail(String memberEmail);
}