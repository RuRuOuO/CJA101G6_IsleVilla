package com.islevilla.jay.memberCoupon.model;

import com.islevilla.jay.coupon.model.Coupon;
import com.islevilla.lai.members.model.Members;
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
        return repository.findAllByOrderByUsedDateDesc();
    }

    public List<MemberCoupon> findByMemberId(Integer memberId) {
        return repository.findByMemberId(memberId);
    }

    public List<MemberCoupon> findByCouponId(Integer couponId) {
        return repository.findByCouponId(couponId);
    }

    public List<MemberCoupon> findMemberCouponsByMemberId(Integer memberId) {
        return repository.findMemberCouponsByMemberId(memberId);
    }

    // 檢查會員是否已使用過此優惠券
    public boolean hasUsedCoupon(Integer memberId, Integer couponId) {
        return repository.existsByMemberIdAndCouponId(memberId, couponId);
    }

    // 記錄優惠券使用（當會員使用優惠券時呼叫）
    public void recordCouponUsage(Integer memberId, Integer couponId) {
        MemberCoupon memberCoupon = new MemberCoupon();
        MemberCouponId id = new MemberCouponId(memberId, couponId);
        memberCoupon.setId(id);
        repository.save(memberCoupon);
    }

    // 標記優惠券為已使用
    @Transactional
    public void markCouponAsUsed(Integer memberId, Integer couponId) {
        try {
            // 檢查是否已經使用過此優惠券
            if (hasUsedCoupon(memberId, couponId)) {
                return; // 如果已經使用過，直接返回
            }
            
            // 建立新的會員優惠券使用記錄
            MemberCoupon memberCoupon = new MemberCoupon();
            MemberCouponId id = new MemberCouponId(memberId, couponId);
            memberCoupon.setId(id);
            memberCoupon.setUsedDate(java.time.LocalDate.now());
            
            repository.save(memberCoupon);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("標記優惠券使用失敗: " + e.getMessage());
        }
    }

    // 檢查優惠券是否可用（未過期且符合使用條件）
    public boolean isCouponValid(Coupon coupon, Integer orderAmount) {
        if (coupon == null) {
            return false;
        }

        java.time.LocalDate today = java.time.LocalDate.now();
        
        return orderAmount >= coupon.getMinSpend()
                && (coupon.getStartDate() == null || !coupon.getStartDate().isAfter(today))
                && (coupon.getEndDate() == null || !coupon.getEndDate().isBefore(today));
    }
    
    // 計算優惠券折扣金額
    public Integer calculateDiscount(Coupon coupon, Integer orderAmount) {
        if (!isCouponValid(coupon, orderAmount)) {
            return 0;
        }
        return coupon.getDiscountValue();
    }

    // 查詢會員可用的優惠券（未過期且符合使用條件）
    public List<Coupon> findValidCouponsByMemberId(Integer memberId, Integer orderAmount) {
        return repository.findValidCouponsByMemberId(memberId, orderAmount);
    }
} 