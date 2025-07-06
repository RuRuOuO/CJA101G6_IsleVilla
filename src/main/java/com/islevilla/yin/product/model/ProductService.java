package com.islevilla.yin.product.model;

import com.islevilla.yin.productphoto.ProductPhoto;
import com.islevilla.yin.productphoto.ProductPhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductPhotoRepository productPhotoRepository;

    public void addProduct(Product product) {
        productRepository.save(product);
    }
    public void updateProduct(Product product) {
        productRepository.save(product);
    }
    public void deleteProduct(Integer productId) {
        if(productRepository.existsById(productId)){
            productRepository.deleteById(productId);
        }
    }
    public Product getProductById(Integer productId) {
        return productRepository.findById(productId)
                .orElse(null);
    }
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    public List<Product> getProductByProductCategoryId(Integer productCategoryId) {
        return productRepository.findByProductCategoryProductCategoryId(productCategoryId);
    }
    public List<Product> getProductByStatus(Byte status) {
        return productRepository.findByProductStatus(status);
    }
    public List<Product> getProductByCategoryIdAndStatus(Integer categoryId, Byte status) {
        return productRepository.findByProductCategoryProductCategoryIdAndProductStatus(categoryId, status);
    }
    /**
     * 以悲觀鎖查詢商品，確保同時只有一個交易能修改庫存。
     * 用於下單流程防止超賣。
     */
    public Product getProductByIdForUpdate(Integer productId) {
        return productRepository.findByIdForUpdate(productId);
    }
}
