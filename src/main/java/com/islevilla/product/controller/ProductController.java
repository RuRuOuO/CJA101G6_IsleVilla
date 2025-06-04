package com.islevilla.product.controller;

import com.islevilla.product.model.Product;
import com.islevilla.product.model.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;  // 注入服務層

    @PostMapping
    public String addProduct(@RequestBody Product product) {
        int productId = productService.addProduct(product);  // 呼叫服務層的 addProduct 方法
        if (productId > 0) {
            return "Product added successfully with ID: " + productId;
        } else {
            return "Error occurred while adding product.";
        }
    }
}
