package com.islevilla.jay.productOrder.model;

import com.islevilla.jay.hibernate.util.compositeQuery.HibernateUtil_CompositeQuery_ProductOrder;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service("productOrderService")
public class ProductOrderService {

    @Autowired
    private ProductOrderRepository repository;

    @Autowired
    private SessionFactory sessionFactory;

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
}

