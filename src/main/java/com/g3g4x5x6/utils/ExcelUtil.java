package com.g3g4x5x6.utils;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ExcelUtil {
    private ExcelUtil(){

    }

    public static void exportSession(Sheet sessionSheet){
        try {
            Connection connection = DbUtil.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM session");
            int i = 0;
            while (resultSet.next()) {
                //3.根据sheet创建row
                Row row = sessionSheet.createRow(i);
                //4.根据row创建cell
                row.createCell(0).setCellValue(resultSet.getString("session_name"));
                row.createCell(1).setCellValue(resultSet.getString("protocol"));
                row.createCell(2).setCellValue(resultSet.getString("address"));
                row.createCell(3).setCellValue(resultSet.getString("port"));
                row.createCell(4).setCellValue(resultSet.getString("auth_type"));
                row.createCell(5).setCellValue(resultSet.getString("username"));
                row.createCell(6).setCellValue(resultSet.getString("password"));
                row.createCell(7).setCellValue(resultSet.getString("private_key"));
                row.createCell(8).setCellValue(resultSet.getString("create_time"));
                row.createCell(9).setCellValue(resultSet.getString("access_time"));
                row.createCell(10).setCellValue(resultSet.getString("modified_time"));
                row.createCell(11).setCellValue(resultSet.getString("comment"));
                i++;
            }
            DbUtil.close(statement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void exportTag(Sheet tagSheet){
        try {
            Connection connection = DbUtil.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM tag");
            int i = 0;
            while (resultSet.next()) {
                //3.根据sheet创建row
                Row row = tagSheet.createRow(i);
                //4.根据row创建cell
                row.createCell(0).setCellValue(resultSet.getString("tag"));
                i++;
            }
            DbUtil.close(statement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void exportRelation(Sheet relationSheet){
        try {
            Connection connection = DbUtil.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM relation");
            int i = 0;
            while (resultSet.next()) {
                //3.根据sheet创建row
                Row row = relationSheet.createRow(i);
                //4.根据row创建cell
                row.createCell(0).setCellValue(resultSet.getString("session"));
                row.createCell(1).setCellValue(resultSet.getString("tag"));
                i++;
            }
            DbUtil.close(statement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void importBackup(String sql){
        try {
            Connection connection = DbUtil.getConnection();
            Statement statement = connection.createStatement();
            // 更新 session 表
            statement.executeUpdate(sql);
            DbUtil.close(statement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
