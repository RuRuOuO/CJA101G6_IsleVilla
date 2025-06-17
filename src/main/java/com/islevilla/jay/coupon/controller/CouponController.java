package com.islevilla.jay.coupon.controller;

import com.islevilla.jay.coupon.model.Coupon;
import com.islevilla.jay.coupon.model.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    // 顯示優惠券列表頁面
    @GetMapping("/list")
    public String listAllCoupons(
            @RequestParam(required = false) String couponCode,
            @RequestParam(required = false) Integer minSpend,
            Model model) {
        List<Coupon> couponList;
        
        if (couponCode != null && !couponCode.isEmpty()) {
            couponList = couponService.findByCouponCode(couponCode);
        } else if (minSpend != null) {
            couponList = couponService.findByMinSpend(minSpend);
        } else {
            couponList = couponService.getAll();
        }
        
        model.addAttribute("couponListData", couponList);
        return "back-end/coupon/listAllCoupon";
    }

    // 顯示查詢頁面
    @GetMapping("/select_page")
    public String showSelectPage() {
        return "back-end/coupon/select_page";
    }

    // 獲取單個優惠券用於更新
    @GetMapping("/getOneForUpdate")
    public String getOneForUpdate(@RequestParam("couponId") Integer couponId, Model model) {
        Coupon coupon = couponService.findById(couponId).orElse(null);
        model.addAttribute("coupon", coupon);
        return "back-end/coupon/update_coupon";
    }

    // 更新優惠券
    @PostMapping("/update")
    public String updateCoupon(@ModelAttribute Coupon coupon) {
        couponService.save(coupon);
        return "redirect:/coupon/list";
    }

    // 刪除優惠券
    @GetMapping("/delete")
    public String deleteCoupon(@RequestParam("couponId") Integer couponId) {
        couponService.deleteById(couponId);
        return "redirect:/coupon/list";
    }
}



