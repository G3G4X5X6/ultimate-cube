package com.g3g4x5x6;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.dialog.LockDialog;
import com.g3g4x5x6.ssh.SessionInfo;
import com.g3g4x5x6.utils.AppConfig;
import com.g3g4x5x6.utils.CheckUtil;
import com.g3g4x5x6.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.PropertyConfigurator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.diogonunes.jcolor.Ansi.colorize;
import static com.diogonunes.jcolor.Attribute.*;
import static java.awt.Frame.NORMAL;


@Slf4j
public class App {
    public static MainFrame mainFrame;
    public static Properties properties;
    public static AtomicBoolean lockState = new AtomicBoolean(false);
    public static LinkedHashMap<String, SessionInfo> sessionInfos = new LinkedHashMap<>();
    public static String lockPassword = "";

    public static void main(String[] args) {
        // 显示旗标
        showBanner();
        // 加载配置
        properties = loadProperties();
        // 检查程序运行环境
        CheckUtil.checkEnv();
        // 加载自定义日志配置
        initLog4j();
        // 启动主程序
        SwingUtilities.invokeLater(App::createGUI);
    }

    private static void createGUI() {
        // 设置GUI全局字体
//        initGlobalFont(new Font("sans", Font.PLAIN, 14));

        // 配置主题皮肤
        initFlatLaf();

        mainFrame = new MainFrame();
        mainFrame.setTitle(properties.getProperty("app.title"));
        mainFrame.pack();
        mainFrame.setVisible(true);

        // 初始化系统托盘
        initSystemTray();

        showBootstrapTips();
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
                image = ImageIO.read(Objects.requireNonNull(App.class.getClassLoader().getResource("icon.jpg")));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 创建右键图标时的弹出菜单：JPopupMenu
            JPopupMenu popupMenu = new JPopupMenu();

            JMenuItem openMenuItem = new JMenuItem("打开");
            openMenuItem.setIcon(new FlatSVGIcon("icons/start.svg"));
            openMenuItem.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openApp();
                }
            });

            JMenuItem exitMenuItem = new JMenuItem("退出");
            exitMenuItem.setIcon(new FlatSVGIcon("icons/exit.svg"));
            exitMenuItem.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });

            JMenuItem editorMenuItem = new JMenuItem("内置编辑器");
            editorMenuItem.setIcon(new FlatSVGIcon("icons/editScheme.svg"));
            editorMenuItem.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MainFrame.editorFrame.setVisible(true);
                }
            });

            JMenuItem nucleiMenuItem = new JMenuItem("Poc验证框架");
            nucleiMenuItem.setIcon(new FlatSVGIcon("icons/Administrator.svg"));
            nucleiMenuItem.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MainFrame.nucleiFrame.setVisible(true);
                }
            });

            popupMenu.add(openMenuItem);
            popupMenu.addSeparator();
            popupMenu.add(nucleiMenuItem);
            popupMenu.add(editorMenuItem);
            popupMenu.addSeparator();
            popupMenu.add(exitMenuItem);

            // 创建一个托盘图标
            assert image != null;
            DefaultTrayIcon trayIcon = new DefaultTrayIcon(image, "ultimatecube's SystemTray", popupMenu);
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
                            System.out.println("托盘图标被鼠标右键被点击，X:" + e.getX() + " <=> Y:" + e.getY());
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
                mainFrame.setExtendedState(NORMAL);
            }
        }
    }

    private static Properties loadProperties() {
        // 初始化应用配置
        if (!Files.exists(Path.of(AppConfig.getPropertiesPath()))) {
            try {
                InputStream appIn = App.class.getClassLoader().getResourceAsStream("application.properties");
                assert appIn != null;
                Files.copy(appIn, Path.of(AppConfig.getPropertiesPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 初始化日志配置
        if (!Files.exists(Path.of(AppConfig.getWorkPath() + "/log4j.properties"))) {
            try {
                InputStream logIn = App.class.getClassLoader().getResourceAsStream("log4j.properties");
                assert logIn != null;
                Files.copy(logIn, Path.of(AppConfig.getWorkPath() + "/log4j.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 加载配置
        Properties properties = new Properties();
        try {
            InputStreamReader inputStream = new InputStreamReader(new FileInputStream(AppConfig.getPropertiesPath()), StandardCharsets.UTF_8);
            properties.load(inputStream);
        } catch (Exception ignored) {

        }

        CommonUtil.terminalOutput("加载程序主配置：" + properties.toString());
        return properties;
    }

    private static void initLog4j() {
        try {
            if (App.properties.getProperty("app.log.setting.enable").equalsIgnoreCase("true")) {
                PropertyConfigurator.configureAndWatch(App.properties.getProperty("app.log.setting.path").replace("{workspace}", AppConfig.getWorkPath()));

                CommonUtil.terminalOutput("已加载自定义日志配置");
            }
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }

    private static void showBanner(){
        // Credits
        System.out.println(colorize("==============================================================", CYAN_TEXT(), BOLD()));
        System.out.print(colorize("\tPOWER BY ", BOLD(), BRIGHT_YELLOW_TEXT(), GREEN_BACK()));
        System.out.println(colorize("G3G4X5X6\t", BOLD(), BRIGHT_YELLOW_TEXT(), RED_BACK()));
        System.out.println(colorize("\nI hope you find it useful ;)", YELLOW_TEXT(), BOLD()));
        System.out.println(colorize("==============================================================", CYAN_TEXT(), BOLD()));
    }

    private static void showBootstrapTips(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(colorize("==============================================================", CYAN_TEXT(), BOLD()));
        System.out.print(colorize("\tStarted at ", BOLD(), BRIGHT_YELLOW_TEXT(), GREEN_BACK()));
        System.out.println(colorize(sdf.format(new Date()) + "\t", BOLD(), BRIGHT_YELLOW_TEXT(), RED_BACK()));
        System.out.println();
        System.out.print(colorize("——————————————————————————————————————————————————————————————", CYAN_TEXT(), BOLD()));
        System.out.println(colorize("\nGo for it!", YELLOW_TEXT(), BOLD()));
        System.out.print(colorize("——————————————————————————————————————————————————————————————", CYAN_TEXT(), BOLD()));
        System.out.println();
        System.out.println(colorize("==============================================================", CYAN_TEXT(), BOLD()));
    }
}
