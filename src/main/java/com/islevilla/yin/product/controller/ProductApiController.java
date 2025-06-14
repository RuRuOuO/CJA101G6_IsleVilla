package com.islevilla.yin.product.controller;

import com.islevilla.yin.product.model.Product;
import com.islevilla.yin.product.model.ProductService;
import com.islevilla.yin.productcategory.model.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductApiController {
    @Autowired
    private ProductService productService;


    // 新增產品
    @PostMapping("add")
    public void addProduct(@RequestBody Product product) {
        productService.addProduct(product);
    }
    // 修改商品
    @PostMapping("update")
    public void updateProduct(@RequestBody Product product) {
        productService.updateProduct(product);
    }
    // 刪除商品
    @PostMapping("delete")
    public void deleteProduct(@RequestParam Integer productId) {
        productService.deleteProduct(productId);
    }
}
