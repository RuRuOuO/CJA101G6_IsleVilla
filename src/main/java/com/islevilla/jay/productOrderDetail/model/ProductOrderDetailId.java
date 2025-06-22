package com.islevilla.jay.productOrderDetail.model;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProductOrderDetailId implements Serializable {

    @Column(name = "product_order_id")
    private Integer productOrderId;

    @Column(name = "product_id")
    private Integer productId;

    public ProductOrderDetailId() {
    }

    public ProductOrderDetailId(Integer productOrderId, Integer productId) {
        this.productOrderId = productOrderId;
        this.productId = productId;
    }

    // Getters / Setters

    public Integer getProductOrderId() {
        return productOrderId;
    }

    public void setProductOrderId(Integer productOrderId) {
        this.productOrderId = productOrderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductOrderDetailId)) return false;
        ProductOrderDetailId that = (ProductOrderDetailId) o;
        return Objects.equals(productOrderId, that.productOrderId) &&
                Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productOrderId, productId);
    }
}

