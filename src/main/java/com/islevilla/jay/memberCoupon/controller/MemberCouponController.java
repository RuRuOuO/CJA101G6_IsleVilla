package com.islevilla.jay.memberCoupon.controller;

import com.islevilla.jay.coupon.model.Coupon;
import com.islevilla.jay.coupon.model.CouponService;
import com.islevilla.jay.memberCoupon.model.MemberCoupon;
import com.islevilla.jay.memberCoupon.model.MemberCouponId;
import com.islevilla.jay.memberCoupon.model.MemberCouponService;
import com.islevilla.lai.members.model.Members;
import com.islevilla.lai.members.model.MembersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/member-coupon")
public class MemberCouponController {

    @Autowired
    private MemberCouponService memberCouponSvc;

    @Autowired
    private MembersService memberSvc;

    @Autowired
    private CouponService couponSvc;

    // // 顯示查詢頁面
    // @GetMapping("select_page")
    // public String selectPage(Model model) {
    //     return "back-end/member-coupon/select_page";
    // }

    // // 顯示會員使用過的優惠券記錄
    // @GetMapping("listAllMemberCoupon")
    // public String listAllMemberCoupon(@RequestParam(value = "memberId", required = false) Integer memberId,
    //                                 @RequestParam(value = "couponId", required = false) Integer couponId,
    //                                 Model model) {
    //     List<MemberCoupon> list;
        
    //     if (memberId != null && couponId != null) {
    //         // 查詢特定會員是否使用過特定優惠券
    //         MemberCouponId id = new MemberCouponId(memberId, couponId);
    //         MemberCoupon memberCoupon = memberCouponSvc.getOneMemberCoupon(id);
    //         if (memberCoupon != null) {
    //             list = List.of(memberCoupon);
    //         } else {
    //             list = List.of(); // 空列表
    //         }
    //         model.addAttribute("selectedMemberId", memberId);
    //         model.addAttribute("selectedCouponId", couponId);
    //     } else if (memberId != null) {
    //         // 顯示特定會員使用過的所有優惠券
    //         list = memberCouponSvc.findByMemberId(memberId);
    //         model.addAttribute("selectedMemberId", memberId);
    //     } else if (couponId != null) {
    //         // 顯示使用過特定優惠券的所有會員
    //         list = memberCouponSvc.findByCouponId(couponId);
    //         model.addAttribute("selectedCouponId", couponId);
    //     } else {
    //         // 如果沒有指定任何參數，顯示所有會員優惠券使用記錄
    //         list = memberCouponSvc.getAll();
    //     }
        
    //     model.addAttribute("memberCouponListData", list);
    //     return "back-end/member-coupon/listAllMemberCoupon";
    // }

    // // 新增會員優惠券頁面
    // @GetMapping("addMemberCoupon")
    // public String addMemberCoupon(Model model) {
    //     MemberCoupon memberCoupon = new MemberCoupon();
    //     model.addAttribute("memberCoupon", memberCoupon);
    //     return "back-end/member-coupon/addMemberCoupon";
    // }

    // // 新增會員優惠券
    // @PostMapping("insert")
    // public String insert(@Valid @ModelAttribute("memberCoupon") MemberCoupon memberCoupon,
    //                     BindingResult result,
    //                     Model model) {
    //     if (result.hasErrors()) {
    //         return "back-end/member-coupon/addMemberCoupon";
    //     }
    //     memberCouponSvc.addMemberCoupon(memberCoupon);
    //     return "redirect:/member-coupon/listAllMemberCoupon";
    // }

    // // 更新會員優惠券頁面
    // @GetMapping("getOne_For_Update")
    // public String getOneForUpdate(@RequestParam("memberId") Integer memberId,
    //                             @RequestParam("couponId") Integer couponId,
    //                             Model model) {
    //     MemberCouponId id = new MemberCouponId(memberId, couponId);
    //     MemberCoupon memberCoupon = memberCouponSvc.getOneMemberCoupon(id);
    //     model.addAttribute("memberCoupon", memberCoupon);
    //     return "back-end/member-coupon/updateMemberCoupon";
    // }

    // // 更新會員優惠券
    // @PostMapping("update")
    // public String update(@Valid @ModelAttribute("memberCoupon") MemberCoupon memberCoupon,
    //                     BindingResult result,
    //                     Model model) {
    //     if (result.hasErrors()) {
    //         return "back-end/member-coupon/updateMemberCoupon";
    //     }
    //     memberCouponSvc.updateMemberCoupon(memberCoupon);
    //     return "redirect:/member-coupon/listAllMemberCoupon";
    // }

    // // 刪除會員優惠券
    // @PostMapping("delete")
    // public String delete(@RequestParam("memberId") Integer memberId,
    //                     @RequestParam("couponId") Integer couponId) {
    //     MemberCouponId id = new MemberCouponId(memberId, couponId);
    //     memberCouponSvc.deleteMemberCoupon(id);
    //     return "redirect:/member-coupon/listAllMemberCoupon";
    // }

    // // 查詢單筆會員優惠券
    // @PostMapping("getOne_For_Display")
    // public String getOneForDisplay(@RequestParam("memberId") Integer memberId,
    //                              @RequestParam("couponId") Integer couponId,
    //                              Model model) {
    //     MemberCouponId id = new MemberCouponId(memberId, couponId);
    //     MemberCoupon memberCoupon = memberCouponSvc.getOneMemberCoupon(id);
    //     if (memberCoupon == null) {
    //         model.addAttribute("errorMessage", "查無資料");
    //         return "back-end/member-coupon/select_page";
    //     }
    //     model.addAttribute("memberCoupon", memberCoupon);
    //     model.addAttribute("getOne_For_Display", "true");
    //     return "back-end/member-coupon/listOneMemberCoupon";
    // }

    // 提供下拉選單資料
    @ModelAttribute("memberListData")
    protected List<Members> referenceMemberListData() {
        return memberSvc.getAll();
    }

    @ModelAttribute("couponListData")
    protected List<Coupon> referenceCouponListData() {
        return couponSvc.getAll();
    }

    // 異常處理
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ModelAndView handleError(HttpServletRequest req,
                                  ConstraintViolationException e,
                                  Model model) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        StringBuilder strBuilder = new StringBuilder();
        for (ConstraintViolation<?> violation : violations) {
            strBuilder.append(violation.getMessage()).append("<br>");
        }

        List<MemberCoupon> list = memberCouponSvc.getAll();
        model.addAttribute("memberCouponListData", list);
        String message = strBuilder.toString();
        return new ModelAndView("back-end/member-coupon/select_page",
                              "errorMessage",
                              "請修正以下錯誤:<br>" + message);
    }
} 