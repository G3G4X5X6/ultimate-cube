package com.g3g4x5x6.utils;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;


@Slf4j
public class DbUtil {
    private static Connection connection = null;
    private DbUtil() {

    }

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        String dbPath = System.getProperties().getProperty("user.home") + "/.ultimateshell/ultilmateshell.sqlite";
        if (connection != null){
            return connection;
        }else {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        }
        return connection;
    }

    public static void createDatabase() {
        // TODO 后续切换为 H2 数据库
        String dbPath = System.getProperties().getProperty("user.home") + "/.ultimateshell/ultilmateshell.sqlite";
        if (!Files.exists(Path.of(dbPath))) {
            log.debug("数据库不存在，开始创建数据库");
            // 创建数据库
            try {
                Connection connection = DbUtil.getConnection();

                Statement statement = connection.createStatement();
                int effectSession = statement.executeUpdate("CREATE TABLE \"session\" (\n" +
                        "  \"id\" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                        "  \"session_name\" TEXT NOT NULL DEFAULT '新建会话',\n" +
                        "  \"protocol\" TEXT NOT NULL DEFAULT 'SSH',\n" +
                        "  \"address\" TEXT NOT NULL DEFAULT '127.0.0.1',\n" +
                        "  \"port\" TEXT NOT NULL DEFAULT 22,\n" +
                        "  \"auth_type\" TEXT NOT NULL DEFAULT 'password',\n" +
                        "  \"username\" TEXT,\n" +
                        "  \"password\" TEXT,\n" +
                        "  \"private_key\" TEXT,\n" +
                        "  \"create_time\" TEXT NOT NULL,\n" +
                        "  \"access_time\" TEXT NOT NULL,\n" +
                        "  \"modified_time\" TEXT NOT NULL,\n" +
                        "  \"comment\" TEXT NOT NULL DEFAULT 'Your comment'\n" +
                        ");\n" +
                        "PRAGMA foreign_keys = true;");

                int effectTag = statement.executeUpdate("CREATE TABLE \"tag\" (\n" +
                        "  \"id\" INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "  \"tag\" TEXT\n" +
                        ");\n" +
                        "PRAGMA foreign_keys = true;");

                int effectRelation = statement.executeUpdate("CREATE TABLE \"relation\" (\n" +
                        "  \"id\" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                        "  \"session\" integer NOT NULL,\n" +
                        "  \"tag\" integer NOT NULL,\n" +
                        "  CONSTRAINT \"session\" FOREIGN KEY (\"session\") REFERENCES \"session\" (\"id\") ON DELETE NO ACTION ON UPDATE NO ACTION,\n" +
                        "  CONSTRAINT \"tag\" FOREIGN KEY (\"tag\") REFERENCES \"tag\" (\"id\") ON DELETE NO ACTION ON UPDATE NO ACTION\n" +
                        ");\n" +
                        "PRAGMA foreign_keys = true;");

                int addTag = statement.executeUpdate("INSERT INTO tag VALUES (null, '会话标签');");

                int effectSettings = statement.executeUpdate("CREATE TABLE settings (" +
                        " id INTEGER NOT NULL, " +
                        " key TEXT NOT NULL, " +
                        " value TEXT NOT NULL, " +
                        " comment TEXT, " +
                        "  PRIMARY KEY ( id ) " +
                        ");");

                int insertSettings = statement.executeUpdate("INSERT INTO settings VALUES (null, 'theme', '3', null );\n" +
                        "INSERT INTO settings VALUES (null, 'theme_enable', '0', null);");

                int effectTheme = statement.executeUpdate("CREATE TABLE theme (\n" +
                        "  id INTEGER NOT NULL,\n" +
                        "  name TEXT NOT NULL,\n" +
                        "  class TEXT NOT NULL,\n" +
                        "  foreground TEXT,\n" +
                        "  background TEXT,\n" +
                        "  PRIMARY KEY (id)\n" +
                        ");");

                int insertTheme = statement.executeUpdate("INSERT INTO \"theme\" VALUES (1, 'Arc', 'com.formdev.flatlaf.intellijthemes.FlatArcIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (2, 'Arc - Orange', 'com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (3, 'Arc Dark', 'com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme', '222,222,222', '64,70,81');\n" +
                        "INSERT INTO \"theme\" VALUES (4, 'Arc Dark - Orange', 'com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme', '222,222,222', '64,70,81');\n" +
                        "INSERT INTO \"theme\" VALUES (5, 'Carbon', 'com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (6, 'Cobalt 2', 'com.formdev.flatlaf.intellijthemes.FlatCobalt2IJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (7, 'Cyan light', 'com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (8, 'Dark Flat', 'com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (9, 'Dark purple', 'com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (10, 'Dracula', 'com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (11, 'Gradianto Dark Fuchsia', 'com.formdev.flatlaf.intellijthemes.FlatGradiantoDarkFuchsiaIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (12, 'Gradianto Deep Ocean', 'com.formdev.flatlaf.intellijthemes.FlatGradiantoDeepOceanIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (13, 'Gradianto Midnight Blue', 'com.formdev.flatlaf.intellijthemes.FlatGradiantoMidnightBlueIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (14, 'Gradianto Nature Green', 'com.formdev.flatlaf.intellijthemes.FlatGradiantoNatureGreenIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (15, 'Gray', 'com.formdev.flatlaf.intellijthemes.FlatGrayIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (16, 'Gruvbox Dark Hard', 'com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkHardIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (17, 'Gruvbox Dark Medium', 'com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkMediumIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (18, 'Gruvbox Dark Soft', 'com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkSoftIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (19, 'Hiberbee Dark', 'com.formdev.flatlaf.intellijthemes.FlatHiberbeeDarkIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (20, 'High contrast', 'com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (21, 'Light Flat', 'com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (22, 'Material Design Dark', 'com.formdev.flatlaf.intellijthemes.FlatMaterialDesignDarkIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (23, 'Monocai', 'com.formdev.flatlaf.intellijthemes.FlatMonocaiIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (24, 'Nord', 'com.formdev.flatlaf.intellijthemes.FlatNordIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (25, 'One Dark', 'com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (26, 'Solarized Dark', 'com.formdev.flatlaf.intellijthemes.FlatSolarizedDarkIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (27, 'Solarized Light', 'com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (28, 'Spacegray', 'com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (29, 'Vuesion', 'com.formdev.flatlaf.intellijthemes.FlatVuesionIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (30, 'Arc Dark (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatArcDarkIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (31, 'Arc Dark Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatArcDarkContrastIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (32, 'Atom One Dark (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (33, 'Atom One Dark Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkContrastIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (34, 'Atom One Light (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneLightIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (35, 'Atom One Light Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneLightContrastIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (36, 'Dracula (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatDraculaIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (37, 'Dracula Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatDraculaContrastIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (38, 'GitHub (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (39, 'GitHub Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubContrastIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (40, 'GitHub Dark (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (41, 'GitHub Dark Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkContrastIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (42, 'Light Owl (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatLightOwlIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (43, 'Light Owl Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatLightOwlContrastIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (44, 'Material Darker (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (45, 'Material Darker Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerContrastIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (46, 'Material Deep Ocean (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDeepOceanIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (47, 'Material Deep Ocean Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDeepOceanContrastIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (48, 'Material Lighter (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialLighterIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (49, 'Material Lighter Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialLighterContrastIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (50, 'Material Oceanic (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialOceanicIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (51, 'Material Oceanic Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialOceanicContrastIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (52, 'Material Palenight (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialPalenightIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (53, 'Material Palenight Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialPalenightContrastIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (54, 'Monokai Pro (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMonokaiProIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (55, 'Monokai Pro Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMonokaiProContrastIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (56, 'Moonlight (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMoonlightIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (57, 'Moonlight Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMoonlightContrastIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (58, 'Night Owl (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatNightOwlIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (59, 'Night Owl Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatNightOwlContrastIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (60, 'Solarized Dark (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedDarkIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (61, 'Solarized Dark Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedDarkContrastIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (62, 'Solarized Light (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedLightIJTheme', '0,0,0', '255,255,255');\n" +
                        "INSERT INTO \"theme\" VALUES (63, 'Solarized Light Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedLightContrastIJTheme', '0,0,0', '255,255,255');");

                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.debug("找到数据库：" + dbPath);
        }
    }


    public static Boolean updateAccessTime(long time, String where) {
        try {
            Connection connection = DbUtil.getConnection();
            Statement statement = connection.createStatement();

            String sql = "UPDATE session SET access_time='" + time + "' WHERE " + where;

            int result = statement.executeUpdate(sql);
            log.debug("更新访问时间返回码：" + result);
            if (result >= 1) {
                return true;
            }
            DbUtil.close(statement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }


    public static void close(Statement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }


    public static void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
