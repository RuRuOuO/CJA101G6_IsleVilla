package com.islevilla.jay.memberCoupon.model;

import com.islevilla.jay.coupon.model.Coupon;
import com.islevilla.member.model.Member;
import jakarta.persistence.*;

@Entity
@Table(name = "member_coupon")
public class MemberCoupon {

    @EmbeddedId
    private MemberCouponId id;

    @ManyToOne
    @MapsId("memberId") // 對應複合主鍵裡的 memberId
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @MapsId("couponId") // 對應複合主鍵裡的 couponId
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    public MemberCoupon() {}

    public MemberCoupon(MemberCouponId id) {
        this.id = id;
    }

    public MemberCouponId getId() {
        return id;
    }

    public void setId(MemberCouponId id) {
        this.id = id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }
}

