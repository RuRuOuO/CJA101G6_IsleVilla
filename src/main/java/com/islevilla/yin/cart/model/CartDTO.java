package com.islevilla.yin.cart.model;

public class CartDTO {
    private Integer productId;
    private String productName;
    private int productPrice;
    private int quantity;
    private int subtotal;
    private String productImageUrl;
    private int productQuantity;

    public CartDTO(Integer productId, String productName, int productPrice, int quantity, int subtotal, String productImageUrl, int productQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.productImageUrl = productImageUrl;
        this.productQuantity = productQuantity;
    }

    // Getters and Setters
    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getSubtotal() {
        return subtotal;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public int getProductQuantity() {
        return productQuantity;
    }
}


