//package com.islevilla.lai.auth;
//
//import java.util.Collection;
//import java.util.Collections;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import com.islevilla.lai.members.model.Members;
//
//public class MembersUserDetails implements UserDetails {
//	
//	private final Members member;
//
//    public MembersUserDetails(Members member) {
//        this.member = member;
//    }
//
//    public Members getMember() {
//        return member;
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        // 回傳會員的權限，這裡先簡單給 MEMBER 權限
//        return Collections.singleton(() -> "ROLE_MEMBER");
//    }
//
//    @Override
//    public String getPassword() {
//        return member.getMemberPasswordHash(); // 對應你資料庫的密碼欄位
//    }
//
//    @Override
//    public String getUsername() {
//        return member.getMemberEmail(); // 用 email 當作登入帳號
//    }
//
//    @Override public boolean isAccountNonExpired() { return true; }
//    @Override public boolean isAccountNonLocked() { return true; }
//    @Override public boolean isCredentialsNonExpired() { return true; }
//    @Override public boolean isEnabled() { return member.getMemberStatus() == 1; } // 1 為已驗證
//}
