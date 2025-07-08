package com.islevilla.yin.employee.model;

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
public class EmployeePhotoBatchLoader {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void importEmployeePhotos() {
        String basePath = "src/main/resources/static/img/employee/";

        try (Connection conn = dataSource.getConnection()) {
            // 查詢所有 employee 資料
            String selectSql = "SELECT employee_id FROM employee";
            try (PreparedStatement selectPs = conn.prepareStatement(selectSql);
                 ResultSet rs = selectPs.executeQuery()) {

                String updateSql = "UPDATE employee SET employee_photo = ? WHERE employee_id = ?";
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    while (rs.next()) {
                        int employeeId = rs.getInt("employee_id");

                        // 設定檔名格式
                        String fileName = "employee" + employeeId + ".jpg";
                        File img = new File(basePath + fileName);

                        if (img.exists()) {
                            try (FileInputStream fis = new FileInputStream(img)) {
                                updatePs.setBinaryStream(1, fis, (int) img.length());
                                updatePs.setInt(2, employeeId);
                                updatePs.executeUpdate();
                                System.out.println("已匯入圖片: " + fileName + " -> employee_id=" + employeeId);
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