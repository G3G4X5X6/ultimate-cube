package com.g3g4x5x6;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.MainFrame;
import com.g3g4x5x6.utils.ConfigUtil;
import com.g3g4x5x6.utils.DbUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

import com.g3g4x5x6.utils.CheckUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


@Slf4j
public class App {
    public static MainFrame mainFrame;
    public static Properties properties = loadProperties();

    public static void main(String[] args) {
        // 检查程序运行环境
        CheckUtil.checkEnv();
        // 初始化数据库
        DbUtil.createDatabase();
        // TODO 转换配置，如：{home}

        // 启动主程序
        SwingUtilities.invokeLater(App::createGUI);
    }

    private static void createGUI() {
        // 配置主题皮肤
        initFlatLaf();

        // 程序主窗口 <html><font style='color:green'></font></html>
        mainFrame = new MainFrame();
//        mainFrame.setTitle("<html><font style='color:green'>" + properties.getProperty("app.title") + "</font></html>");
        mainFrame.setTitle(properties.getProperty("app.title"));
        mainFrame.pack();
        mainFrame.setVisible(true);

        // 初始化系统托盘
        initSystemTray();

        log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>");
        log.info("<<<<<<<<<<<<<<<<<<<<<程序启动完成>>>>>>>>>>>>>>>>>>>>>");
        log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    private static void initFlatLaf() {
        try {
            if (properties.getProperty("app.theme.enable").equalsIgnoreCase("false")) {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } else {
                UIManager.setLookAndFeel(properties.getProperty("app.theme.class"));
                log.debug("加载主题：" + properties.getProperty("app.theme.class"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Failed to initialize LaF !!!!!!!! \n" + ex.getMessage());
        }
        UIManager.put( "TextComponent.arc", 5 );
//        log.debug(String.valueOf(UIManager.getColor("Panel.background")));
//        log.debug(String.valueOf(UIManager.getColor("Panel.foreground")));
    }

    private static void initSystemTray() {
        /*
         * 添加系统托盘
         */
        if (SystemTray.isSupported()) {
            // 获取当前平台的系统托盘
            SystemTray tray = SystemTray.getSystemTray();

            // 加载一个图片用于托盘图标的显示
            Image image = new FlatSVGIcon("com/g3g4x5x6/ui/icons/digitalOceanSpaces.svg").getImage();

            // 创建点击图标时的弹出菜单
            PopupMenu popupMenu = new PopupMenu();

            MenuItem openItem = new MenuItem();
            openItem.setLabel("Open");
            MenuItem exitItem = new MenuItem();
            exitItem.setLabel("Quit");

            openItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 点击打开菜单时显示窗口
                    if (!mainFrame.isShowing()) {
                        mainFrame.setVisible(true);
                    }
                }
            });
            exitItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 点击退出菜单时退出程序
                    System.exit(0);
                }
            });

            popupMenu.add(openItem);
            popupMenu.add(exitItem);

            // 创建一个托盘图标
            TrayIcon trayIcon = new TrayIcon(image, "UltimateShell's SystemTray", popupMenu);

            // 托盘图标自适应尺寸
            trayIcon.setImageAutoSize(true);

            trayIcon.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("托盘图标被右键点击");
                }
            });
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    switch (e.getButton()) {
                        case MouseEvent.BUTTON1: {
                            System.out.println("托盘图标被鼠标左键被点击");
                            mainFrame.setVisible(true);
                            break;
                        }
                        case MouseEvent.BUTTON2: {
                            System.out.println("托盘图标被鼠标中键被点击");
                            break;
                        }
                        case MouseEvent.BUTTON3: {
                            System.out.println("托盘图标被鼠标右键被点击");
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                }
            });

            // 添加托盘图标到系统托盘
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    private static Properties loadProperties() {
        // 加载配置
        Properties properties = new Properties();
        try {
            InputStreamReader inputStream = new InputStreamReader(new FileInputStream(ConfigUtil.getPropertiesPath()), StandardCharsets.UTF_8);
            properties.load(inputStream);
        } catch (Exception ignored) {

        }
        return properties;
    }
}
