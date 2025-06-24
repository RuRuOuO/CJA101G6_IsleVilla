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
import org.springframework.web.multipart.MultipartFile;
import com.islevilla.yin.productphoto.ProductPhoto;
import com.islevilla.yin.productphoto.ProductPhotoService;
import java.util.List;

//資料傳輸API
@RestController
@RequestMapping("/backend/product")
public class ProductApiController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductPhotoService productPhotoService;

    // 送出新增產品
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('product')")
    public ResponseEntity<String> addProduct(@ModelAttribute Product product,
                                             @RequestParam(value = "productPhotos", required = false) List<MultipartFile> productPhotos) {
        try {
            productService.addProduct(product);
            if (productPhotos != null) {
                for (MultipartFile file : productPhotos) {
                    if (!file.isEmpty()) {
                        ProductPhoto photo = new ProductPhoto();
                        photo.setProductId(product.getProductId());
                        photo.setProductImage(file.getBytes());
                        productPhotoService.save(photo);
                    }
                }
            }
            return ResponseEntity.ok("新增成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("新增失敗: " + e.getMessage());
        }
    }
    // 送出修改商品
    @PostMapping("/edit")
    @PreAuthorize("hasAuthority('product')")
    public ResponseEntity<String> updateProduct(@ModelAttribute Product product,
                                                @RequestParam(value = "productPhotos", required = false) List<MultipartFile> productPhotos) {
        try {
            productService.updateProduct(product);
            if (productPhotos != null) {
                for (MultipartFile file : productPhotos) {
                    if (!file.isEmpty()) {
                        ProductPhoto photo = new ProductPhoto();
                        photo.setProductId(product.getProductId());
                        photo.setProductImage(file.getBytes());
                        productPhotoService.save(photo);
                    }
                }
            }
            return ResponseEntity.ok("修改成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("修改失敗: " + e.getMessage());
        }
    }

}
