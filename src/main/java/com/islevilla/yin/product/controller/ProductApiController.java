package com.islevilla.yin.product.controller;

import com.islevilla.yin.product.model.Product;
import com.islevilla.yin.product.model.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

//資料傳輸API
@RestController
@RequestMapping("/product")
public class ProductApiController {
    @Autowired
    private ProductService productService;

    // 送出新增產品
    @PostMapping("/add")
    public void addProduct(@RequestBody Product product) {
        productService.addProduct(product);
    }
    // 送出修改商品
    @PostMapping("/edit")
    public void updateProduct(@Valid @RequestBody Product product) {
        productService.updateProduct(product);
    }

}
