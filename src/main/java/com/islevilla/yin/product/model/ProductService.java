package com.islevilla.yin.product.model;

import com.islevilla.yin.productphoto.ProductPhoto;
import com.islevilla.yin.productphoto.ProductPhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Product> getProductByStatus(Byte status, Pageable pageable) {
        return productRepository.findByProductStatus(status, pageable);
    }
    public Page<Product> getProductByCategoryIdAndStatus(Integer categoryId, Byte status, Pageable pageable) {
        return productRepository.findByProductCategoryProductCategoryIdAndProductStatus(categoryId, status, pageable);
    }
    public Page<Product> getProductByProductCategoryId(Integer productCategoryId, Pageable pageable) {
        return productRepository.findByProductCategoryProductCategoryId(productCategoryId, pageable);
    }
  
    /**
     * 以悲觀鎖查詢商品，確保同時只有一個交易能修改庫存。
     * 用於下單流程防止超賣。
     */
    public Product getProductByIdForUpdate(Integer productId) {
        return productRepository.findByIdForUpdate(productId);
    }

    public Page<Product> getProductByStatusAndStock(Byte status, Pageable pageable) {
        return productRepository.findByProductStatusAndProductQuantityGreaterThan(status, 0, pageable);
    }

    public Page<Product> findByStatusAndNameContaining(Byte status, String keyword, Pageable pageable) {
        return productRepository.findByProductStatusAndProductQuantityGreaterThanAndProductNameContaining(status, 0, keyword, pageable);
    }
    public Page<Product> findByCategoryAndStatusAndNameContaining(Integer categoryId, Byte status, String keyword, Pageable pageable) {
        return productRepository.findByProductCategoryProductCategoryIdAndProductStatusAndProductQuantityGreaterThanAndProductNameContaining(categoryId, status, 0, keyword, pageable);
    }
    public Page<Product> getProductByCategoryIdAndStatusAndStock(Integer categoryId, Byte status, Pageable pageable) {
        return productRepository.findByProductCategoryProductCategoryIdAndProductStatusAndProductQuantityGreaterThan(categoryId, status, 0, pageable);
    }

    // 價格範圍搜尋方法
    public Page<Product> findByStatusAndPriceBetween(Byte status, Integer minPrice, Integer maxPrice, Pageable pageable) {
        return productRepository.findByProductStatusAndProductQuantityGreaterThanAndProductPriceBetween(status, 0, minPrice, maxPrice, pageable);
    }
    
    public Page<Product> findByStatusAndNameContainingAndPriceBetween(Byte status, String keyword, Integer minPrice, Integer maxPrice, Pageable pageable) {
        return productRepository.findByProductStatusAndProductQuantityGreaterThanAndProductNameContainingAndProductPriceBetween(status, 0, keyword, minPrice, maxPrice, pageable);
    }
    
    public Page<Product> findByCategoryAndStatusAndPriceBetween(Integer categoryId, Byte status, Integer minPrice, Integer maxPrice, Pageable pageable) {
        return productRepository.findByProductCategoryProductCategoryIdAndProductStatusAndProductQuantityGreaterThanAndProductPriceBetween(categoryId, status, 0, minPrice, maxPrice, pageable);
    }
    
    public Page<Product> findByCategoryAndStatusAndNameContainingAndPriceBetween(Integer categoryId, Byte status, String keyword, Integer minPrice, Integer maxPrice, Pageable pageable) {
        return productRepository.findByProductCategoryProductCategoryIdAndProductStatusAndProductQuantityGreaterThanAndProductNameContainingAndProductPriceBetween(categoryId, status, 0, keyword, minPrice, maxPrice, pageable);
    }

    // 單邊價格範圍搜尋方法
    public Page<Product> findByStatusAndPriceGreaterThanEqual(Byte status, Integer minPrice, Pageable pageable) {
        return productRepository.findByProductStatusAndProductQuantityGreaterThanAndProductPriceGreaterThanEqual(status, 0, minPrice, pageable);
    }
    
    public Page<Product> findByStatusAndPriceLessThanEqual(Byte status, Integer maxPrice, Pageable pageable) {
        return productRepository.findByProductStatusAndProductQuantityGreaterThanAndProductPriceLessThanEqual(status, 0, maxPrice, pageable);
    }
    
    public Page<Product> findByStatusAndNameContainingAndPriceGreaterThanEqual(Byte status, String keyword, Integer minPrice, Pageable pageable) {
        return productRepository.findByProductStatusAndProductQuantityGreaterThanAndProductNameContainingAndProductPriceGreaterThanEqual(status, 0, keyword, minPrice, pageable);
    }
    
    public Page<Product> findByStatusAndNameContainingAndPriceLessThanEqual(Byte status, String keyword, Integer maxPrice, Pageable pageable) {
        return productRepository.findByProductStatusAndProductQuantityGreaterThanAndProductNameContainingAndProductPriceLessThanEqual(status, 0, keyword, maxPrice, pageable);
    }
    
    public Page<Product> findByCategoryAndStatusAndPriceGreaterThanEqual(Integer categoryId, Byte status, Integer minPrice, Pageable pageable) {
        return productRepository.findByProductCategoryProductCategoryIdAndProductStatusAndProductQuantityGreaterThanAndProductPriceGreaterThanEqual(categoryId, status, 0, minPrice, pageable);
    }
    
    public Page<Product> findByCategoryAndStatusAndPriceLessThanEqual(Integer categoryId, Byte status, Integer maxPrice, Pageable pageable) {
        return productRepository.findByProductCategoryProductCategoryIdAndProductStatusAndProductQuantityGreaterThanAndProductPriceLessThanEqual(categoryId, status, 0, maxPrice, pageable);
    }
    
    public Page<Product> findByCategoryAndStatusAndNameContainingAndPriceGreaterThanEqual(Integer categoryId, Byte status, String keyword, Integer minPrice, Pageable pageable) {
        return productRepository.findByProductCategoryProductCategoryIdAndProductStatusAndProductQuantityGreaterThanAndProductNameContainingAndProductPriceGreaterThanEqual(categoryId, status, 0, keyword, minPrice, pageable);
    }
    
    public Page<Product> findByCategoryAndStatusAndNameContainingAndPriceLessThanEqual(Integer categoryId, Byte status, String keyword, Integer maxPrice, Pageable pageable) {
        return productRepository.findByProductCategoryProductCategoryIdAndProductStatusAndProductQuantityGreaterThanAndProductNameContainingAndProductPriceLessThanEqual(categoryId, status, 0, keyword, maxPrice, pageable);
    }

    public List<Product> searchProducts(Integer categoryId, Byte status, String keyword) {
        if (categoryId != null && status != null) {
            return productRepository.findByProductCategoryProductCategoryIdAndProductStatusAndProductNameContaining(categoryId, status, keyword);
        } else if (categoryId != null) {
            return productRepository.findByProductCategoryProductCategoryIdAndProductNameContaining(categoryId, keyword);
        } else if (status != null) {
            return productRepository.findByProductStatusAndProductNameContaining(status, keyword);
        } else {
            return productRepository.findByProductNameContaining(keyword);
        }
    }
}
