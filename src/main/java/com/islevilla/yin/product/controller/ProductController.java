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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
        // 只查詢上架商品
        List<Product> products = productService.getProductByStatus((byte)1);
        // 僅顯示庫存大於0的商品
        List<ProductWithImageDTO> productWithImageDTOs = new ArrayList<>();
        for (Product product : products) {
            if (product.getProductQuantity() == null || product.getProductQuantity() <= 0) continue;
            // 根據 productId 查詢該產品的第一張圖片
            ProductPhoto mainProductPhoto = productPhotoService.getMainProductPhotoByProductId(product.getProductId());
            String productImageUrl = "https://dummyimage.com/300x200/808080&text=No+Image";  // 預設圖片 URL
            if (mainProductPhoto != null) {
                // 將圖片轉換為 Base64 或圖片 URL
                productImageUrl = convertImageToBase64(mainProductPhoto.getProductImage());
            }
            // 封裝為 DTO
            ProductWithImageDTO productWithImageDTO = new ProductWithImageDTO(
                    product.getProductId(),
                    product.getProductName(),
                    product.getProductDescription(),
                    product.getProductPrice(),
                    productImageUrl,
                    product.getProductQuantity()
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
            return "https://dummyimage.com/300x200/808080&text=No+Image";
        }
        // 如果圖片不為 null，將圖片轉換為 Base64 字串
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }

    //前台-商品分頁
    @GetMapping("/product/list")
    public String homeProduct(
            @RequestParam(value = "productCategoryId", required = false) Integer productCategoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "minPrice", required = false) Integer minPrice,
            @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {
        int pageSize = 12;
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Product> productPage;
        
        // 根據搜尋條件查詢商品
        if (productCategoryId != null && keyword != null && !keyword.isEmpty() && minPrice != null && maxPrice != null) {
            productPage = productService.findByCategoryAndStatusAndNameContainingAndPriceBetween(productCategoryId, (byte)1, keyword, minPrice, maxPrice, pageable);
        } else if (productCategoryId != null && keyword != null && !keyword.isEmpty() && minPrice != null) {
            productPage = productService.findByCategoryAndStatusAndNameContainingAndPriceGreaterThanEqual(productCategoryId, (byte)1, keyword, minPrice, pageable);
        } else if (productCategoryId != null && keyword != null && !keyword.isEmpty() && maxPrice != null) {
            productPage = productService.findByCategoryAndStatusAndNameContainingAndPriceLessThanEqual(productCategoryId, (byte)1, keyword, maxPrice, pageable);
        } else if (productCategoryId != null && keyword != null && !keyword.isEmpty()) {
            productPage = productService.findByCategoryAndStatusAndNameContaining(productCategoryId, (byte)1, keyword, pageable);
        } else if (productCategoryId != null && minPrice != null && maxPrice != null) {
            productPage = productService.findByCategoryAndStatusAndPriceBetween(productCategoryId, (byte)1, minPrice, maxPrice, pageable);
        } else if (productCategoryId != null && minPrice != null) {
            productPage = productService.findByCategoryAndStatusAndPriceGreaterThanEqual(productCategoryId, (byte)1, minPrice, pageable);
        } else if (productCategoryId != null && maxPrice != null) {
            productPage = productService.findByCategoryAndStatusAndPriceLessThanEqual(productCategoryId, (byte)1, maxPrice, pageable);
        } else if (keyword != null && !keyword.isEmpty() && minPrice != null && maxPrice != null) {
            productPage = productService.findByStatusAndNameContainingAndPriceBetween((byte)1, keyword, minPrice, maxPrice, pageable);
        } else if (keyword != null && !keyword.isEmpty() && minPrice != null) {
            productPage = productService.findByStatusAndNameContainingAndPriceGreaterThanEqual((byte)1, keyword, minPrice, pageable);
        } else if (keyword != null && !keyword.isEmpty() && maxPrice != null) {
            productPage = productService.findByStatusAndNameContainingAndPriceLessThanEqual((byte)1, keyword, maxPrice, pageable);
        } else if (productCategoryId != null) {
            productPage = productService.getProductByCategoryIdAndStatusAndStock(productCategoryId, (byte)1, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            productPage = productService.findByStatusAndNameContaining((byte)1, keyword, pageable);
        } else if (minPrice != null && maxPrice != null) {
            productPage = productService.findByStatusAndPriceBetween((byte)1, minPrice, maxPrice, pageable);
        } else if (minPrice != null) {
            productPage = productService.findByStatusAndPriceGreaterThanEqual((byte)1, minPrice, pageable);
        } else if (maxPrice != null) {
            productPage = productService.findByStatusAndPriceLessThanEqual((byte)1, maxPrice, pageable);
        } else {
            productPage = productService.getProductByStatusAndStock((byte)1, pageable);
        }
        
        List<ProductWithImageDTO> productWithImageDTOs = new ArrayList<>();
        for (Product product : productPage.getContent()) {
            ProductPhoto mainProductPhoto = productPhotoService.getMainProductPhotoByProductId(product.getProductId());
            String productImageUrl = "https://dummyimage.com/300x200/808080&text=No+Image";
            if (mainProductPhoto != null) {
                productImageUrl = convertImageToBase64(mainProductPhoto.getProductImage());
            }
            ProductWithImageDTO productWithImageDTO = new ProductWithImageDTO(
                    product.getProductId(),
                    product.getProductName(),
                    product.getProductDescription(),
                    product.getProductPrice(),
                    productImageUrl,
                    product.getProductQuantity()
            );
            productWithImageDTOs.add(productWithImageDTO);
        }
        
        // 根據排序條件排序結果
        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "price_asc":
                    productWithImageDTOs.sort((a, b) -> a.getProductPrice().compareTo(b.getProductPrice()));
                    break;
                case "price_desc":
                    productWithImageDTOs.sort((a, b) -> b.getProductPrice().compareTo(a.getProductPrice()));
                    break;
                case "name_asc":
                    productWithImageDTOs.sort((a, b) -> a.getProductName().compareTo(b.getProductName()));
                    break;
                case "name_desc":
                    productWithImageDTOs.sort((a, b) -> b.getProductName().compareTo(a.getProductName()));
                    break;
            }
        }
        
        model.addAttribute("product", productWithImageDTOs);
        model.addAttribute("category", productCategoryService.getAllProductCategory());
        model.addAttribute("selectedCategoryId", productCategoryId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalElements", productPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sort", sort);
        
        // 建立分頁 URL
        StringBuilder pageURL = new StringBuilder("/product/list");
        boolean hasParam = false;
        if (productCategoryId != null) {
            pageURL.append("?productCategoryId=").append(productCategoryId);
            hasParam = true;
        }
        if (keyword != null && !keyword.isEmpty()) {
            pageURL.append(hasParam ? "&" : "?").append("keyword=").append(keyword);
            hasParam = true;
        }
        if (minPrice != null) {
            pageURL.append(hasParam ? "&" : "?").append("minPrice=").append(minPrice);
            hasParam = true;
        }
        if (maxPrice != null) {
            pageURL.append(hasParam ? "&" : "?").append("maxPrice=").append(maxPrice);
            hasParam = true;
        }
        if (sort != null && !sort.isEmpty()) {
            pageURL.append(hasParam ? "&" : "?").append("sort=").append(sort);
        }
        model.addAttribute("pageURL", pageURL.toString());
        
        return "front-end/product/listProduct";
    }

    // 取得商品主圖（排序第一張）
    @GetMapping("/product/photo/{productId}")
    @ResponseBody
    public ResponseEntity<byte[]> getProductPhoto(@PathVariable Integer productId) {
        ProductPhoto photo = productPhotoService.getMainProductPhotoByProductId(productId);
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

    //前台-商品詳細頁
    @GetMapping("/product/detail/{productId}")
    public String productDetail(@PathVariable Integer productId, Model model) {
        try {
            // 獲取商品資料
            Product product = productService.getProductById(productId);
            if (product == null) {
                return "redirect:/product/list"; // 商品不存在時重導向到商品列表
            }

            // 獲取商品的所有圖片
            List<ProductPhoto> productPhotos = productPhotoService.getProductPhotosByProductId(productId);
            List<String> productImageUrls = new ArrayList<>();

            if (productPhotos == null || productPhotos.isEmpty()) {
                // 如果沒有圖片，使用預設圖片
                productImageUrls.add("https://dummyimage.com/400x300/808080&text=No+Image");
            } else {
                // 將所有圖片轉換為 Base64
                for (ProductPhoto photo : productPhotos) {
                    if (photo != null && photo.getProductImage() != null) {
                        String imageUrl = convertImageToBase64(photo.getProductImage());
                        productImageUrls.add(imageUrl);
                    }
                }
            }

            // 如果沒有成功轉換的圖片，使用預設圖片
            if (productImageUrls.isEmpty()) {
                productImageUrls.add("https://dummyimage.com/400x300/808080&text=No+Image");
            }

            // 獲取商品分類資訊
            String categoryName = "未分類";
            if (product.getProductCategory() != null) {
                try {
                    categoryName = product.getProductCategory().getProductCategoryName();
                    if (categoryName == null || categoryName.trim().isEmpty()) {
                        categoryName = "未分類";
                    }
                } catch (Exception e) {
                    categoryName = "未分類";
                }
            }
            System.out.println("商品ID=" + productId + "，庫存=" + product.getProductQuantity() + "，狀態=" + product.getProductStatus());
            // 將資料添加到模型中
            model.addAttribute("product", product);
            model.addAttribute("productImages", productImageUrls);
            model.addAttribute("categoryName", categoryName);
            model.addAttribute("mainImage", productImageUrls.get(0));

            return "front-end/product/detailProduct";
        } catch (Exception e) {
            // 記錄錯誤並重導向
            System.err.println("商品詳細頁錯誤: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/product/list";
        }
    }

    //後台-商品列表
    @GetMapping("/backend/product/list")
    @PreAuthorize("hasAuthority('product')")
    public String listProduct(
        @RequestParam(value = "categoryId", required = false) Integer categoryId,
        @RequestParam(value = "status", required = false) Byte status,
        @RequestParam(value = "search", required = false) String search,
        Model model) {
        List<Product> productList;
        if (search != null && !search.trim().isEmpty()) {
            productList = productService.searchProducts(categoryId, status, search.trim());
        } else if (categoryId != null && status != null) {
            productList = productService.getProductByCategoryIdAndStatus(categoryId, status);
        } else if (categoryId != null) {
            productList = productService.getProductByProductCategoryId(categoryId);
        } else if (status != null) {
            productList = productService.getProductByStatus(status);
        } else {
            productList = productService.getAllProducts();
        }
        model.addAttribute("productList", productList);
        model.addAttribute("product", new Product());
        model.addAttribute("category", productCategoryService.getAllProductCategory());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("search", search);
        model.addAttribute("sidebarActive", "product-list");
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
