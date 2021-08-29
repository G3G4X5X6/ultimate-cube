package com.g3g4x5x6.utils;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


@Slf4j
public class ConfigUtil {
    private ConfigUtil() {

    }

    public static Boolean isEnableTheme() {
        try {
            Connection connection = DbUtil.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT value FROM settings WHERE key = 'theme_enable'");
            while (resultSet.next()) {
                if (resultSet.getString("value").equals("0")) {
                    return false;
                }
            }
            DbUtil.close(statement, resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    public static String getThemeClass() {
        String themeClass = "";
        try {
            Connection connection = DbUtil.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT class FROM theme WHERE id = (SELECT value FROM settings WHERE key = 'theme')");
            while (resultSet.next()) {
                themeClass = resultSet.getString("class");
            }
            DbUtil.close(statement, resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return themeClass;
    }

    public static Boolean updateThemeEnableOption(String enable) {
        try {
            Connection connection = DbUtil.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE settings SET value='" + enable + "' WHERE key = 'theme_enable'");
            DbUtil.close(statement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        log.debug("updateThemeEnableOption： " + enable);
        return true;
    }

    public static Boolean updateThemeOption(String theme) {
        try {
            Connection connection = DbUtil.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE settings SET value='" + theme + "' WHERE key = 'theme'");
            DbUtil.close(statement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        log.debug("updateThemeEnableOption： " + theme);
        return true;
    }
}
