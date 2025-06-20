package com.islevilla.jay.coupon.model;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CouponRepository extends JpaRepository<com.islevilla.coupon.model.Coupon, Integer> {
    @Transactional
    @Modifying
    @Query(value = "delete from coupon where coupon_id =?1", nativeQuery = true)
    void deleteByCouponId(int couponId);
}

