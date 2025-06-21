package com.islevilla.jay.productOrderDetail.model;

import com.islevilla.jay.productOrder.model.ProductOrder;
import com.islevilla.yin.product.model.Product;
import jakarta.persistence.*;

@Entity
@Table(name = "product_order_detail")
public class ProductOrderDetail {

    @EmbeddedId
    private ProductOrderDetailId id; //代表複合主鍵

    @Column(name = "product_order_price", nullable = false)
    private Integer productOrderPrice;

    @Column(name = "product_order_quantity", nullable = false)
    private Integer productOrderQuantity;

    @ManyToOne
    @JoinColumn(name = "product_name", referencedColumnName = "product_name", nullable = false)
    private Product productName;

    // 訂單主表關聯
    @ManyToOne
    @MapsId("productOrderId") // 對應複合主鍵中的欄位名稱
    @JoinColumn(name = "product_order_id", nullable = false)
    private ProductOrder productOrder;

    // 商品關聯
    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public ProductOrderDetail() {
    }

    // Getter / Setter

    public ProductOrderDetailId getId() {
        return id;
    }

    public void setId(ProductOrderDetailId id) {
        this.id = id;
    }

    public Integer getProductOrderPrice() {
        return productOrderPrice;
    }

    public void setProductOrderPrice(Integer productOrderPrice) {
        this.productOrderPrice = productOrderPrice;
    }

    public Integer getProductOrderQuantity() {
        return productOrderQuantity;
    }

    public void setProductOrderQuantity(Integer productOrderQuantity) {
        this.productOrderQuantity = productOrderQuantity;
    }

    public Product getProductName() {
        return productName;
    }

    public void setProductName(Product productName) {
        this.productName = productName;
    }

    public ProductOrder getProductOrder() {
        return productOrder;
    }

    public void setProductOrder(ProductOrder productOrder) {
        this.productOrder = productOrder;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
