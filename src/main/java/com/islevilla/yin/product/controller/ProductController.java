package com.islevilla.yin.product.controller;

import com.islevilla.yin.product.model.Product;
import com.islevilla.yin.product.model.ProductService;
import com.islevilla.yin.productcategory.model.ProductCategoryService;
import com.islevilla.yin.productphoto.ProductPhoto;
import com.islevilla.yin.productphoto.ProductPhotoService;
import com.islevilla.yin.productphoto.ProductWithImageDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final ProductCategoryService productCategoryService;
    private final ProductPhotoService productPhotoService;


    public ProductController(ProductService productService, ProductCategoryService productCategoryService, ProductPhotoService productPhotoService) {
        this.productService = productService;
        this.productCategoryService = productCategoryService;
        this.productPhotoService = productPhotoService;
    }

    //商品首頁-產品和產品類別渲染
    @GetMapping
    public String homeProduct(Model model) {
        // 獲取所有產品的資料
        List<Product> products = productService.getAllProducts();
        // 創建一個用來封裝產品資料和圖片 URL 的 DTO 列表
        List<ProductWithImageDTO> productWithImageDTOs = new ArrayList<>();
        // 查詢每個產品的第一張圖片並設置給產品資料
        for (Product product : products) {
            // 根據 productId 查詢該產品的第一張圖片
            ProductPhoto firstProductPhoto = productPhotoService.getFirstProductPhotoByProductId(product.getProductId());
            String productImageUrl = "https://dummyimage.com/300x200/";  // 預設圖片 URL
            if (firstProductPhoto != null) {
                // 將圖片轉換為 Base64 或圖片 URL
                productImageUrl = convertImageToBase64(firstProductPhoto.getProductImage());
            }
            // 封裝為 DTO
            ProductWithImageDTO productWithImageDTO = new ProductWithImageDTO(
                    product.getProductId(),
                    product.getProductName(),
                    product.getProductDescription(),
                    product.getProductPrice(),
                    productImageUrl
            );
            // 加入到 DTO 列表中
            productWithImageDTOs.add(productWithImageDTO);
        }

        // 將產品和產品類別資料添加到模型中
        model.addAttribute("product", productWithImageDTOs);
        model.addAttribute("category", productCategoryService.getAllProductCategory());
        return "front-end/product/product_index";
    }


    private String convertImageToBase64(byte[] imageBytes) {
        if (imageBytes == null) {
            // 如果圖片是 null，返回預設圖片的 URL
            return "https://dummyimage.com/300x200/";
        }
        // 如果圖片不為 null，將圖片轉換為 Base64 字串
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }

//    //商品列表頁面-產品渲染
//    @GetMapping("/list")
//    public String listProduct(@RequestParam(value = "categoryId", required = false) Integer categoryId, Model model) {
//        if (categoryId != null) {
//            model.addAttribute("product", productService.getProductByProductCategoryId(categoryId));
//            model.addAttribute("selectedCategory", productCategoryService.getProductCategoryById(categoryId));
//        } else {
//            model.addAttribute("product", productService.getAllProducts());
//            model.addAttribute("selectedCategory", "全部商品");
//        }
//        model.addAttribute("category", productCategoryService.getAllProductCategory());
//        return "front-end/product/product_list";
//    }
@GetMapping("/new")
public String newProduct(@RequestParam(required = false) Integer productId, Model model) {
    Product product = (productId != null) ? productService.getProductById(productId) : new Product();
    model.addAttribute("product", product);
    model.addAttribute("category", productCategoryService.getAllProductCategory());
    return "back-end/product/product_new"; // 這裡就是你的表單頁面路徑
}




}
