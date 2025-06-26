package com.islevilla.yin.productcategory.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import com.islevilla.yin.product.model.ProductService;

@Component
public class ProductCategoryService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductService productService;

    public void addProductCategory(ProductCategory productCategory) {
        productCategoryRepository.save(productCategory);
    }
    public void updateProductCategory(ProductCategory productCategory) {
        productCategoryRepository.save(productCategory);
    }
    public void deleteProductCategory(Integer productCategoryId) {
        if (productService.getProductByProductCategoryId(productCategoryId).size() > 0) {
            throw new RuntimeException("商品類別內還有商品，不能刪除該類別");
        }
        if(productCategoryRepository.existsById(productCategoryId)){
            productCategoryRepository.deleteById(productCategoryId);
        }
    }
    public List<ProductCategory> getAllProductCategory() {
        return productCategoryRepository.findAll();
    }
    public ProductCategory getProductCategoryById(Integer productCategoryId) {
        return productCategoryRepository.findById(productCategoryId)
                .orElse(null);
    }

}
