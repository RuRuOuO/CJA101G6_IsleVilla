package com.islevilla.product.model;

import java.util.List;

public interface ProductDao {
    public int insert(Product product);
    public int update(Product product);
    public int delete(Integer productId);
    public Product findByPK(Integer productId);
    public List<Product> getAll();
//  public void findbyProductName(String productName);
}
