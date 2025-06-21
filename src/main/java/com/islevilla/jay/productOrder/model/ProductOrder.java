package com.islevilla.jay.productOrder.model;

import com.islevilla.jay.coupon.model.Coupon;
import com.islevilla.jay.productOrderDetail.model.ProductOrderDetail;
import com.islevilla.lai.members.model.Members;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_order")
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_order_id", updatable = false)
    private Integer productOrderId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Members member;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(name = "product_order_amount", nullable = false)
    private Integer productOrderAmount;

    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount;

    @Column(name = "product_paid_amount", nullable = false)
    private Integer productPaidAmount;

    @Column(name = "payment_method", nullable = false)
    private Byte paymentMethod;

    @Column(name = "shipping_method", nullable = false)
    private Byte shippingMethod;

    @Column(name = "contact_address", length = 200)
    @NotEmpty(message = "聯絡地址不能為空")
    private String contactAddress;

    @Column(name = "contact_name", length = 30)
    @NotEmpty(message = "聯絡人姓名不能為空")
    private String contactName;

    @Column(name = "contact_phone", length = 20)
    @NotEmpty(message = "聯絡人電話不能為空")
    private String contactPhone;

    @Column(name = "note", length = 50)
    private String note;

    @Column(name = "product_order_status", nullable = false)
    private Byte orderStatus;

    @Column(name = "product_order_time", nullable = false)
    private LocalDateTime orderTime;

    @OneToMany(mappedBy = "productOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOrderDetail> orderDetails = new ArrayList<>();

    // 無參數建構子（JPA 要求）
    public ProductOrder() {
    }

    // Getter & Setter
    public Integer getProductOrderId() {
        return productOrderId;
    }

    public void setProductOrderId(Integer productOrderId) {
        this.productOrderId = productOrderId;
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

    public Integer getProductOrderAmount() {
        return productOrderAmount;
    }

    public void setProductOrderAmount(Integer productOrderAmount) {
        this.productOrderAmount = productOrderAmount;
    }

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Integer discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Integer getProductPaidAmount() {
        return productPaidAmount;
    }

    public void setProductPaidAmount(Integer productPaidAmount) {
        this.productPaidAmount = productPaidAmount;
    }

    public Byte getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Byte paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Byte getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(Byte shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Byte getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Byte orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public List<ProductOrderDetail> getProductOrderDetails() {
        return orderDetails;
    }

    public void setProductOrderDetails(List<ProductOrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
