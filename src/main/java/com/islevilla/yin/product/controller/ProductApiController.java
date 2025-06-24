package com.islevilla.yin.product.controller;

import com.islevilla.yin.product.model.Product;
import com.islevilla.yin.product.model.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

//資料傳輸API
@RestController
@RequestMapping("/backend/product")
public class ProductApiController {
    @Autowired
    private ProductService productService;

    // 送出新增產品
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('product')")
    public ResponseEntity<String> addProduct(@RequestBody Product product) {
        try {
        productService.addProduct(product);
            return ResponseEntity.ok("新增成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("新增失敗: " + e.getMessage());
        }
    }
    // 送出修改商品
    @PostMapping("/edit")
    @PreAuthorize("hasAuthority('product')")
    public ResponseEntity<String> updateProduct(@Valid @RequestBody Product product) {
        try {
        productService.updateProduct(product);
            return ResponseEntity.ok("修改成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("修改失敗: " + e.getMessage());
        }
    }

}
