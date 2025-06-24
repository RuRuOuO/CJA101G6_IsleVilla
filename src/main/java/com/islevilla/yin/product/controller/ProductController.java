package com.islevilla.yin.product.controller;
import com.islevilla.yin.product.model.Product;
import com.islevilla.yin.product.model.ProductService;
import com.islevilla.yin.productcategory.model.ProductCategoryService;
import com.islevilla.yin.productphoto.ProductPhoto;
import com.islevilla.yin.productphoto.ProductPhotoService;
import com.islevilla.yin.productphoto.ProductWithImageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

//頁面渲染
@Controller
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ProductPhotoService productPhotoService;

    //前台-商品首頁
    @GetMapping("/product")
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
        return "front-end/product/homeProduct";
    }
    private String convertImageToBase64(byte[] imageBytes) {
        if (imageBytes == null) {
            // 如果圖片是 null，返回預設圖片的 URL
            return "https://dummyimage.com/300x200/";
        }
        // 如果圖片不為 null，將圖片轉換為 Base64 字串
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }

    //前台-商品分頁
    @GetMapping("/product/list")
    public String homeProduct(@RequestParam(value = "productCategoryId", required = false) Integer productCategoryId, Model model) {
        List<Product> products;
        if (productCategoryId != null) {
            // 根據商品類別過濾商品
            products = productService.getProductByProductCategoryId(productCategoryId);
        } else {
            // 沒有選擇類別時顯示所有商品
            products = productService.getAllProducts();
        }

        List<ProductWithImageDTO> productWithImageDTOs = new ArrayList<>();
        for (Product product : products) {
            ProductPhoto firstProductPhoto = productPhotoService.getFirstProductPhotoByProductId(product.getProductId());
            String productImageUrl = "https://dummyimage.com/300x200/";  // 預設圖片 URL
            if (firstProductPhoto != null) {
                productImageUrl = convertImageToBase64(firstProductPhoto.getProductImage());
            }
            ProductWithImageDTO productWithImageDTO = new ProductWithImageDTO(
                    product.getProductId(),
                    product.getProductName(),
                    product.getProductDescription(),
                    product.getProductPrice(),
                    productImageUrl
            );
            productWithImageDTOs.add(productWithImageDTO);
        }

        model.addAttribute("product", productWithImageDTOs);
        model.addAttribute("category", productCategoryService.getAllProductCategory());
        model.addAttribute("selectedCategoryId", productCategoryId); // 新增這行

        return "front-end/product/listProduct";
    }

    // 取得商品第一張圖片（保留這個方法，刪除用 productPhotoId 查單一圖的方法）
    @GetMapping("/backend/product/photo/{productId}")
    @ResponseBody
    public ResponseEntity<byte[]> getProductPhoto(@PathVariable Integer productId) {
        ProductPhoto photo = productPhotoService.getFirstProductPhotoByProductId(productId);
        if (photo != null && photo.getProductImage() != null) {
            byte[] img = photo.getProductImage();
            String contentType = "image/jpeg";
            if (img.length > 4 && img[0] == (byte)0x89 && img[1] == 0x50 && img[2] == 0x4E && img[3] == 0x47) {
                contentType = "image/png";
            }
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(img);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //後台-商品列表
    @GetMapping("/backend/product/list")
    @PreAuthorize("hasAuthority('product')")
    public String listProduct(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                             @RequestParam(value = "status", required = false) Byte status, 
                             Model model) {
        List<Product> productList;
        
        if (categoryId != null && status != null) {
            // 根據類別和狀態過濾商品
            productList = productService.getProductByCategoryIdAndStatus(categoryId, status);
        } else if (categoryId != null) {
            // 根據類別過濾商品
            productList = productService.getProductByProductCategoryId(categoryId);
        } else if (status != null) {
            // 根據狀態過濾商品
            productList = productService.getProductByStatus(status);
        } else {
            // 沒有選擇篩選條件時顯示所有商品
            productList = productService.getAllProducts();
        }
        
        model.addAttribute("productList", productList); // 商品列表
        model.addAttribute("product", new Product());   // 空的 Product 給 modal 表單用
        model.addAttribute("category", productCategoryService.getAllProductCategory());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedStatus", status);
        return "back-end/product/listProduct";
    }

    //後台-編輯商品頁
    @GetMapping("/backend/product/edit/{productId}")
    @PreAuthorize("hasAuthority('product')")
    public String showEditProductPage(@PathVariable Integer productId, Model model) {
        Product product = productService.getProductById(productId);
        model.addAttribute("product", product);
        model.addAttribute("category", productCategoryService.getAllProductCategory());
        return "back-end/product/editProduct";
    }

    // 查詢商品所有圖片（依順序）
    @GetMapping("/backend/product/photos/{productId}")
    @ResponseBody
    public List<ProductPhoto> getProductPhotos(@PathVariable Integer productId) {
        return productPhotoService.getProductPhotosByProductId(productId);
    }

    // 儲存圖片新順序
    @PostMapping("/backend/product/photos/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderProductPhotos(@RequestBody List<Integer> photoIds) {
        for (int i = 0; i < photoIds.size(); i++) {
            ProductPhoto photo = productPhotoService.getPhotoById(photoIds.get(i));
            if (photo != null) {
                photo.setDisplayOrder(i);
                productPhotoService.save(photo);
            }
        }
        return ResponseEntity.ok().build();
    }

    // 根據圖片ID取得單一圖片（for 後台多圖預覽）
    @GetMapping("/backend/product/photo/id/{productPhotoId}")
    @ResponseBody
    public ResponseEntity<byte[]> getProductPhotoById(@PathVariable Integer productPhotoId) {
        ProductPhoto photo = productPhotoService.getPhotoById(productPhotoId);
        if (photo != null && photo.getProductImage() != null) {
            byte[] img = photo.getProductImage();
            String contentType = "image/jpeg";
            if (img.length > 4 && img[0] == (byte)0x89 && img[1] == 0x50 && img[2] == 0x4E && img[3] == 0x47) {
                contentType = "image/png";
            }
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(img);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
