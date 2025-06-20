package com.islevilla.jay.productOrder.model;

import com.islevilla.jay.hibernate.util.compositeQuery.HibernateUtil_CompositeQuery_ProductOrder;
import com.islevilla.jay.productOrderDetail.model.ProductOrderDetail;
import com.islevilla.jay.productOrderDetail.model.ProductOrderDetailRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service("productOrderService")
public class ProductOrderService {

    @Autowired
    private ProductOrderRepository repository;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ProductOrderDetailRepository productOrderDetailRepository;

    public void addProductOrder(ProductOrder productOrder) {
        repository.save(productOrder);
    }

    public void updateProductOrder(ProductOrder productOrder) {
        repository.save(productOrder);
    }

    public void deleteProductOrder(Integer productOrderId) {
        if (repository.existsById(productOrderId))
            repository.deleteById(productOrderId);
    }

    public ProductOrder getOneProductOrder(Integer productOrderId) {
        Optional<ProductOrder> optional = repository.findById(productOrderId);
        return optional.orElse(null);  // 如果值存在就回傳其值，否則回傳null
    }

    public List<ProductOrder> getAll() {
        return repository.findAll();
    }

    public List<ProductOrder> getAll(Map<String, String[]> map) {
        return HibernateUtil_CompositeQuery_ProductOrder.getAllC(map, sessionFactory.openSession());
    }

    // 新增訂單明細
    @Transactional
    public void addOrderDetail(ProductOrder order, ProductOrderDetail detail) {
        if (order == null || detail == null) {
            throw new IllegalArgumentException("訂單和訂單明細不能為空");
        }
        
        // 設置關聯關係
        detail.setProductOrder(order);
        order.getProductOrderDetails().add(detail);
        
        // 保存訂單明細
        productOrderDetailRepository.save(detail);
    }

    // 移除訂單明細
    @Transactional
    public void removeOrderDetail(ProductOrder order, ProductOrderDetail detail) {
        if (order == null || detail == null) {
            throw new IllegalArgumentException("訂單和訂單明細不能為空");
        }
        
        // 移除關聯關係
        order.getProductOrderDetails().remove(detail);
        detail.setProductOrder(null);
        
        // 刪除訂單明細
        productOrderDetailRepository.delete(detail);
    }

    public List<ProductOrder> getMemAllOrder(Integer memberId) {
        return repository.findByMember_MemberId(memberId);
    }
}

