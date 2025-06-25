package com.islevilla.yin.productphoto;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_photo")
@Data
public class ProductPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_photo_id")
    private Integer productPhotoId;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "product_image")
    private byte[] productImage; // 使用 byte[] 存儲圖片

    @Column(name = "display_order")
    private Integer displayOrder;
}
