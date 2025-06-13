package com.islevilla.lai.members.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MembersRepository extends JpaRepository<Members, Integer> {
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM members WHERE memberId =?1", nativeQuery = true)
	void deleteByEmpno(int memberId);
}
