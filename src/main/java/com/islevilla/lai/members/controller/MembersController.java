package com.islevilla.lai.members.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

	/* ----------------------------登入------------------------------- */

	// 顯示登入頁面
	@GetMapping("/member/login")
	public String showLoginPage(@RequestParam(value = "redirect", required = false) String redirect, 
	                           HttpSession session, Model model) {
		// 如果用戶已經登入，重定向到首頁
		if (session.getAttribute("member") != null) {
			System.out.println("目前已經登入！\n目前登入中的會員：" + ((Members) session.getAttribute("member")).getMemberName());
			return "redirect:/";
		}
		System.out.println("進入登入頁面");
		
		// 將redirect參數傳遞給登入頁面
		if (redirect != null && !redirect.isEmpty()) {
			model.addAttribute("redirect", redirect);
		}
		
		return "front-end/member/member-login";
	}

	// 處理登入請求
	@PostMapping("/member/login")
	public String login(@RequestParam("memberEmail") String email, @RequestParam("memberPassword") String password,
			@RequestParam(value = "rememberMe", required = false) boolean rememberMe,
			@RequestParam(value = "redirect", required = false) String redirect,
			HttpSession session, RedirectAttributes redirectAttributes) {

		try {
			// 驗證電子信箱格式
			if (!isValidEmail(email)) {
				redirectAttributes.addFlashAttribute("error", "請輸入有效的電子信箱格式");
				return "redirect:/member/login";
			}

			// 驗證會員登入
			Members member = membersService.authenticateMember(email, password);

			if (member != null) {
				// 檢查會員狀態
				if (member.getMemberStatus() == 2) {
					redirectAttributes.addFlashAttribute("error", "您的帳號已被停用，請聯繫客服");
					System.out.println("您的帳號已被停用，請聯繫客服");
					return "redirect:/member/login";
				}

				// 登入成功，將會員資訊存入session
				session.setAttribute("member", member);
				System.out.println("session.getAttribute: " + session.getAttribute("member"));
				
				// 如果選擇記住我，設定較長的session timeout（可選）
//		                if (rememberMe) {
//              		      session.setMaxInactiveInterval(30 * 24 * 60 * 60); // 30天
//                		} else {
//		                    session.setMaxInactiveInterval(8 * 60 * 60); // 8小時
//		                }

				// 更新最後登入時間
				membersService.updateMemberLastLoginTime(member.getMemberId(), LocalDateTime.now());

				redirectAttributes.addFlashAttribute("success", "登入成功！歡迎回來");

				// 檢查是否有redirect參數，如果有就重定向到指定頁面
				if (redirect != null && !redirect.isEmpty()) {
					return "redirect:" + redirect;
				} else {
					return "redirect:/";
				}

			} else {
				redirectAttributes.addFlashAttribute("error", "電子信箱或密碼錯誤");
				return "redirect:/member/login";
			}

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "登入過程發生錯誤，請稍後再試");
			return "redirect:/member/login";
		}
	}

	/* ----------------------------註冊------------------------------- */

	// 顯示註冊頁面
	@GetMapping("/member/register")
	public String showRegisterPage(HttpSession session) {
		// 如果用戶已經登入，重定向到首頁
		if (session.getAttribute("member") != null) {
			System.out.println("目前已經登入！\n目前登入中的會員：" + ((Members) session.getAttribute("member")).getMemberName());
			return "redirect:/";
		}
		System.out.println("進入註冊頁面");
		return "front-end/member/member-register";
	}

	// 處理註冊請求
	@PostMapping("/member/register")
	public String register(@RequestParam("memberEmail") String memberEmail,
			@RequestParam("memberPassword") String memberPassword,
			@RequestParam("confirmPassword") String confirmPassword, @RequestParam("memberName") String memberName,
			@RequestParam("memberBirthdate") LocalDate memberBirthdate,
			@RequestParam("memberGender") Integer memberGender, @RequestParam("memberPhone") String memberPhone,
			@RequestParam("memberAddress") String memberAddress,
			@RequestParam(value = "memberPhoto", required = false) MultipartFile photoFile, Model model,
			RedirectAttributes redirectAttributes) {

		try {
			// Step 1. 驗證必填欄位
			if (memberEmail == null || memberEmail.trim().isEmpty()) {
				model.addAttribute("error", "請輸入電子信箱");
				return "front-end/member/member-register";
			}

			if (memberPassword == null || memberPassword.trim().isEmpty()) {
				model.addAttribute("error", "請輸入密碼");
				return "front-end/member/member-register";
			}

			if (memberName == null || memberName.trim().isEmpty()) {
				model.addAttribute("error", "請輸入姓名");
				return "front-end/member/member-register";
			}

			if (memberBirthdate == null) {
				model.addAttribute("error", "請選擇生日");
				return "front-end/member/member-register";
			}

			if (memberGender == null) {
				model.addAttribute("error", "請選擇性別");
				return "front-end/member/member-register";
			}

			if (memberPhone == null || memberPhone.trim().isEmpty()) {
				model.addAttribute("error", "請輸入電話號碼");
				return "front-end/member/member-register";
			}

			if (memberAddress == null || memberAddress.trim().isEmpty()) {
				model.addAttribute("error", "請輸入地址");
				return "front-end/member/member-register";
			}

			// Step 2. 各項驗證
			// Step 2.1 驗證電子信箱格式
			if (!isValidEmail(memberEmail)) {
				model.addAttribute("error", "請輸入有效的電子信箱格式");
				return "front-end/member/member-register";
			}

			// Step 2.2 驗證電子信箱是否已存在
			if (membersService.existsByEmail(memberEmail)) {
				model.addAttribute("error", "此電子信箱已被註冊");
				return "front-end/member/member-register";
			}

			// Step 2.3 驗證密碼確認
			if (!memberPassword.equals(confirmPassword)) {
				model.addAttribute("error", "密碼確認不一致");
				return "front-end/member/member-register";
			}

			// Step 2.4 驗證密碼強度
			if (!isValidPassword(memberPassword)) {
				model.addAttribute("error", "密碼至少需要8個字元，並包含英文字母和數字");
				return "front-end/member/member-register";
			}

			// Step 2.5 驗證姓名長度
			if (memberName.length() > 30) {
				model.addAttribute("error", "姓名長度不能超過30個字");
				return "front-end/member/member-register";
			}

			// Step 2.6 驗證年齡
			if (!isValidAge(memberBirthdate)) {
				model.addAttribute("error", "年齡必須滿18歲");
				return "front-end/member/member-register";
			}

			// Step 2.7 驗證性別值
			if (memberGender < 0 || memberGender > 2) {
				model.addAttribute("error", "請選擇有效的性別");
				return "front-end/member/member-register";
			}

			// Step 2.8 驗證電話號碼格式
			if (!isValidPhoneNumber(memberPhone)) {
				model.addAttribute("error", "請輸入有效的臺灣電話號碼");
				return "front-end/member/member-register";
			}

			// Step 2.9 驗證電話號碼是否已存在
			if (membersService.existsByPhone(memberPhone)) {
				model.addAttribute("error", "此電話號碼已被註冊");
				return "front-end/member/member-register";
			}

			// Step 2.10 驗證地址長度
			if (memberAddress.length() > 200) {
				model.addAttribute("error", "地址長度不能超過200個字");
				return "front-end/member/member-register";
			}

			// Step 3. 建立會員物件
			Members member = new Members();
			member.setMemberEmail(memberEmail); // 2. 會員信箱
			member.setMemberPasswordHash(pc.hashing(memberPassword)); // 3. 會員雜湊密碼
			member.setMemberName(memberName); // 4. 會員姓名
			member.setMemberBirthdate(memberBirthdate); // 5. 會員生日
			member.setMemberGender(memberGender); // 6. 會員性別
			member.setMemberPhone(memberPhone); // 7. 會員電話
			member.setMemberAddress(memberAddress); // 8. 會員地址

			// 處理照片上傳
			if (photoFile != null && !photoFile.isEmpty()) {
				byte[] photoData = handlePhotoUpload(photoFile);
				if (photoData == null) {
					model.addAttribute("error", "照片上傳失敗，請檢查檔案格式和大小（支援JPG、PNG、GIF，不超過5MB）");
					return "front-end/member/member-register";
				}
				member.setMemberPhoto(photoData); // 9. 會員照片
			}

			// 10. 會員建立日期
			// 11. 會員更新日期
			// 12. 會員最後登入時間
			// 13. 會員狀態 (預設為0:未驗證)

			// Step 4. 註冊會員
			System.out.println("準備註冊......");
			membersService.addMember(member);
			System.out.println("註冊成功！請使用您的帳號密碼登入");

			redirectAttributes.addFlashAttribute("success", "註冊成功！請使用您的帳號密碼登入");
			return "redirect:/member/login";

		} catch (Exception e) {
			System.err.println("註冊失敗：" + e.getMessage());
			model.addAttribute("error", "註冊過程發生錯誤，請稍後再試");
			return "front-end/member/member-register";
		}
	}

	// 顯示忘記密碼頁面
	@GetMapping("/member/forgot-password")
	public String showForgotPasswordPage(HttpSession session) {
		// 如果用戶已經登入，重定向到首頁
		if (session.getAttribute("member") != null) {
			System.out.println("目前已經登入！\n目前登入中的會員：" + ((Members) session.getAttribute("member")).getMemberName());
			return "redirect:/";
		}
		return "front-end/member/member-forgot-password";
	}

	// -------------------------------登出----------------------------------- //
	// 顯示登出確認頁面
	@GetMapping("/member/logout")
	public String showLogoutPage(HttpSession session, Model model) {
		// 檢查是否已登入
		Members member = (Members) session.getAttribute("member");
		if (member == null) {
			// 如果沒有登入，直接跳轉到登入頁面
			System.out.println("找不到登入紀錄，即將跳轉到登入頁面......");
			return "redirect:/member/login";
		}

		// 將會員資訊傳給頁面顯示
		model.addAttribute("member", member);
		return "front-end/member/member-logout";
	}

	// 處理登出請求
	@PostMapping("/member/logout")
	public String logout(HttpSession session, HttpServletRequest request, RedirectAttributes redirectAttributes) {

		try {
			// 獲取當前登入的會員資訊（記錄日誌用）
			Members member = (Members) session.getAttribute("member");

			if (member != null) {
				System.out.println("會員 " + member.getMemberName() + " (ID: " + member.getMemberId() + ") 於 "
						+ LocalDateTime.now() + " 登出");
			}

			// 清除 session
			session.invalidate();

			// 清除可能的 cookies（如果有使用記住我功能）
//            Cookie[] cookies = request.getCookies();
//            if (cookies != null) {
//                for (Cookie cookie : cookies) {
//                    if ("JSESSIONID".equals(cookie.getName()) || 
//                        "rememberMe".equals(cookie.getName())) {
//                        cookie.setMaxAge(0);
//                        cookie.setPath("/");
//                        // response.addCookie(cookie); // 需要在方法參數加入 HttpServletResponse response
//                    }
//                }
//            }

			// 添加成功訊息
			redirectAttributes.addFlashAttribute("logoutSuccess", true);
			redirectAttributes.addFlashAttribute("message", "您已成功登出，感謝您的使用！");

			return "redirect:/member/logout-success";

		} catch (Exception e) {
			System.err.println("登出過程發生錯誤: " + e.getMessage());
			e.printStackTrace();

			// 即使發生錯誤也要清除 session
			if (session != null) {
				try {
					session.invalidate();
				} catch (IllegalStateException ise) {
					// Session 可能已經無效，忽略此錯誤
				}
			}

			redirectAttributes.addFlashAttribute("error", "登出過程發生錯誤，但您已安全登出");
			return "redirect:/member/logout-success";
		}
	}

	// 顯示登出成功頁面
	@GetMapping("/member/logout-success")
	public String showLogoutSuccessPage(Model model) {
		return "front-end/member/member-logout-success";
	}

	// ----------------------------工具方法---------------------------- //

	// 有效電子郵件驗證
	private boolean isValidEmail(String email) {
		return email != null && email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
	}

	// 密碼強度驗證
	private boolean isValidPassword(String password) {
		return password != null && password.length() >= 8 && password.matches(".*[a-zA-Z].*")
				&& password.matches(".*\\d.*");
	}

	// 年齡驗證
	private boolean isValidAge(LocalDate birthdate) {
		if (birthdate == null)
			return false;
		int age = Period.between(birthdate, LocalDate.now()).getYears();
		return age >= 18 && age <= 120;
	}

	// 有效電話號碼驗證
	private boolean isValidPhoneNumber(String phone) {
		if (phone == null)
			return false;
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
			// logger.error("照片處理失敗: ", e); ← 移除這行
			System.out.println("照片處理失敗: " + e.getMessage()); // 或者使用System.out.println
			return null;
		}
	}

	// 驗證圖片類型
	private boolean isValidImageType(String contentType) {
		return contentType.equals("image/jpeg") || contentType.equals("image/jpg") || contentType.equals("image/png")
				|| contentType.equals("image/gif") || contentType.equals("image/webp");
	}
}
