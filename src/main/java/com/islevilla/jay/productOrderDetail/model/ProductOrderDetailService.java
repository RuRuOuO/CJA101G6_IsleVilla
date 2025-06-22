package com.islevilla.jay.productOrderDetail.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("productOrderDetailService")
public class ProductOrderDetailService {

    @Autowired
    ProductOrderDetailRepository repository;

    public ProductOrderDetail addOrderDetail(ProductOrderDetail orderDetail) {
        return repository.save(orderDetail);
    }

    @Transactional
    public void updateOrderDetail(ProductOrderDetail orderDetail) {
        ProductOrderDetailId id = orderDetail.getId();
        ProductOrderDetail existingOrderDetail = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到訂單明細"));

        // 更新訂單明細資訊
        existingOrderDetail.setProductOrderPrice(orderDetail.getProductOrderPrice());
        existingOrderDetail.setProductOrderQuantity(orderDetail.getProductOrderQuantity());
        repository.save(existingOrderDetail);
    }

    public ProductOrderDetail getOneOrderDetail(ProductOrderDetailId id) {
        Optional<ProductOrderDetail> optional = repository.findById(id);
        return optional.orElse(null);
    }

    @Transactional
    public void deleteOrderDetail(ProductOrderDetailId id) {
        try {
            if (repository.existsById(id)) {
                repository.deleteByCompositeDetail(
                    id.getProductOrderId(),
                    id.getProductId()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ProductOrderDetail> getAll() {
        return repository.findAll();
    }

    public List<ProductOrderDetail> findByProductOrderId(Integer productOrderId) {
        return repository.findOrderDetailsByOrderId(productOrderId);
    }

    // 根據商品名稱查詢訂單明細
    public List<ProductOrderDetail> findByProductName(String productName) {
        return repository.findOrderDetailsByProductName(productName);
    }

    // 根據商品ID查詢訂單明細
    public List<ProductOrderDetail> findByProductId(Integer productId) {
        return repository.findOrderDetailsByProductId(productId);
    }

    // 根據訂單ID和商品ID查詢訂單明細
    public ProductOrderDetail findByOrderIdAndProductId(Integer productOrderId, Integer productId) {
        return repository.findOrderDetailByOrderIdAndProductId(productOrderId, productId);
    }

    // 根據價格範圍查詢訂單明細
    public List<ProductOrderDetail> findByPriceRange(Integer minPrice, Integer maxPrice) {
        return repository.findOrderDetailsByPriceRange(minPrice, maxPrice);
    }

    // 根據數量範圍查詢訂單明細
    public List<ProductOrderDetail> findByQuantityRange(Integer minQuantity, Integer maxQuantity) {
        return repository.findOrderDetailsByQuantityRange(minQuantity, maxQuantity);
    }
} 