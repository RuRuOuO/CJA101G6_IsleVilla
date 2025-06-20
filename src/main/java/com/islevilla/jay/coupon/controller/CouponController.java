package com.islevilla.jay.coupon.controller;

import com.islevilla.jay.coupon.model.Coupon;
import com.islevilla.jay.coupon.model.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text == null || text.isEmpty()) {
                    setValue(null);
                } else {
                    setValue(LocalDateTime.parse(text, dtf));
                }
            }
        });
    }

    // 顯示優惠券列表頁面
    @GetMapping("/list")
    public String listAllCoupons(
            @RequestParam(required = false) String couponCode,
            @RequestParam(required = false) Integer minSpend,
            Model model) {
        List<Coupon> couponList;
        
        if (couponCode != null && !couponCode.isEmpty()) {
            couponList = couponService.findByCouponCodeContaining(couponCode);
        } else if (minSpend != null) {
            couponList = couponService.findByMinSpend(minSpend);
        } else {
            couponList = couponService.getAll();
        }
        
        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        java.util.Map<Integer, String> startDateMap = new java.util.HashMap<>();
        java.util.Map<Integer, String> endDateMap = new java.util.HashMap<>();
        for (Coupon c : couponList) {
            startDateMap.put(c.getCouponId(), c.getStartDate() != null ? c.getStartDate().format(dtf) : "");
            endDateMap.put(c.getCouponId(), c.getEndDate() != null ? c.getEndDate().format(dtf) : "");
        }
        model.addAttribute("couponListData", couponList);
        model.addAttribute("startDateMap", startDateMap);
        model.addAttribute("endDateMap", endDateMap);
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
        // System.out.println("couponId=" + coupon.getCouponId());
        // System.out.println("couponCode=" + coupon.getCouponCode());
        // System.out.println("discountValue=" + coupon.getDiscountValue());
        // System.out.println("minSpend=" + coupon.getMinSpend());
        // System.out.println("startDate=" + coupon.getStartDate());
        // System.out.println("endDate=" + coupon.getEndDate());
        if (coupon.getCouponId() == null) {
            return "redirect:/coupon/list";
        }
        // 只允許更新已存在的資料
        Coupon dbCoupon = couponService.findById(coupon.getCouponId()).orElse(null);
        if (dbCoupon == null) {
            return "redirect:/coupon/list";
        }
        // 更新欄位
        dbCoupon.setCouponCode(coupon.getCouponCode());
        dbCoupon.setDiscountValue(coupon.getDiscountValue());
        dbCoupon.setMinSpend(coupon.getMinSpend());
        dbCoupon.setStartDate(coupon.getStartDate());
        dbCoupon.setEndDate(coupon.getEndDate());
        couponService.save(dbCoupon);
        return "redirect:/coupon/list";
    }

    // 刪除優惠券
    @GetMapping("/delete")
    public String deleteCoupon(@RequestParam("couponId") Integer couponId) {
        couponService.deleteById(couponId);
        return "redirect:/coupon/list";
    }
}



