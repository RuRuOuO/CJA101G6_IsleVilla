package com.islevilla.lai.members.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.islevilla.lai.util.PasswordConvert;

@Service
public class MembersService {
	
	@Autowired
	MembersRepository membersRepository;
	
	@Autowired
    private PasswordConvert pc;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Transactional
	public void addMember(Members members) {
		membersRepository.save(members);
	}
	
	@Transactional
	public void updateMember(Members members) {
		membersRepository.save(members);
	}
	
	public void deleteMember(Integer memberId) {
		if (membersRepository.existsById(memberId)) {
//			repository.deleteById(memberId);
		}
	}
	
	public Members getOneMember(Integer memberId) {
		Optional<Members> optional = membersRepository.findById(memberId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}
	
	public boolean login(String email, String inputPassword) {
        Members members = membersRepository.findByEmail(email);
        return members != null && pc.passwordVerify(members.getMemberPasswordHash(), inputPassword);
    }
	
	public Members getMemberByEmail(String memberEmail) {
        return membersRepository.findByEmail(memberEmail);
    }
	
	public List<Members> getAll() {
		return membersRepository.findAll();
	}
	
	// 會員登入驗證
    public Members authenticateMember(String email, String password) {
//        Optional<Members> membersOpt = membersRepository.findByMemberEmailAndMemberStatus(email, 1);
//        Optional<Members> membersOpt = membersRepository.findByMemberEmailAndMemberStatus(email, 1);
//        Optional<Members> membersOpt0 = membersRepository.findByMemberEmailAndMemberStatus(email, 0);
//        if (membersOpt.isPresent() || membersOpt0.isPresent()) {
//            Members members = membersOpt.get();
//            System.out.println(members);
//            if (pc.passwordVerify(members.getMemberPasswordHash(), password)) {
//                return members;
//            }
//        }
        
        Optional<Members> membersOpt1 = membersRepository.findByMemberEmail(email);
        if (membersOpt1.isPresent()) {
            Members members = membersOpt1.get();
            System.out.println(members);
            if (pc.passwordVerify(members.getMemberPasswordHash(), password)) {
            	System.out.println("Login Successfully!");
                return members;
            }
            System.out.println("Login Failed by Wrong Password!");
        } else {
        	System.out.println("Login Failed by Wrong Email!");
        }
        
        return null;
    }
	
    // 更新最後登入時間
    @Transactional
    public void updateMemberLastLoginTime(Integer memberId, LocalDateTime lastLoginTime) {
    	System.out.println(LocalDateTime.now());
        membersRepository.updateMemberLastLoginTime(memberId, lastLoginTime);
    }
    
    // 檢查電子信箱是否已存在
    public boolean existsByEmail(String memberEmail) {
        return membersRepository.existsByMemberEmail(memberEmail);
    }

	public boolean existsByPhone(String memberPhone) {
		 return membersRepository.existsByMemberPhone(memberPhone);
	}
}
