package com.islevilla.jay.productOrderDetail.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductOrderDetailRepository extends JpaRepository<ProductOrderDetail, ProductOrderDetailId> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM product_order_detail WHERE product_order_id = ?1 AND product_id = ?2", nativeQuery = true)
    void deleteByCompositeDetail(Integer productOrderId, Integer productId);

    @Query("SELECT od FROM ProductOrderDetail od WHERE od.productOrder.productOrderId = :productOrderId")
    List<ProductOrderDetail> findOrderDetailsByOrderId(@Param("productOrderId") Integer productOrderId);

    // 根據商品ID查詢訂單明細
    @Query("SELECT od FROM ProductOrderDetail od WHERE od.product.productId = :productId")
    List<ProductOrderDetail> findOrderDetailsByProductId(@Param("productId") Integer productId);

    // 根據訂單ID和商品ID查詢訂單明細
    @Query("SELECT od FROM ProductOrderDetail od WHERE od.productOrder.productOrderId = :productOrderId AND od.product.productId = :productId")
    ProductOrderDetail findOrderDetailByOrderIdAndProductId(
        @Param("productOrderId") Integer productOrderId,
        @Param("productId") Integer productId
    );

    // 根據訂單金額範圍查詢訂單明細
    @Query("SELECT od FROM ProductOrderDetail od WHERE od.productOrderPrice BETWEEN :minPrice AND :maxPrice")
    List<ProductOrderDetail> findOrderDetailsByPriceRange(
        @Param("minPrice") Integer minPrice,
        @Param("maxPrice") Integer maxPrice
    );

    // 根據訂購數量範圍查詢訂單明細
    @Query("SELECT od FROM ProductOrderDetail od WHERE od.productOrderQuantity BETWEEN :minQuantity AND :maxQuantity")
    List<ProductOrderDetail> findOrderDetailsByQuantityRange(
        @Param("minQuantity") Integer minQuantity,
        @Param("maxQuantity") Integer maxQuantity
    );

    // 根據商品名稱查詢訂單明細
    @Query("SELECT od FROM ProductOrderDetail od WHERE od.product.productName LIKE %:productName%")
    List<ProductOrderDetail> findOrderDetailsByProductName(@Param("productName") String productName);
} 