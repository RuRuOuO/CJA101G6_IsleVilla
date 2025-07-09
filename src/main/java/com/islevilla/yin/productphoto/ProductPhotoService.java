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
        return productPhotoRepository.findByProductIdOrderByDisplayOrderAsc(productId);
    }

    public ProductPhoto getFirstProductPhotoByProductId(Integer productId) {
        // 查詢產品的所有圖片，並取第一張
        List<ProductPhoto> productPhotos = productPhotoRepository.findByProductIdOrderByDisplayOrderAsc(productId);
        if (productPhotos != null && !productPhotos.isEmpty()) {
            return productPhotos.get(0); // 返回第一張圖片
        }
        return null;  // 如果沒有圖片，返回 null
    }

    public ProductPhoto getMainProductPhotoByProductId(Integer productId) {
        // 直接回傳 displayOrder 最小的那一張
        List<ProductPhoto> productPhotos = productPhotoRepository.findByProductIdOrderByDisplayOrderAsc(productId);
        if (productPhotos != null && !productPhotos.isEmpty()) {
            return productPhotos.get(0);
        }
        return null;
    }

    public void save(ProductPhoto productPhoto) {
        productPhotoRepository.save(productPhoto);
    }

    public ProductPhoto getPhotoById(Integer productPhotoId) {
        return productPhotoRepository.findById(productPhotoId).orElse(null);
    }

    public void deleteById(Integer productPhotoId) {
        productPhotoRepository.deleteById(productPhotoId);
    }
}

