package com.islevilla.jay.coupon.controller;

import com.islevilla.jay.coupon.model.Coupon;
import com.islevilla.jay.coupon.model.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
    public String updateCoupon(@ModelAttribute Coupon coupon, Model model) {
        java.util.List<String> errorMessages = new java.util.ArrayList<>();

        // 1. 檢查優惠券代碼是否重複（排除自己）
        java.util.List<Coupon> sameCodeList = couponService.findByCouponCode(coupon.getCouponCode());
        boolean codeExists = sameCodeList.stream()
            .anyMatch(c -> !c.getCouponId().equals(coupon.getCouponId()));
        if (codeExists) {
            errorMessages.add("優惠券代碼已存在，請重新輸入。");
        }
        // 2. 檢查日期邏輯
        java.time.LocalDate today = java.time.LocalDate.now();
        if (coupon.getStartDate() == null || coupon.getEndDate() == null) {
            errorMessages.add("請輸入完整的啟用日期與結束日期。");
        } else {
            if (coupon.getStartDate().isBefore(today)) {
                errorMessages.add("啟用日期不能小於今日日期。");
            }
            if (coupon.getEndDate().isBefore(coupon.getStartDate())) {
                errorMessages.add("結束日期不能小於啟用日期。");
            }
        }
        // 3. 折扣金額要小於等於最低消費金額
        if (coupon.getDiscountValue() == null || coupon.getMinSpend() == null || coupon.getDiscountValue() > coupon.getMinSpend()) {
            errorMessages.add("折扣金額必須小於或等於最低消費金額。");
        }

        if (!errorMessages.isEmpty()) {
            // 取得所有優惠券資料
            java.util.List<Coupon> couponList = couponService.getAll();
            java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
            java.util.Map<Integer, String> startDateMap = new java.util.HashMap<>();
            java.util.Map<Integer, String> endDateMap = new java.util.HashMap<>();
            for (Coupon c : couponList) {
                startDateMap.put(c.getCouponId(), c.getStartDate() != null ? c.getStartDate().format(dtf) : "");
                endDateMap.put(c.getCouponId(), c.getEndDate() != null ? c.getEndDate().format(dtf) : "");
            }
            java.util.Map<Integer, java.util.List<String>> errorMap = new java.util.HashMap<>();
            errorMap.put(coupon.getCouponId(), errorMessages);
            model.addAttribute("couponListData", couponList);
            model.addAttribute("startDateMap", startDateMap);
            model.addAttribute("endDateMap", endDateMap);
            model.addAttribute("rowErrorMap", errorMap);
            return "back-end/coupon/listAllCoupon";
        }

        // 更新資料
        Coupon dbCoupon = couponService.findById(coupon.getCouponId()).orElse(null);
        if (dbCoupon == null) {
            return "redirect:/coupon/list";
        }
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

    // 顯示新增優惠券頁面
    @GetMapping("/add")
    public String showAddCouponPage(Model model) {
        Coupon coupon = new Coupon();
        coupon.setStartDate(java.time.LocalDate.now());
        model.addAttribute("coupon", coupon);
        model.addAttribute("startDateStr", coupon.getStartDate().toString());
        return "back-end/coupon/addCoupon";
    }

    // 處理新增優惠券表單
    @PostMapping("/add")
    public String addCoupon(@ModelAttribute Coupon coupon, Model model) {
        java.util.List<String> errorMessages = new java.util.ArrayList<>();

        // 1. 檢查優惠券代碼是否重複
        boolean codeExists = !couponService.findByCouponCode(coupon.getCouponCode()).isEmpty();
        if (codeExists) {
            errorMessages.add("優惠券代碼已存在，請重新輸入。");
        }
        // 2. 檢查日期邏輯
        java.time.LocalDate today = java.time.LocalDate.now();
        if (coupon.getStartDate() == null || coupon.getEndDate() == null) {
            errorMessages.add("請輸入完整的啟用日期與結束日期。");
        } else {
            if (coupon.getStartDate().isBefore(today)) {
                errorMessages.add("啟用日期不能小於今日日期。");
            }
            if (coupon.getEndDate().isBefore(coupon.getStartDate())) {
                errorMessages.add("結束日期不能小於啟用日期。");
            }
        }
        // 3. 折扣金額要小於等於最低消費金額
        if (coupon.getDiscountValue() == null || coupon.getMinSpend() == null || coupon.getDiscountValue() > coupon.getMinSpend()) {
            errorMessages.add("折扣金額必須小於或等於最低消費金額。");
        }

        if (!errorMessages.isEmpty()) {
            model.addAttribute("coupon", coupon);
            model.addAttribute("errorMessages", errorMessages);
            // model.addAttribute("startDateStr", coupon.getStartDate() != null ? coupon.getStartDate().toString() : "");
            // model.addAttribute("endDateStr", coupon.getEndDate() != null ? coupon.getEndDate().toString() : "");
            return "back-end/coupon/addCoupon";
        }

        couponService.save(coupon);
        return "redirect:/coupon/list";
    }
}



