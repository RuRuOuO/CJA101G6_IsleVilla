package com.islevilla.product.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    @Autowired
    private ProductDAO productDAO;  // 注入 DAO 層

    public int addProduct(Product product) {
        return productDAO.insert(product);  // 調用 DAO 層的插入方法
    }
}
