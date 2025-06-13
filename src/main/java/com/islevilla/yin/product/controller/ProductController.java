package com.islevilla.yin.product.controller;

import com.islevilla.yin.product.model.ProductService;
import com.islevilla.yin.productcategory.model.ProductCategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final ProductCategoryService productCategoryService;

    public ProductController(ProductService productService, ProductCategoryService productCategoryService) {
        this.productService = productService;
        this.productCategoryService = productCategoryService;
    }

    //商品首頁-產品和產品類別渲染
    @GetMapping
    public String homeProduct(Model model) {
        model.addAttribute("product", productService.getAllProducts());
        model.addAttribute("category", productCategoryService.getAllProductCategory());
        return "front-end/product/product_index";
    }
    //商品列表頁面-產品渲染
    @GetMapping("/list")
    public String listProduct(@RequestParam(value = "categoryId", required = false) Integer categoryId, Model model) {
        if (categoryId != null) {
            model.addAttribute("product", productService.getProductByProductCategoryId(categoryId));
            model.addAttribute("selectedCategory", productCategoryService.getProductCategoryById(categoryId));
        } else {
            model.addAttribute("product", productService.getAllProducts());
            model.addAttribute("selectedCategory", "全部商品");
        }
        model.addAttribute("category", productCategoryService.getAllProductCategory());
        return "front-end/product/product_list";
    }


}
