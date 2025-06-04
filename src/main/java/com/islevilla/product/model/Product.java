package com.islevilla.product.model;


import jakarta.persistence.*;

public class Product {
    @Entity
    @Table(name = "product")
    public class Product {
        @Column(name = "product_id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Id
        private Integer productId;
        @Column(name = "product_category_id")
        private Integer productCategoryId;
        @Column(name = "product_name")
        private String productName;
        @Column(name = "product_price")
        private Integer productPrice;
        @Column(name = "product_description")
        private String productDescription;
        @Column(name = "product_quantity")
        private Integer productQuantity;
        @Column(name = "product_status")
        private Byte productStatus; // 0:下架, 1:上架


        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public Integer getProductCategoryId() {
            return productCategoryId;
        }

        public void setProductCategoryId(Integer productCategoryId) {
            this.productCategoryId = productCategoryId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public Integer getProductPrice() {
            return productPrice;
        }

        public void setProductPrice(Integer productPrice) {
            this.productPrice = productPrice;
        }

        public String getProductDescription() {
            return productDescription;
        }

        public void setProductDescription(String productDescription) {
            this.productDescription = productDescription;
        }

        public Integer getProductQuantity() {
            return productQuantity;
        }

        public void setProductQuantity(Integer productQuantity) {
            this.productQuantity = productQuantity;
        }

        public Byte getProductStatus() {
            return productStatus;
        }

        public void setProductStatus(Byte productStatus) {
            this.productStatus = productStatus;
        }
    }

}
