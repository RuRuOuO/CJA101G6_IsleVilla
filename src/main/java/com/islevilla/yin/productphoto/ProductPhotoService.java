package com.islevilla.yin.productphoto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductPhotoService {
    @Autowired
    private  ProductPhotoRepository productPhotoRepository;

    public List<ProductPhoto> getProductPhotosByProductId(Integer productId) {
        // 根據 productId 查詢所有圖片
        return productPhotoRepository.findByProductId(productId);
    }

    public ProductPhoto getFirstProductPhotoByProductId(Integer productId) {
        // 查詢產品的所有圖片，並取第一張
        List<ProductPhoto> productPhotos = productPhotoRepository.findByProductId(productId);
        if (productPhotos != null && !productPhotos.isEmpty()) {
            return productPhotos.get(0); // 返回第一張圖片
        }
        return null;  // 如果沒有圖片，返回 null
    }
}

