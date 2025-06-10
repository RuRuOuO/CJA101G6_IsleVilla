package com.islevilla.yin.productcategory.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_category")
@Data
public class ProductCategory {
    @Id
    @Column(name = "product_category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productCategoryId;
    @Column(name = "product_category_name")
    private String productCategoryName;
}