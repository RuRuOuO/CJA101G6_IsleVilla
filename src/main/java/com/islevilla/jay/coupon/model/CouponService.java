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

    public Optional<Coupon> findById(Integer id) {
        return couponRepository.findById(id);
    }

    public Coupon save(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public void deleteById(Integer id) {
        couponRepository.deleteById(id);
    }
}


