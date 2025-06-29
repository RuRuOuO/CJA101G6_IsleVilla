package com.islevilla.jay.coupon.controller;

import com.islevilla.jay.coupon.model.Coupon;
import com.islevilla.jay.memberCoupon.model.MemberCouponService;
import com.islevilla.lai.members.model.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/coupon")
public class CouponApiController {

    @Autowired
    private MemberCouponService memberCouponService;

    // 查詢會員可用優惠券
    @GetMapping("/available")
    public List<Coupon> getAvailableCoupons(HttpSession session, @RequestParam Integer orderAmount) {
        Members member = (Members) session.getAttribute("member");
        if (member == null) {
            return Collections.emptyList();
        }
        return memberCouponService.findValidCouponsByMemberId(member.getMemberId(), orderAmount);
    }

    // 結帳頁面: 當會員進入結帳頁面時，前端可以調用此API獲取可用的優惠券
    // 動態優惠券顯示: 根據訂單金額動態顯示可用的優惠券選項
    // 優惠券篩選: 只顯示符合最低消費門檻的優惠券
} 