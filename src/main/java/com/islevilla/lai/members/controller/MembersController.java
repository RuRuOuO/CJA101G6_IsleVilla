package com.islevilla.lai.members.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.islevilla.lai.members.model.Members;
import com.islevilla.lai.members.model.MembersService;
import com.islevilla.lai.util.PasswordConvert;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class MembersController {
	@Autowired
	private MembersService membersService;
	
	@Autowired
    private PasswordConvert pc;
	
    /* ----------------------------登入-------------------------------  */
	
	// 顯示登入頁面
	@GetMapping("/member-login")
    public String showLoginPage(Model model, HttpSession session) {
        // 如果用戶已經登入，重定向到首頁
        if (session.getAttribute("member") != null) {
            return "redirect:/";
        }
        return "front-end/member/member-login";
    }
	
	// 處理登入
//    @PostMapping("/member-login") // 待修改
//    public String login(@RequestParam String account,
//                        @RequestParam String password,
//                        HttpServletRequest request,
//                        Model model) {
//
//        Members members = membersService.getMemberByEmail(account);
//        if (members == null || !membersService.login(members.getMemberEmail(), password)) {
//            model.addAttribute("errorMsg", "登入失敗，請檢查帳號或密碼！");
//            return "front-end/member/member-login"; // 回到 member-login.html
//        }
//
//        // 登入成功
//        HttpSession session = request.getSession();
//        session.setAttribute("account", members.getMemberId());
//        System.out.println(session.getAttribute("account"));
//
//        String location = (String) session.getAttribute("location");
//        return (location != null ? location : "/testlogin/testlogin");
//    }
    
	// 處理登入請求
    @PostMapping("/member-login")
    public String login(@RequestParam("memberEmail") String email,
                       @RequestParam("memberPassword") String password,
                       @RequestParam(value = "rememberMe", required = false) boolean rememberMe,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        
        try {
            // 驗證電子信箱格式
            if (!isValidEmail(email)) {
                redirectAttributes.addFlashAttribute("error", "請輸入有效的電子信箱格式");
                return "redirect:/member-login";
            }

            // 驗證會員登入
            Members member = membersService.authenticateMember(email, password);
            
            if (member != null) {
                // 檢查會員狀態
                if (member.getMemberStatus() == 2) {
                    redirectAttributes.addFlashAttribute("error", "您的帳號已被停用，請聯繫客服");
                    System.out.println("您的帳號已被停用，請聯繫客服");
                    return "redirect:/member-login";
                }
                
                // 登入成功，將會員資訊存入session
                session.setAttribute("member", member);
                System.out.println("session.getAttribute: " + session.getAttribute("member"));
                
                // 如果選擇記住我，設定較長的session timeout（可選）
//                if (rememberMe) {
//                    session.setMaxInactiveInterval(30 * 24 * 60 * 60); // 30天
//                } else {
//                    session.setMaxInactiveInterval(8 * 60 * 60); // 8小時
//                }
                
                // 更新最後登入時間
                membersService.updateMemberLastLoginTime(member.getMemberId(), LocalDateTime.now());
                
                redirectAttributes.addFlashAttribute("success", "登入成功！歡迎回來");

                return "redirect:/";
                
                
            } else {
                redirectAttributes.addFlashAttribute("error", "電子信箱或密碼錯誤");
                return "redirect:/member-login";
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "登入過程發生錯誤，請稍後再試");
            return "redirect:/member-login";
        }
    }
    
    /* ----------------------------註冊-------------------------------  */
    
    // 顯示註冊頁面
    @GetMapping("/member-register")
    public String showRegisterPage(Model model, HttpSession session) {
        // 如果用戶已經登入，重定向到首頁
        if (session.getAttribute("member") != null) {
            return "redirect:/";
        }
        
        model.addAttribute("member", new Members());
        return "front-end/member/member-register";
    }
    
 // 處理註冊請求 (施工中)
    @PostMapping("/member-register")
    public String register(@ModelAttribute Members members,
                          BindingResult bindingResult,
                          @RequestParam("memberEmail") String memberEmail,
                          @RequestParam("memberPassword") String memberPassword,
                          @RequestParam("confirmPassword") String confirmPassword,
                          @RequestParam("memberName") String memberName,                          
                          @RequestParam("memberBirthdate") LocalDate memberBirthdate,
                          @RequestParam("memberGender") Integer memberGender,
                          @RequestParam("memberPhone") String memberPhone,
                          @RequestParam("memberAddress") String memberAddress,
                          @RequestParam(value = "memberPhoto", required = false) MultipartFile photoFile,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        
        try {
            // 後端驗證
            if (bindingResult.hasErrors()) {
                model.addAttribute("error", "請檢查輸入的資料格式");
                return "member-register";
            }

            // 驗證電子信箱是否已存在
            if (membersService.existsByEmail(memberEmail)) {
                model.addAttribute("error", "此電子信箱已被註冊");
                return "member-register";
            }

            // 驗證密碼確認
            if (!memberPassword.equals(confirmPassword)) {
                model.addAttribute("error", "密碼確認不一致");
                return "member-register";
            }

            // 驗證密碼強度
            if (!isValidPassword(memberPassword)) {
                model.addAttribute("error", "密碼至少需要8個字元，並包含英文字母和數字");
                return "member-register";
            }

            // 驗證年齡
            if (!isValidAge(memberBirthdate)) {
                model.addAttribute("error", "年齡必須滿13歲");
                return "member-register";
            }

            // 驗證電話號碼
            if (!isValidPhoneNumber(memberPhone)) {
                model.addAttribute("error", "請輸入有效的台灣電話號碼");
                return "member-register";
            }
            
            // 驗證電話號碼是否已存在
            if (membersService.existsByPhone(memberPhone)) {
                model.addAttribute("error", "此電話號碼已被註冊");
                return "member-register";
            }

            // 處理照片上傳
            if (photoFile != null && !photoFile.isEmpty()) {
                byte[] photoData = handlePhotoUpload(photoFile);
                if (photoData == null) {
                    model.addAttribute("error", "照片上傳失敗，請檢查檔案格式和大小");
                    return "member-register";
                }
                members.setMemberPhoto(photoData); // 9. 會員照片
            }
            
            // 設定接收到的參數到 members 物件
            members.setMemberEmail(memberEmail); // 2. 會員信箱
            members.setMemberPasswordHash(pc.hashing(memberPassword)); // 3. 會員雜湊密碼
            members.setMemberName(memberName);   // 4. 會員姓名
            members.setMemberBirthdate(memberBirthdate); // 5. 會員生日
            members.setMemberGender(memberGender); // 6. 會員性別
            members.setMemberPhone(memberPhone); // 7. 會員電話
            members.setMemberAddress(memberAddress); // 8. 會員地址
            // 9. 會員照片(在前面)
            members.setMemberCreatedAt(LocalDateTime.now()); // 10. 會員建立日期
            members.setMemberUpdatedAt(LocalDateTime.now()); // 11. 會員更新日期
            members.setMemberLastLoginTime(LocalDateTime.now()); // 12. 會員最後登入時間
            members.setMemberStatus(0); // 會員狀態 (預設為0:未驗證)

            // 註冊會員
            System.out.println("準備註冊");
            membersService.addMember(members);
            System.out.println("註冊成功");
            
            redirectAttributes.addFlashAttribute("success", "註冊成功！請使用您的帳號密碼登入");
            return "redirect:/member-login";

        } catch (Exception e) {
        	System.out.println("註冊失敗！\n錯誤訊息：" + e);
        	model.addAttribute("error", "註冊過程發生錯誤，請稍後再試");
            System.out.println("註冊失敗！\n錯誤訊息：" + e);
//            return "redirect:/member-register";
            return "front-end/member/member-register";
        }
    }
    
    
    
    
    
    
    
    
    
    // 顯示忘記密碼頁面
    @GetMapping("/member-forgot-password")
    public String showForgotPasswordPage(HttpSession session) {
        // 如果用戶已經登入，重定向到首頁
        if (session.getAttribute("member") != null) {
            return "redirect:/";
        }
        return "front-end/member/member-forgot-password";
    }
    
    // 處理登出
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // false: 如果沒 session 就不建立
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login"; // 回登入畫面
    }

    @GetMapping("/testlogin")
    public String testLogin(HttpServletRequest request) {

        return "redirect:/testlogin/testlogin"; // 回登入畫面
    }
    
 // 工具方法
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }
    
    // 密碼強度驗證
    private boolean isValidPassword(String password) {
        return password != null && 
               password.length() >= 8 && 
               password.matches(".*[a-zA-Z].*") && 
               password.matches(".*\\d.*");
    }
    
    // 年齡驗證
    private boolean isValidAge(LocalDate birthdate) {
        if (birthdate == null) return false;
        int age = Period.between(birthdate, LocalDate.now()).getYears();
        return age >= 18 && age <= 120;
    }
    
    // 有效電話號碼驗證
    private boolean isValidPhoneNumber(String phone) {
        if (phone == null) return false;
        String cleanPhone = phone.replaceAll("[\\-\\s]", "");
        return cleanPhone.matches("^(\\+886|0)?[2-9]\\d{7,8}$|^(\\+886|0)?9\\d{8}$");
    }
    
    private byte[] handlePhotoUpload(MultipartFile file) {
        try {
            // 檢查檔案是否為空
            if (file == null || file.isEmpty()) {
                return null;
            }

            // 檢查檔案類型
            String contentType = file.getContentType();
            if (contentType == null || !isValidImageType(contentType)) {
                return null;
            }

            // 檢查檔案大小 (5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return null;
            }

            // 檢查檔案名稱
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.trim().isEmpty()) {
                return null;
            }

            // 將檔案轉換為byte[]
            return file.getBytes();

        } catch (IOException e) {
            // logger.error("照片處理失敗: ", e);  ← 移除這行
            System.out.println("照片處理失敗: " + e.getMessage()); // 或者使用System.out.println
            return null;
        }
    }

    // 驗證圖片類型
    private boolean isValidImageType(String contentType) {
        return contentType.equals("image/jpeg") ||
               contentType.equals("image/jpg") ||
               contentType.equals("image/png") ||
               contentType.equals("image/gif") ||
               contentType.equals("image/webp");
    }
}
