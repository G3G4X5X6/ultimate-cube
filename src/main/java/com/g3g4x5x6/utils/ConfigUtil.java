package com.g3g4x5x6.utils;

import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TextStyle;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


@Slf4j
public class ConfigUtil {
    private ConfigUtil() {

    }

    public static String getWorkPath() {
        String work = System.getProperties().getProperty("user.home") + "/.ultimateshell/";
        File file = new File(work);
        if (!file.exists())
            file.mkdir();
        return work;
    }

    public static TextStyle getTextStyle() {
        if (!isEnableTheme()) {
            return new TextStyle(TerminalColor.BLACK, TerminalColor.WHITE);
        }
        String foreground = "";
        String background = "";
        try {
            Connection connection = DbUtil.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT foreground, background FROM theme WHERE id = (SELECT value FROM settings WHERE key = 'theme')");
            while (resultSet.next()) {
                foreground = resultSet.getString("foreground");
                background = resultSet.getString("background");
            }
            DbUtil.close(statement, resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        int fr = Integer.parseInt(foreground.split(",")[0]);
        int fg = Integer.parseInt(foreground.split(",")[1]);
        int fb = Integer.parseInt(foreground.split(",")[2]);
        int br = Integer.parseInt(background.split(",")[0]);
        int bg = Integer.parseInt(background.split(",")[1]);
        int bb = Integer.parseInt(background.split(",")[2]);
        return new TextStyle(TerminalColor.rgb(fr, fg, fb), TerminalColor.rgb(br, bg, bb));
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
