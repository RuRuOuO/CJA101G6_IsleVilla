package com.islevilla.yin.product.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByProductCategoryProductCategoryId(Integer productCategoryId);

    List<Product> findByProductStatus(Byte status);
    
    List<Product> findByProductCategoryProductCategoryIdAndProductStatus(Integer productCategoryId, Byte status);

    Page<Product> findByProductStatus(Byte status, Pageable pageable);

    Page<Product> findByProductCategoryProductCategoryIdAndProductStatus(Integer productCategoryId, Byte status, Pageable pageable);

    Page<Product> findByProductCategoryProductCategoryId(Integer productCategoryId, Pageable pageable);

    /**
     * 以悲觀鎖(PESSIMISTIC_WRITE)查詢商品，防止高併發下超賣。
     * 此方法會鎖定資料庫該筆商品記錄，直到交易結束，確保同一時間只有一個交易能修改庫存。
     * 用於下單時即時檢查與扣減庫存，避免庫存負數。
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.productId = :productId")
    Product findByIdForUpdate(@Param("productId") Integer productId);

    Page<Product> findByProductStatusAndProductQuantityGreaterThan(Byte status, int quantity, Pageable pageable);

    Page<Product> findByProductStatusAndProductQuantityGreaterThanAndProductNameContaining(Byte status, int quantity, String keyword, Pageable pageable);

    Page<Product> findByProductCategoryProductCategoryIdAndProductStatusAndProductQuantityGreaterThanAndProductNameContaining(Integer categoryId, Byte status, int quantity, String keyword, Pageable pageable);

    Page<Product> findByProductCategoryProductCategoryIdAndProductStatusAndProductQuantityGreaterThan(Integer categoryId, Byte status, int quantity, Pageable pageable);

    List<Product> findByProductCategoryProductCategoryIdAndProductStatusAndProductNameContaining(Integer categoryId, Byte status, String keyword);
    List<Product> findByProductCategoryProductCategoryIdAndProductNameContaining(Integer categoryId, String keyword);
    List<Product> findByProductStatusAndProductNameContaining(Byte status, String keyword);
    List<Product> findByProductNameContaining(String keyword);

}
