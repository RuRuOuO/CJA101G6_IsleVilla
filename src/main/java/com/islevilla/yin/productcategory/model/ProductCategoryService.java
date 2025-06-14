package com.islevilla.yin.productcategory.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class ProductCategoryService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    public void addProductCategory(ProductCategory productCategory) {
        productCategoryRepository.save(productCategory);
    }
    public void updateProductCategory(ProductCategory productCategory) {
        productCategoryRepository.save(productCategory);
    }
    public void deleteProductCategory(Integer productCategoryId) {
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
