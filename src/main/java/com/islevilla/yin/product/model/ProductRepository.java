package com.islevilla.yin.product.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByProductCategoryProductCategoryId(Integer productCategoryId);

    List<Product> findByProductStatus(Byte status);
    
    List<Product> findByProductCategoryProductCategoryIdAndProductStatus(Integer productCategoryId, Byte status);
}
