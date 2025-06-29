package com.islevilla.yin.productcategory.controller;

import com.islevilla.yin.productcategory.model.ProductCategory;
import com.islevilla.yin.productcategory.model.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/backend/product/category")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    // 渲染產品分類列表
    @GetMapping("/list")
    public String listProductCategory(Model model) {
        List<ProductCategory> categories = productCategoryService.getAllProductCategory();
        model.addAttribute("categories", categories);
        return "back-end/product/listProductCategory";
    }

    // 取得產品分類資料 API
    @GetMapping("/get/{categoryId}")
    @ResponseBody
    public ProductCategory getProductCategory(@PathVariable Integer categoryId) {
        return productCategoryService.getProductCategoryById(categoryId);
    }
    
    // 新增產品分類 API
    @PostMapping("/add")
    @ResponseBody
    public String addProductCategory(@RequestParam String productCategoryName) {
        try {
            ProductCategory category = new ProductCategory();
            category.setProductCategoryName(productCategoryName);
            productCategoryService.addProductCategory(category);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
    
    // 更新產品分類資料 API
    @PostMapping("/update")
    @ResponseBody
    public String updateProductCategory(@RequestParam Integer productCategoryId,
                                      @RequestParam String productCategoryName) {
        try {
            ProductCategory category = productCategoryService.getProductCategoryById(productCategoryId);
            if (category == null) {
                return "產品分類不存在";
            }
            category.setProductCategoryName(productCategoryName);
            productCategoryService.updateProductCategory(category);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // 刪除產品分類 API
    @PostMapping("/delete")
    @ResponseBody
    public String deleteProductCategory(@RequestParam Integer productCategoryId) {
        try {
            productCategoryService.deleteProductCategory(productCategoryId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
} 