package com.g3g4x5x6;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme;
import com.g3g4x5x6.ui.MainFrame;
import com.g3g4x5x6.utils.DbUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;

@Slf4j
public class App {

    public static void main(String[] args) {
        // 检查程序运行环境
        checkEnv();
        // 初始化数据库
        createDatabase();
        // 启动主界面
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        // 此处处于 事件调度线程
                        createGUI();
                    }
                }
        );
    }

    public static void createGUI() {
        // 此处处于 事件调度线程
        // TODO 配置主题皮肤
        try {
            UIManager.setLookAndFeel( new FlatLightLaf() );
//            UIManager.setLookAndFeel(new FlatDarkPurpleIJTheme());
//            UIManager.setLookAndFeel(new com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMoonlightIJTheme());
//            Class themeClass = App.class.getClassLoader().loadClass("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialLighterIJTheme");
//            UIManager.setLookAndFeel((LookAndFeel) themeClass.getConstructor().newInstance());
        } catch (Exception ex) {
            log.error("Failed to initialize LaF");
        }

        // TODO 启动主界面
        MainFrame mainFrame = new MainFrame();
        mainFrame.pack();
        mainFrame.setVisible(true);
        log.info("主线程启动完成");
    }

    public static void checkEnv() {
        // TODO 检查程序工作目录

        // TODO 检查用户配置
    }

    public static void createDatabase() {
        // TODO 后续切换为 H2 数据库
        String dbPath = System.getProperties().getProperty("user.home") + "/.ultimateshell/ultilmateshell.sqlite";
        if (!Files.exists(Path.of(dbPath))) {
            log.debug("数据库不存在，开始创建数据库");
            // TODO 创建数据库
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

                connection.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.debug("找到数据库：" + dbPath);
        }
    }
}
