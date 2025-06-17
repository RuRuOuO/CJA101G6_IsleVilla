package com.islevilla.yin.productphoto;

import com.islevilla.yin.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, Integer> {

    List<ProductPhoto> findByProductId(Integer productId);
}
