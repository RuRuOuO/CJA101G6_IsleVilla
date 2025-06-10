package com.islevilla.yin.product.model;


import com.islevilla.yin.productcategory.model.ProductCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


    @Entity
    @Table(name = "product")
    @Data
    public class Product {
        @Column(name = "product_id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Id
        private Integer productId;

        @ManyToOne
        @JoinColumn(name = "product_category_id", referencedColumnName = "product_category_id", nullable = false)
        private ProductCategory productCategory;

        @Column(name = "product_name")
        @NotEmpty(message = "請輸入產品名稱")
        private String productName;

        @NotNull(message = "請輸入價格")
        @Column(name = "product_price")
        private Integer productPrice;

        @Column(name = "product_description")
        private String productDescription;

        @NotNull(message = "請輸入庫存量")
        @Column(name = "product_quantity")
        private Integer productQuantity;

        @Column(name = "product_status")
        private Byte productStatus; // 0:下架, 1:上架
    }

