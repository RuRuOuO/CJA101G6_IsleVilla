package com.islevilla.wei.feedback.model;

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
public class FeedbackImageBatchImporter {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void importFeedbackImages() {
        String basePath = "src/main/resources/static/img/feedback/";

        try (Connection conn = dataSource.getConnection()) {
            // 查詢所有 feedback 資料
            String selectSql = "SELECT fb_id FROM feedback";
            try (PreparedStatement selectPs = conn.prepareStatement(selectSql);
                 ResultSet rs = selectPs.executeQuery()) {

                String updateSql = "UPDATE feedback SET fb_image = ? WHERE fb_id = ?";
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    while (rs.next()) {
                        int fbId = rs.getInt("fb_id");

                        // 設定檔名格式
                        String fileName = "feedback" + fbId + ".png";
                        File img = new File(basePath + fileName);

                        if (img.exists()) {
                            try (FileInputStream fis = new FileInputStream(img)) {
                                updatePs.setBinaryStream(1, fis, (int) img.length());
                                updatePs.setInt(2, fbId);
                                updatePs.executeUpdate();
                                System.out.println("已匯入圖片: " + fileName + " -> fb_id=" + fbId);
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