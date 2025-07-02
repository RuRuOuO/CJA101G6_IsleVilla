package com.islevilla.wei.news.model;

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
public class NewsImageBatchImporter {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void importNewsImages() {
        String basePath = "src/main/resources/static/img/news/";

        try (Connection conn = dataSource.getConnection()) {
            // 查詢所有news資料
            String selectSql = "SELECT news_id FROM news";
            try (PreparedStatement selectPs = conn.prepareStatement(selectSql);
                 ResultSet rs = selectPs.executeQuery()) {

                String updateSql = "UPDATE news SET news_image = ? WHERE news_id = ?";
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    while (rs.next()) {
                        int newsId = rs.getInt("news_id");

                        // 設定檔名格式
                        String fileName = "news" + newsId + ".png";
                        File img = new File(basePath + fileName);

                        if (img.exists()) {
                            try (FileInputStream fis = new FileInputStream(img)) {
                                updatePs.setBinaryStream(1, fis, (int) img.length());
                                updatePs.setInt(2, newsId);
                                updatePs.executeUpdate();
                                System.out.println("已匯入圖片: " + fileName + " -> news_id=" + newsId);
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
