package com.islevilla.jay.memberCoupon.model;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MemberCouponId implements Serializable {

    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "coupon_id")
    private Integer couponId;

    public MemberCouponId() {}

    public MemberCouponId(Integer memberId, Integer couponId) {
        this.memberId = memberId;
        this.couponId = couponId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberCouponId)) return false;
        MemberCouponId that = (MemberCouponId) o;
        return Objects.equals(memberId, that.memberId) &&
                Objects.equals(couponId, that.couponId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, couponId);
    }
}

