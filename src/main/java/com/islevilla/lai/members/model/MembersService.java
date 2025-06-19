package com.islevilla.lai.members.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.islevilla.lai.util.PasswordConvert;

@Service
public class MembersService {

	@Autowired
	MembersRepository membersRepository;

	@Autowired
	private PasswordConvert pc;

	@Autowired
	private SessionFactory sessionFactory;

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
	public Members getOneMember(Integer memberId) {
		Optional<Members> optional = membersRepository.findById(memberId);
		return optional.orElse(null); // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	// 根據電子信箱取得會員
	public Members getMemberByEmail(String memberEmail) {
		Optional<Members> optional = membersRepository.findByMemberEmail(memberEmail);
		return optional.orElse(null);
	}

	// 取得所有會員
	public List<Members> getAll() {
		return membersRepository.findAll();
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

	// 驗證會員登入 (舊方法)
//	public boolean login(String email, String inputPassword) {
//        Members members = membersRepository.findByEmail(email);
//        return members != null && pc.passwordVerify(members.getMemberPasswordHash(), inputPassword);
//    }
}
