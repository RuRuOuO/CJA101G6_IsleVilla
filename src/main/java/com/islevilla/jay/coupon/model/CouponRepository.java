package com.islevilla.jay.coupon.model;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    @Transactional
    @Modifying
    @Query(value = "delete from coupon where coupon_id =?1", nativeQuery = true)
    void deleteByCouponId(int couponId);

    // 根據優惠券代碼查詢
    List<Coupon> findByCouponCode(String couponCode);
    
    // 根據最低消費金額查詢
    List<Coupon> findByMinSpend(Integer minSpend);

    // 模糊查詢優惠券代碼
    List<Coupon> findByCouponCodeContaining(String keyword);

    // 根據狀態查詢優惠券
    List<Coupon> findByCouponStatus(Byte couponStatus);
}

