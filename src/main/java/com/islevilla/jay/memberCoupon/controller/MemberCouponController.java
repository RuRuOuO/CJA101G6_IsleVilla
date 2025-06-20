package com.islevilla.jay.memberCoupon.controller;

import com.islevilla.jay.coupon.model.Coupon;
import com.islevilla.jay.coupon.model.CouponService;
import com.islevilla.jay.memberCoupon.model.MemberCoupon;
import com.islevilla.jay.memberCoupon.model.MemberCouponService;
import com.islevilla.member.model.Member;
import com.islevilla.member.model.MemberService;
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
    private MemberService memberSvc;

    @Autowired
    private CouponService couponSvc;

    // 顯示查詢頁面
    @GetMapping("select_page")
    public String selectPage(Model model) {
        return "back-end/member-coupon/select_page";
    }

    // 顯示所有會員優惠券
    @GetMapping("listAllMemberCoupon")
    public String listAllMemberCoupon(Model model) {
        List<MemberCoupon> list = memberCouponSvc.getAll();
        model.addAttribute("memberCouponListData", list);
        return "back-end/member-coupon/listAllMemberCoupon";
    }

    // 新增會員優惠券頁面
    @GetMapping("addMemberCoupon")
    public String addMemberCoupon(Model model) {
        MemberCoupon memberCoupon = new MemberCoupon();
        model.addAttribute("memberCoupon", memberCoupon);
        return "back-end/member-coupon/addMemberCoupon";
    }

    // 新增會員優惠券
    @PostMapping("insert")
    public String insert(@Valid @ModelAttribute("memberCoupon") MemberCoupon memberCoupon,
                        BindingResult result,
                        Model model) {
        if (result.hasErrors()) {
            return "back-end/member-coupon/addMemberCoupon";
        }
        memberCouponSvc.addMemberCoupon(memberCoupon);
        return "redirect:/member-coupon/listAllMemberCoupon";
    }

    // 更新會員優惠券頁面
    @GetMapping("getOne_For_Update")
    public String getOneForUpdate(@RequestParam("memberCouponId") Integer memberCouponId,
                                Model model) {
        MemberCoupon memberCoupon = memberCouponSvc.getOneMemberCoupon(memberCouponId);
        model.addAttribute("memberCoupon", memberCoupon);
        return "back-end/member-coupon/updateMemberCoupon";
    }

    // 更新會員優惠券
    @PostMapping("update")
    public String update(@Valid @ModelAttribute("memberCoupon") MemberCoupon memberCoupon,
                        BindingResult result,
                        Model model) {
        if (result.hasErrors()) {
            return "back-end/member-coupon/updateMemberCoupon";
        }
        memberCouponSvc.updateMemberCoupon(memberCoupon);
        return "redirect:/member-coupon/listAllMemberCoupon";
    }

    // 刪除會員優惠券
    @PostMapping("delete")
    public String delete(@RequestParam("memberCouponId") Integer memberCouponId) {
        memberCouponSvc.deleteMemberCoupon(memberCouponId);
        return "redirect:/member-coupon/listAllMemberCoupon";
    }

    // 查詢單筆會員優惠券
    @PostMapping("getOne_For_Display")
    public String getOneForDisplay(@RequestParam("memberCouponId") Integer memberCouponId,
                                 Model model) {
        MemberCoupon memberCoupon = memberCouponSvc.getOneMemberCoupon(memberCouponId);
        if (memberCoupon == null) {
            model.addAttribute("errorMessage", "查無資料");
            return "back-end/member-coupon/select_page";
        }
        model.addAttribute("memberCoupon", memberCoupon);
        model.addAttribute("getOne_For_Display", "true");
        return "back-end/member-coupon/listOneMemberCoupon";
    }

    // 提供下拉選單資料
    @ModelAttribute("memberListData")
    protected List<Member> referenceMemberListData() {
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