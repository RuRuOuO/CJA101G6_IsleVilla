//package com.islevilla.lai.auth;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import com.islevilla.lai.members.model.Members;
//import com.islevilla.lai.members.model.MembersRepository;
//
//@Service
//public class MembersUserDetailsService implements UserDetailsService {
//	@Autowired
//    private MembersRepository membersRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Members member = membersRepository.findByMemberEmail(username)
//            .orElseThrow(() -> new UsernameNotFoundException("找不到會員: " + username));
//        return new MembersUserDetails(member);
//    }
//}
