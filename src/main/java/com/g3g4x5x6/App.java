package com.g3g4x5x6;

import com.formdev.flatlaf.FlatLightLaf;
import com.g3g4x5x6.ui.MainFrame;
import com.g3g4x5x6.ui.dialog.LockDialog;
import com.g3g4x5x6.ui.panels.ssh.SessionInfo;
import com.g3g4x5x6.utils.CheckUtil;
import com.g3g4x5x6.utils.ConfigUtil;
import com.g3g4x5x6.utils.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.PropertyConfigurator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
public class App {
    public static MainFrame mainFrame;
    public static Properties properties = loadProperties();
    public static AtomicBoolean lockState = new AtomicBoolean(false);
    public static LinkedHashMap<String, SessionInfo> sessionInfos = new LinkedHashMap<>();
    public static String lockPassword = "";

    public static void main(String[] args) {
        // 检查程序运行环境
        CheckUtil.checkEnv();
        // 加载自定义日志配置
        initLog4j();
        // 启动主程序
        SwingUtilities.invokeLater(App::createGUI);
    }

    private static void createGUI() {
        // 配置主题皮肤
        initFlatLaf();

        mainFrame = new MainFrame();
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
        UIManager.put("TextComponent.arc", 5);
    }

    private static void initSystemTray() {
        /*
         * 添加系统托盘
         */
        if (SystemTray.isSupported()) {
            // 获取当前平台的系统托盘
            SystemTray tray = SystemTray.getSystemTray();
            // 加载一个图片用于托盘图标的显示
            Image image = null;
            try {
                image = ImageIO.read(Objects.requireNonNull(App.class.getClassLoader().getResource("icon.png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 创建点击图标时的弹出菜单
            PopupMenu popupMenu = new PopupMenu();

            MenuItem openItem = new MenuItem();
            openItem.setLabel("Open");
            MenuItem exitItem = new MenuItem();
            exitItem.setLabel("Quit");

            openItem.addActionListener(e -> {
                // 点击打开菜单时显示窗口
                openApp();
            });
            exitItem.addActionListener(e -> {
                // 点击退出菜单时退出程序
                System.exit(0);
            });

            popupMenu.add(openItem);
            popupMenu.add(exitItem);

            // 创建一个托盘图标
            assert image != null;
            TrayIcon trayIcon = new TrayIcon(image, "UltimateShell's SystemTray", popupMenu);
            // 托盘图标自适应尺寸
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    switch (e.getButton()) {
                        case MouseEvent.BUTTON1: {
                            System.out.println("托盘图标被鼠标左键被点击");
                            openApp();
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

    private static void openApp() {
        if (!mainFrame.isShowing()) {
            if (App.lockState.get()) {
                LockDialog lockDialog = new LockDialog();
                lockDialog.setVisible(true);
            } else {
                mainFrame.setVisible(true);
            }
        }
    }

    private static Properties loadProperties() {
        // 初始化应用配置
        if (!Files.exists(Path.of(ConfigUtil.getPropertiesPath()))) {
            try {
                InputStream appIn = App.class.getClassLoader().getResourceAsStream("application.properties");
                assert appIn != null;
                Files.copy(appIn, Path.of(ConfigUtil.getPropertiesPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 初始化日志配置
        if (!Files.exists(Path.of(ConfigUtil.getWorkPath() + "/log4j.properties"))) {
            try {
                InputStream logIn = App.class.getClassLoader().getResourceAsStream("log4j.properties");
                assert logIn != null;
                Files.copy(logIn, Path.of(ConfigUtil.getWorkPath() + "/log4j.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 加载配置
        Properties properties = new Properties();
        try {
            InputStreamReader inputStream = new InputStreamReader(new FileInputStream(ConfigUtil.getPropertiesPath()), StandardCharsets.UTF_8);
            properties.load(inputStream);
        } catch (Exception ignored) {

        }
        return properties;
    }

    private static void initLog4j() {
        try {
            if (App.properties.getProperty("app.log.setting.enable").equalsIgnoreCase("true")) {
                PropertyConfigurator.configureAndWatch(App.properties.getProperty("app.log.setting.path").replace("{workspace}", ConfigUtil.getWorkPath()));
                log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<已加载自定义日志配置>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            }
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }
}
