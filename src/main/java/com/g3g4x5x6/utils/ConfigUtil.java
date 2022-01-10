package com.g3g4x5x6.utils;

import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TextStyle;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
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
        String work = Path.of(System.getProperties().getProperty("user.home") + "/.ultimateshell/").toString();
        File file = new File(work);
        if (!file.exists()){
            if (!file.mkdir()){
                log.debug("文件夹创建失败：" + work);
            }
        }
        log.debug(work);
        return work;
    }

    public static Boolean isExistTerminalColor() {
        Boolean flag = false;
        try {
            Connection connection = DbUtil.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT foreground, background FROM terminal_color WHERE theme = (SELECT value FROM settings WHERE key = 'theme')");
            while (resultSet.next()) {
                flag = true;
            }
            DbUtil.close(statement, resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return flag;
    }

    public static TextStyle getTextStyle() {
        if (!isEnableTheme()) {
            return new TextStyle(TerminalColor.BLACK, TerminalColor.WHITE);
        }
        String foreground = "";
        String background = "";
        if (isExistTerminalColor() && boolSettingValue("terminal_color_enable")) {
            log.debug("=========== 使用自定义配色 ===========");
            try {
                Connection connection = DbUtil.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT foreground, background FROM terminal_color WHERE theme = (SELECT value FROM settings WHERE key = 'theme')");
                while (resultSet.next()) {
                    foreground = resultSet.getString("foreground");
                    background = resultSet.getString("background");
                }
                DbUtil.close(statement, resultSet);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            log.debug("=========== 使用默认配色 ===========");
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
        }
        log.debug("Color: " + foreground + ":" + background);
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

    public static String getThemeClass(String themeId) {
        String themeClass = "";
        try {
            Connection connection = DbUtil.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT class FROM theme WHERE id = " + themeId);
            while (resultSet.next()) {
                themeClass = resultSet.getString("class");
            }
            DbUtil.close(statement, resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return themeClass;
    }

    /**
     * 插入新的设置项
     * @param key
     * @param value
     * @return
     */
    public static Boolean insertSetting(String key, String value) {
        try {
            Connection connection = DbUtil.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO settings VALUES (null, '" + key +"', '" + value +"', null);");
            DbUtil.close(statement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        log.debug("添加设置项 => " + key + "： " + value);
        return true;
    }

    /**
     * 更新存在的设置项
     * @param key
     * @param value
     * @return
     */
    public static Boolean updateSetting(String key, String value) {
        try {
            Connection connection = DbUtil.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE settings SET value='" + value + "' WHERE key = '" + key + "'");
            DbUtil.close(statement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        log.debug("更新设置项 => " + key + "： " + value);
        return true;
    }


    /**
     * 获取设置项的值
     * @param key
     * @return
     */
    public static String getSettingValue(String key){
        String value = "";
        try {
            Connection connection = DbUtil.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT value FROM settings WHERE key = '" + key +"'");
            while (resultSet.next()) {
                value = resultSet.getString("value");
            }
            DbUtil.close(statement, resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return value;
    }

    /**
     * 获取指定设置项的Boolean值
     * @param key
     * @return
     */
    public static Boolean boolSettingValue(String key){
        try {
            Connection connection = DbUtil.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT value FROM settings WHERE key = '" + key + "'");
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
