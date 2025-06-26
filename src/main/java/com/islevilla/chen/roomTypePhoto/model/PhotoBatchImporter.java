package com.islevilla.chen.roomTypePhoto.model;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
@Component
public class PhotoBatchImporter {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void importProductImages() {
        String basePath = "src/main/resources/static/img/roomTypePhoto/";

        try (Connection conn = dataSource.getConnection()) {
            // 1. 查詢所有 product_photo 資料
            String selectSql = "SELECT room_type_photo_id, room_type_id,display_order FROM room_type_photo";
            try (PreparedStatement selectPs = conn.prepareStatement(selectSql);
                 ResultSet rs = selectPs.executeQuery()) {

                String updateSql = "UPDATE room_type_photo SET room_type_photo=? WHERE room_type_photo_id=?";
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    while (rs.next()) {
                        int roomTypePhotoId = rs.getInt("room_type_photo_id");
                        int roomTypeId = rs.getInt("room_type_id");
                        int displayOrder = rs.getInt("display_order");

                        // 檔名規則：01-1.png, 01-2.png, 02-1.png ... 數字格式化成固定寬度的「兩位數字」
                        String roomTypePhotoIdStr = String.format("%02d", roomTypeId);
                        String fileName = roomTypePhotoIdStr + "-" + (displayOrder + 1) + ".png";
                        File img = new File(basePath + fileName);

                        if (img.exists()) {
                            try (FileInputStream fis = new FileInputStream(img)) {
                                updatePs.setBinaryStream(1, fis, (int) img.length());
                                updatePs.setInt(2, roomTypePhotoId);
                                updatePs.executeUpdate();
                                System.out.println("已匯入圖片: " + fileName + " -> room_type_photo_id=" + roomTypePhotoId);
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
