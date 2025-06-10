package com.islevilla.yin.productcategory.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ProductCategoryService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    public void addProduct(ProductCategory productCategory) {
        productCategoryRepository.save(productCategory);
    }
    public void updateProduct(ProductCategory productCategory) {
        productCategoryRepository.save(productCategory);
    }
    public void deleteProduct(Integer productCategoryId) {
        if(productCategoryRepository.existsById(productCategoryId)){
            productCategoryRepository.deleteById(productCategoryId);
        }
    }

    public List<ProductCategory> getAllCategories() {
        return productCategoryRepository.findAll();
    }
}
