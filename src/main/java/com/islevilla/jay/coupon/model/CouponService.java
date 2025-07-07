package com.islevilla.jay.coupon.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public List<Coupon> getAll() {
        return couponRepository.findAll();
    }

    public Optional<Coupon> findById(Integer couponId) {
        return couponRepository.findById(couponId);
    }

    public Coupon save(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public void deleteById(Integer couponId) {
        couponRepository.deleteById(couponId);
    }

    // 根據優惠券代碼查詢
    public List<Coupon> findByCouponCode(String couponCode) {
        return couponRepository.findByCouponCode(couponCode);
    }

    // 根據最低消費金額查詢
    public List<Coupon> findByMinSpend(Integer minSpend) {
        return couponRepository.findByMinSpend(minSpend);
    }

    // 模糊查詢優惠券代碼
    public List<Coupon> findByCouponCodeContaining(String keyword) {
        return couponRepository.findByCouponCodeContaining(keyword);
    }

    public List<Coupon> findByCouponStatus(Byte couponStatus) {
        return couponRepository.findByCouponStatus(couponStatus);
    }
}


