package com.islevilla.lai.members.model;

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
public class MembersImageBatchImporter {
	@Autowired
    private DataSource dataSource;

    @PostConstruct
    public void importMembersImages() {
        String basePath = "src/main/resources/static/img/members/";

        try (Connection conn = dataSource.getConnection()) {
            // 查詢所有members資料
            String selectSql = "SELECT member_id FROM members";
            try (PreparedStatement selectPs = conn.prepareStatement(selectSql);
                 ResultSet rs = selectPs.executeQuery()) {

                String updateSql = "UPDATE members SET member_photo = ? WHERE member_id = ?";
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    while (rs.next()) {
                        int memberId = rs.getInt("member_id");

                        // 設定檔名格式
                        String fileName = "member" + memberId + ".png";
                        File img = new File(basePath + fileName);

                        if (img.exists()) {
                            try (FileInputStream fis = new FileInputStream(img)) {
                                updatePs.setBinaryStream(1, fis, (int) img.length());
                                updatePs.setInt(2, memberId);
                                updatePs.executeUpdate();
                                System.out.println("已匯入圖片: " + fileName + " -> member_id=" + memberId);
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
