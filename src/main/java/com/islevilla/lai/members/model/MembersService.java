package com.islevilla.lai.members.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.islevilla.lai.util.PasswordConvert;

@Service
public class MembersService {

	@Autowired
	MembersRepository membersRepository;

	@Autowired
	private PasswordConvert pc;

	// 新增會員
	public void addMember(Members member) {
		membersRepository.save(member);
	}

	// 更新會員
	public Members updateMember(Members member) {
		member.setMemberUpdatedAt(LocalDateTime.now());
		return membersRepository.save(member);
	}

	// 刪除會員
	public void deleteMember(Integer memberId) {
		if (membersRepository.existsById(memberId)) {
			membersRepository.deleteByMemberId(memberId);
		}
	}

	// 根據會員編號取得會員
	// 使用範圍：MembersController、ProductOrderController
	public Members getOneMember(Integer memberId) {
		Optional<Members> optional = membersRepository.findById(memberId);
		return optional.orElse(null); // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	// 根據電子信箱取得會員
	// 使用範圍：無
	public Members getMemberByEmail(String memberEmail) {
		Optional<Members> optional = membersRepository.findByMemberEmail(memberEmail);
		return optional.orElse(null);
	}

	// 取得所有會員
	// 使用範圍：ProductOrderNoController、MemberCouponController
	public List<Members> getAll() {
		return membersRepository.findAll();
	}
	
	// 分頁查詢所有會員
    public Page<Members> getAll(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return membersRepository.findAll(pageable);
    }

	// 會員登入驗證
	public Members authenticateMember(String email, String password) {
		Optional<Members> membersOpt = membersRepository.findByMemberEmail(email);
		if (membersOpt.isPresent()) {
			Members member = membersOpt.get();
			System.out.println("找到會員：" + member.getMemberEmail());
			// 驗證密碼
			if (pc.passwordVerify(member.getMemberPasswordHash(), password)) {
				System.out.println("密碼驗證成功！");
				return member;
			}
			System.out.println("密碼驗證失敗！");
		} else {
			System.out.println("找不到該電子信箱的會員：" + email);
		}
		return null;
	}

	// 更新最後登入時間
	public void updateMemberLastLoginTime(Integer memberId, LocalDateTime memberLastLoginTime) {
		membersRepository.updateMemberLastLoginTime(memberId, memberLastLoginTime);
		System.out.println("已更新會員最後登入時間: " + memberLastLoginTime);
	}
	
	// 更新會員密碼
	public boolean updatePassword(Integer memberId, String currentPassword, String newPassword) {
	    Optional<Members> optional = membersRepository.findById(memberId);
	    if (optional.isPresent()) {
	        Members member = optional.get();
	        
	        // 檢查原密碼是否正確
	        if (pc.passwordVerify(member.getMemberPasswordHash(), currentPassword)) {
	            // 更新密碼
	            member.setMemberPasswordHash(pc.hashing(newPassword));
	            member.setMemberUpdatedAt(LocalDateTime.now());
	            membersRepository.save(member);
	            return true;
	        }
	    }
	    return false;
	}

	// 檢查電子信箱是否已存在
	public boolean existsByEmail(String memberEmail) {
		return membersRepository.existsByMemberEmail(memberEmail);
	}

	public boolean existsByPhone(String memberPhone) {
		return membersRepository.existsByMemberPhone(memberPhone);
	}

	// 根據電子信箱和狀態查找會員
	public Members getMemberByEmailAndStatus(String memberEmail, Integer memberStatus) {
		Optional<Members> optional = membersRepository.findByMemberEmailAndMemberStatus(memberEmail, memberStatus);
		return optional.orElse(null);
	}

	// 更新會員狀態
	public void updateMemberStatus(Integer memberId, Integer memberStatus) {
		membersRepository.updateMemberStatus(memberId, memberStatus, LocalDateTime.now());
		System.out.println("已更新會員狀態: memberId=" + memberId + ", status=" + memberStatus);
	}
	
	// 搜尋會員（根據姓名或email）
    public Page<Members> searchMembers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("memberCreatedAt").descending());
        if (keyword.contains("@")) {
            return membersRepository.findByMemberEmailContaining(keyword, pageable);
        } else {
            return membersRepository.findByMemberNameContaining(keyword, pageable);
        }
    }
    
    // 複合條件查詢會員
    public Page<Members> findMembersWithConditions(String memberName, String memberEmail, 
                                                  Integer memberStatus, Integer memberGender,
                                                  int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("memberCreatedAt").descending());
        return membersRepository.findByMultipleConditions(memberName, memberEmail, 
                                                         memberStatus, memberGender, pageable);
    }
    
    // 獲取會員總數
    public long getTotalMembersCount() {
        return membersRepository.count();
    }
    
    // 獲取會員狀態統計
    public Map<String, Long> getMemberStatusStatistics() {
        List<Object[]> results = membersRepository.countByMemberStatus();
        return results.stream().collect(
            java.util.stream.Collectors.toMap(
                arr -> getStatusName((Integer) arr[0]),
                arr -> (Long) arr[1]
            )
        );
    }
    
    // 輔助方法：獲取狀態名稱
    private String getStatusName(Integer status) {
        switch (status) {
            case 0: return "未驗證";
            case 1: return "已驗證";
            case 2: return "停用";
            default: return "未知";
        }
    }

	public List<Members> getMemberByMemberGenderAndMemberStatus(Integer gender, Integer status) {
		return membersRepository.findByMemberGenderAndMemberStatus(gender, status);
	}

	public List<Members> getMemberByMemberStatus(Integer status) {
		return membersRepository.findByMemberStatus(status);
	}

	public List<Members> getMemberByMemberGender(Integer gender) {
		// TODO Auto-generated method stub
		return membersRepository.findByMemberGender(gender);
	}
	
	/**
     * 關鍵字搜尋會員
     */
    public List<Members> searchMembers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        return membersRepository.findByKeyword(keyword.trim());
    }
    
    
}
