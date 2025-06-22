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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Members member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", insertable = false, updatable = false)
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

