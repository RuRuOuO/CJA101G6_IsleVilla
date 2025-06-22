package com.islevilla.jay.memberCoupon.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, MemberCouponId> {
    
    // 根據會員ID查詢優惠券
    @Query("SELECT mc FROM MemberCoupon mc WHERE mc.id.memberId = :memberId ORDER BY mc.usedDate DESC")
    List<MemberCoupon> findByMemberId(@Param("memberId") Integer memberId);
    
    // 根據優惠券ID查詢會員
    @Query("SELECT mc FROM MemberCoupon mc WHERE mc.id.couponId = :couponId ORDER BY mc.usedDate DESC")
    List<MemberCoupon> findByCouponId(@Param("couponId") Integer couponId);
    
    // 刪除特定會員的特定優惠券
    @Transactional
    @Modifying
    @Query("DELETE FROM MemberCoupon mc WHERE mc.id.memberId = :memberId AND mc.id.couponId = :couponId")
    void deleteByMemberIdAndCouponId(@Param("memberId") Integer memberId, @Param("couponId") Integer couponId);
    
    // 檢查會員是否擁有特定優惠券
    @Query("SELECT COUNT(mc) > 0 FROM MemberCoupon mc WHERE mc.id.memberId = :memberId AND mc.id.couponId = :couponId")
    boolean existsByMemberIdAndCouponId(@Param("memberId") Integer memberId, @Param("couponId") Integer couponId);
    
    // 查詢會員可用的優惠券（未過期且符合使用條件）
    @Query("SELECT mc FROM MemberCoupon mc WHERE mc.id.memberId = :memberId " +
           "AND mc.coupon.startDate <= CURRENT_DATE " +
           "AND mc.coupon.endDate >= CURRENT_DATE " +
           "AND mc.coupon.minSpend <= :orderAmount")
    List<MemberCoupon> findValidCouponsByMemberId(@Param("memberId") Integer memberId, @Param("orderAmount") Integer orderAmount);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM member_coupon WHERE member_id = ?1 AND coupon_id = ?2", nativeQuery = true)
    void deleteByCompositeDetail(Integer memberId, Integer couponId);

    @Query("SELECT mc FROM MemberCoupon mc WHERE mc.member.memberId = :memberId ORDER BY mc.usedDate DESC")
    List<MemberCoupon> findMemberCouponsByMemberId(@Param("memberId") Integer memberId);

    // 查詢所有會員優惠券，依使用日期降冪排序
    List<MemberCoupon> findAllByOrderByUsedDateDesc();
} 