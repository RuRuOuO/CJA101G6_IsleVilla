package com.islevilla.yin.productphoto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Component
public class ProductPhotoBatchImporter {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void importProductImages() {
        String basePath = "src/main/resources/static/img/product/";

        try (Connection conn = dataSource.getConnection()) {
            // 1. 查詢所有 product_photo 資料
            String selectSql = "SELECT product_photo_id, product_id, display_order FROM product_photo";
            try (PreparedStatement selectPs = conn.prepareStatement(selectSql);
                 ResultSet rs = selectPs.executeQuery()) {

                String updateSql = "UPDATE product_photo SET product_image=? WHERE product_photo_id=?";
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    while (rs.next()) {
                        int productPhotoId = rs.getInt("product_photo_id");
                        int productId = rs.getInt("product_id");
                        int displayOrder = rs.getInt("display_order");

                        // 檔名規則：01-1.png, 01-2.png, 02-1.png ...
                        String productIdStr = String.format("%02d", productId);
                        String fileName = productIdStr + "-" + (displayOrder + 1) + ".png";
                        File img = new File(basePath + fileName);

                        if (img.exists()) {
                            try (FileInputStream fis = new FileInputStream(img)) {
                                updatePs.setBinaryStream(1, fis, (int) img.length());
                                updatePs.setInt(2, productPhotoId);
                                updatePs.executeUpdate();
                                System.out.println("已匯入圖片: " + fileName + " -> product_photo_id=" + productPhotoId);
                            }
                        } else {
                            System.out.println("找不到圖片: " + fileName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 