package com.islevilla.yin.productphoto;

import lombok.Data;

@Data
public class ProductWithImageDTO {
    private Integer productId;
    private String productName;
    private String productDescription;
    private Integer productPrice;
    private String productImageUrl; // 圖片 URL 或 Base64 字串

    // Constructor
    public ProductWithImageDTO(Integer productId, String productName, String productDescription, Integer productPrice, String productImageUrl) {
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.productImageUrl = productImageUrl;
    }


}

