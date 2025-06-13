package com.islevilla.yin.product.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public void addProduct(Product product) {
        productRepository.save(product);
    }
    public void updateProduct(Product product) {
        productRepository.save(product);
    }
    public void deleteProduct(Integer productId) {
        if(productRepository.existsById(productId)){
            productRepository.deleteById(productId);
        }
    }
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    public List<Product> getProductByProductCategoryId(Integer productCategoryId) {
        return productRepository.findByProductCategoryProductCategoryId(productCategoryId);
    }
    public Product getProductById(Integer productId) {
        return productRepository.findById(productId)
                .orElse(null);
    }

}
