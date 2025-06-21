package com.islevilla.jay.memberCoupon.model;

import com.islevilla.jay.coupon.model.Coupon;
import com.islevilla.lai.members.model.Members;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "member_coupon")
public class MemberCoupon {

    @EmbeddedId
    private MemberCouponId id;

    @ManyToOne
    @MapsId("memberId") // 對應複合主鍵裡的 memberId
    @JoinColumn(name = "member_id", nullable = false)
    private Members member;

    @ManyToOne
    @MapsId("couponId") // 對應複合主鍵裡的 couponId
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @Column(name = "used_date")
    private LocalDate usedDate; // 使用日期

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

    public Members getMember() {
        return member;
    }

    public void setMember(Members member) {
        this.member = member;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public LocalDate getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(LocalDate usedDate) {
        this.usedDate = usedDate;
    }
}

