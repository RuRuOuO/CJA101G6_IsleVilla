package com.islevilla.jay.memberCoupon.model;

import com.islevilla.jay.coupon.model.Coupon;
import com.islevilla.member.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("memberCouponService")
public class MemberCouponService {

    @Autowired
    MemberCouponRepository repository;

    public MemberCoupon addMemberCoupon(MemberCoupon memberCoupon) {
        return repository.save(memberCoupon);
    }

    @Transactional
    public void updateMemberCoupon(MemberCoupon memberCoupon) {
        MemberCouponId id = memberCoupon.getId();
        MemberCoupon existingMemberCoupon = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到會員優惠券記錄"));

        repository.save(existingMemberCoupon);
    }

    public MemberCoupon getOneMemberCoupon(MemberCouponId id) {
        Optional<MemberCoupon> optional = repository.findById(id);
        return optional.orElse(null);
    }

    @Transactional
    public void deleteMemberCoupon(MemberCouponId id) {
        try {
            if (repository.existsById(id)) {
                repository.deleteByCompositeDetail(
                    id.getMemberId(),
                    id.getCouponId()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MemberCoupon> getAll() {
        return repository.findAll();
    }

    public List<MemberCoupon> findByMemberId(Integer memberId) {
        return repository.findMemberCouponsByMemberId(memberId);
    }

    // 檢查會員是否已使用過此優惠券
    public boolean hasUsedCoupon(Integer memberId, Integer couponId) {
        return repository.existsByMemberIdAndCouponId(memberId, couponId);
    }

    // 記錄優惠券使用
    public void recordCouponUsage(Integer memberId, Integer couponId) {
        MemberCoupon memberCoupon = new MemberCoupon();
        MemberCouponId id = new MemberCouponId(memberId, couponId);
        memberCoupon.setId(id);
        repository.save(memberCoupon);
    }

    // 檢查優惠券是否可用（未過期且符合使用條件）
    public boolean isCouponValid(MemberCoupon memberCoupon, Integer orderAmount) {
        if (memberCoupon == null || memberCoupon.getCoupon() == null) {
            return false;
        }

        Coupon coupon = memberCoupon.getCoupon();
        return orderAmount >= coupon.getMinSpend() &&
               coupon.getStartDate().isBefore(java.time.LocalDateTime.now()) &&
               coupon.getEndDate().isAfter(java.time.LocalDateTime.now());
    }

    // 計算優惠券折扣金額
    public Integer calculateDiscount(MemberCoupon memberCoupon, Integer orderAmount) {
        if (!isCouponValid(memberCoupon, orderAmount)) {
            return 0;
        }
        return memberCoupon.getCoupon().getDiscountValue();
    }
} 