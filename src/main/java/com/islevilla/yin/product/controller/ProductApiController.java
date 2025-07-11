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
import com.fasterxml.jackson.databind.ObjectMapper;

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
            Integer productId = product.getProductId();
            if (productPhotos != null) {
                for (int i = 0; i < productPhotos.size(); i++) {
                    MultipartFile file = productPhotos.get(i);
                    System.out.println("[DEBUG] productId: " + productId);
                    System.out.println("[DEBUG] file name: " + file.getOriginalFilename());
                    System.out.println("[DEBUG] file size: " + file.getSize());
                    System.out.println("[DEBUG] file isEmpty: " + file.isEmpty());
                    byte[] bytes = file.getBytes();
                    System.out.println("[DEBUG] file bytes length: " + (bytes != null ? bytes.length : -1));
                    if (!file.isEmpty()) {
                        ProductPhoto photo = new ProductPhoto();
                        photo.setProductId(productId);
                        photo.setProductImage(bytes);
                        photo.setDisplayOrder(i);
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
    public ResponseEntity<String> updateProduct(
            @ModelAttribute Product product,
            @RequestParam(value = "photoOrder", required = false) String photoOrderJson,
            @RequestParam(value = "deletedPhotoIds", required = false) String deletedPhotoIdsJson,
            @RequestParam(value = "newPhotos", required = false) List<MultipartFile> newPhotos) {
        try {
            productService.updateProduct(product);
            ObjectMapper mapper = new ObjectMapper();
            // 1. 刪除舊圖
            if (deletedPhotoIdsJson != null && !deletedPhotoIdsJson.isEmpty()) {
                List<Integer> deletedIds = mapper.readValue(deletedPhotoIdsJson, mapper.getTypeFactory().constructCollectionType(List.class, Integer.class));
                for (Integer id : deletedIds) {
                    productPhotoService.deleteById(id);
                }
            }
            // 2. 依照 photoOrder 混合排序處理
            if (photoOrderJson != null && !photoOrderJson.isEmpty()) {
                List<?> orderList = mapper.readValue(photoOrderJson, List.class);
                int newPhotoIdx = 0;
                for (int i = 0; i < orderList.size(); i++) {
                    Object obj = orderList.get(i);
                    if (obj instanceof java.util.Map) {
                        java.util.Map map = (java.util.Map) obj;
                        Boolean isNew = (Boolean) map.get("isNew");
                        if (isNew != null && isNew) {
                            // 新圖
                            if (newPhotos != null && newPhotoIdx < newPhotos.size()) {
                                MultipartFile file = newPhotos.get(newPhotoIdx++);
                                if (!file.isEmpty()) {
                                    ProductPhoto photo = new ProductPhoto();
                                    photo.setProductId(product.getProductId());
                                    photo.setProductImage(file.getBytes());
                                    photo.setDisplayOrder(i);
                                    productPhotoService.save(photo);
                                }
                            }
                        } else {
                            // 舊圖
                            Integer id = (Integer) map.get("id");
                            if (id != null) {
                                ProductPhoto photo = productPhotoService.getPhotoById(id);
                                if (photo != null) {
                                    photo.setDisplayOrder(i);
                                    productPhotoService.save(photo);
                                }
                            }
                        }
                    }
                }
            }
            return ResponseEntity.ok("修改成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("修改失敗: " + e.getMessage());
        }
    }

}
