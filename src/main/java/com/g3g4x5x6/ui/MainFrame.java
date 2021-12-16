package com.g3g4x5x6.ui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatToggleButton;
import com.g3g4x5x6.App;
import com.g3g4x5x6.Version;
import com.g3g4x5x6.ui.panels.SessionsManager;
import com.g3g4x5x6.ui.panels.tools.ExternalToolIntegration;
import com.g3g4x5x6.ui.panels.tools.ColorPicker;
import com.g3g4x5x6.ui.panels.tools.QRTool;
import com.g3g4x5x6.ui.settings.SettingsDialog;
import com.g3g4x5x6.ui.dialog.ThemeDialog;
import com.g3g4x5x6.ui.panels.dashboard.DashboardPane;
import com.g3g4x5x6.ui.panels.NewTabbedPane;
import com.g3g4x5x6.ui.panels.tools.FreeRdp;
import com.g3g4x5x6.utils.*;
import com.glavsoft.exceptions.CommonException;
import com.glavsoft.viewer.ParametersHandler;
import com.glavsoft.viewer.Viewer;
import com.glavsoft.viewer.cli.Parser;
import com.glavsoft.viewer.swing.mac.MacApplicationWrapper;
import com.glavsoft.viewer.swing.mac.MacUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.BiConsumer;

import static com.formdev.flatlaf.FlatClientProperties.*;


/**
 * 主界面
 */
@Slf4j
public class MainFrame extends JFrame implements MouseListener {

    public MainFrame() throws HeadlessException {
        // 主窗口设置
        this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);      // 提示确认退出
        this.setSize(new Dimension(1000, 700));
        this.setMinimumSize(new Dimension(900, 600));
        this.setPreferredSize(new Dimension(1000, 600));
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon(this.getClass().getClassLoader().getResource("icon.png")).getImage());

        // 初始化 ”菜单栏“
        initMenuBar();

        // 初始化 ”菜单栏——功能小图标“
        initFuncIconButton();

        // TODO 初始化 ”工具栏“
        initToolBar();

        // 初始化 ”主选项卡面板“
        initMainTabbedPane();

        // TODO 初始化 ”状态栏“
//        initStatusBar();
    }

    /**
     * 添加菜单动作
     */
    private void initMenuBar() {
        // 终端菜单
        JMenu openSessionMenu = new JMenu("打开会话");
        String rootPath = ConfigUtil.getWorkPath() + "/sessions/ssh/";
        File dir = new File(rootPath);
        try {
            initOpenSessionMenu(dir, openSessionMenu);
        } catch (IOException e) {
            e.printStackTrace();
        }
        terminalMenu.add(openSessionMenu);
        terminalMenu.add(myNewAction);
        terminalMenu.add(mySessionAction);

        // 查看菜单
        viewMenu.add(themeAction);

        // 选项菜单
        JMenuItem settingsItem = new JMenuItem("全局配置");
        settingsItem.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.ALT_DOWN_MASK));
        settingsItem.addActionListener(settingsAction);
        optionMenu.add(settingsItem);
        optionMenu.addSeparator();
        optionMenu.add(importSessionAction);
        optionMenu.add(exportSessionAction);

        // 帮助菜单
        helpMenu.add(githubAction);
        helpMenu.add(gitpageAction);
        helpMenu.addSeparator();
        helpMenu.add(myAboutAction);

        // 工具菜单
        JMenu myToolMenu = new JMenu("内置工具");
        myToolMenu.add(myEditorAction);
        toolMenu.add(myToolMenu);
        JMenu otherToolMenu = new JMenu("杂七杂八");
        otherToolMenu.add(encodeConversionAction);
        otherToolMenu.add(colorPickerAction);
        otherToolMenu.add(qrCodePickerAction);
        toolMenu.add(otherToolMenu);
        toolMenu.addSeparator();
        toolMenu.add(tightVNCAction);
        // 快捷键
        JMenuItem freeRdpItem = new JMenuItem("FreeRDP");
        freeRdpItem.setAccelerator(KeyStroke.getKeyStroke('D', InputEvent.ALT_DOWN_MASK));
        freeRdpItem.addActionListener(freeRDPAction);
        toolMenu.add(freeRdpItem);
        toolMenu.addSeparator();
        toolMenu.add(externalSubMenu);

        // 插件菜单
        pluginMenu.add(loadPluginAction);
        pluginMenu.add(managePluginAction);
        pluginMenu.addSeparator();
        pluginMenu.add(apiPluginAction);

        // 外部集成工具
        externalSubMenu.add(new AbstractAction("添加工具") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("添加工具");
                DialogUtil.info("敬请期待！");
            }
        });
        externalSubMenu.add(new AbstractAction("工具管理") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("工具管理");
                DialogUtil.info("敬请期待！");
            }
        });
        externalSubMenu.addSeparator();
        ExternalToolIntegration integration = new ExternalToolIntegration(externalSubMenu);

        menuBar.add(terminalMenu);
        menuBar.add(viewMenu);
        menuBar.add(optionMenu);
        menuBar.add(toolMenu);
        menuBar.add(pluginMenu);
        menuBar.add(helpMenu);
        this.setJMenuBar(menuBar);
    }

    private void initFuncIconButton() {
        // TODO add "Users" button to menubar
        FlatButton usersButton = new FlatButton();
        usersButton.setIcon(new FlatSVGIcon("icons/users.svg"));
        usersButton.setButtonType(FlatButton.ButtonType.toolBarButton);
        usersButton.setFocusable(false);
        usersButton.addActionListener(e -> JOptionPane.showMessageDialog(MainFrame.this, "Hello User! How are you?", "User", JOptionPane.INFORMATION_MESSAGE));

        // TODO 置顶图标按钮
        FlatToggleButton toggleButton = new FlatToggleButton();
        toggleButton.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/pinTab.svg"));
        toggleButton.setButtonType(FlatButton.ButtonType.toolBarButton);
        toggleButton.setToolTipText("窗口置顶");
        toggleButton.setFocusable(false);
        toggleButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (toggleButton.isSelected()) {
                    setAlwaysOnTop(true);
                    toggleButton.setToolTipText("取消置顶");
                } else {
                    setAlwaysOnTop(false);
                    toggleButton.setToolTipText("窗口置顶");
                }
            }
        });

        // TODO update button
        FlatButton updateBtn = new FlatButton();
        updateBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/ideUpdate.svg"));
        updateBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        updateBtn.setFocusable(false);

        menuBar.add(Box.createGlue());
        menuBar.add(usersButton);
        menuBar.add(toggleButton);
        // 添加更新按钮
        new Thread(() -> {
            latestVersion = CommonUtil.getLastestVersion();
            String currentVersion = "v" + Version.VERSION;
            if (!currentVersion .equals(latestVersion)) {
                menuBar.add(updateBtn);
                log.debug("添加更新按钮");
                updateBtn.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // 检查更新
                        String msg = null;
                        msg = "<html>当前版本：  <font color='red'>" + currentVersion + "</font<br>" +
                                "最新版本： <font color='green'>" + latestVersion + "</font><br><br>" +
                                "是否现在下载更新？</html>";
                        log.debug("Msg: " + msg);
                        int code = JOptionPane.showConfirmDialog(App.mainFrame, msg, "更新", JOptionPane.YES_NO_OPTION);
                        if (code == 0) {
                            // TODO 更新
                            log.debug("马上下载更新");
                            CommonUtil.getLatestJar();
                        } else {
                            // TODO 暂不更新
                            log.debug("暂不下载更新");
                        }
                    }
                });
            }
        }).start();
    }

    private void initToolBar() {

    }

    private void initMainTabbedPane() {
        // 选项卡面板UI设置
        UIManager.put("TabbedPane.tabInsets", new Insets(0, 10, 0, 10));

        // 添加主选项卡面板
        mainTabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        mainTabbedPane.setEnabled(true);
        mainTabbedPane.addMouseListener(this);
        initClosableTabs(mainTabbedPane);
        customComponents();     // 定制 ”选项卡面板“ 功能组件按钮

        // 添加 ”仪表盘“ 面板
        mainTabbedPane.addTab("仪表板",
                new FlatSVGIcon("com/g3g4x5x6/ui/icons/homeFolder.svg"),
                new DashboardPane());

        this.add(mainTabbedPane);
    }

    private void initStatusBar() {
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        statusBar = new JPanel();
        statusBar.setLayout(flowLayout);
        statusBar.add(new JLabel("状态栏"));
        this.add(statusBar, BorderLayout.SOUTH);
    }

    private void customComponents() {
        JToolBar leading = null;
        JToolBar trailing = null;
        leading = new JToolBar();
        leading.setFloatable(false);
        leading.setBorder(null);
        trailing = new JToolBar();
        trailing.setFloatable(false);
        trailing.setBorder(null);

        JButton dashboardBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/homeFolder.svg"));
        dashboardBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainTabbedPane.setSelectedIndex(0);
            }
        });

        JButton addBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/add.svg"));
        addBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainTabbedPane.insertTab("新建选项卡", new FlatSVGIcon("com/g3g4x5x6/ui/icons/addToDictionary.svg"), new NewTabbedPane(), "新建选项卡", mainTabbedPane.getTabCount());
                mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
            }
        });

        // TODO 选项卡面板前置工具栏，暂不使用
        leading.add(dashboardBtn);
//        mainTabbedPane.putClientProperty(TABBED_PANE_LEADING_COMPONENT, leading);
        // TODO 选项卡面板后置工具栏，待实现
        trailing.add(addBtn);
        trailing.add(Box.createHorizontalGlue());
        trailing.add(new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/commit.svg")));
        trailing.add(new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/diff.svg")));
        trailing.add(new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/listFiles.svg")));
        mainTabbedPane.putClientProperty(TABBED_PANE_TRAILING_COMPONENT, trailing);
    }

    public void initOpenSessionMenu(File directory, JMenuItem menuItem) throws IOException {
        // 是目录，获取该目录下面的所有文件（包括目录）
        File[] files = directory.listFiles();
        // 判断 files 是否为空？
        if (null != files) {
            // 遍历文件数组
            for (File f : files) {
                // 判断是否是目录？
                if (f.isDirectory()) {
                    // 是目录
                    System.out.println("目录绝对路径：" + f.getAbsolutePath());
                    initOpenSessionMenu(f, menuItem);
                } else {
                    // 不是目录，判断是否是文件？
                    if (f.isFile()) {
                        System.out.println("文件绝对路径：" + f.getAbsolutePath());
                        String json = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
                        JSONObject jsonObject = JSON.parseObject(json);
//                        String sessionAddress = jsonObject.getString("sessionAddress");
                        String sessionName = jsonObject.getString("sessionName");
                        String itemName = sessionName;
                        if (itemName.strip().equals(""))
                            itemName = f.getName().substring(f.getName().indexOf("_") + 1, f.getName().length() - 5);
                        JMenuItem tempItem = new JMenuItem(itemName);
                        tempItem.setToolTipText(f.getPath());
                        tempItem.addActionListener(new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                log.debug(f.getAbsolutePath());
                                new Thread(() -> SessionUtil.openSshSession(f.getAbsolutePath())).start();
                            }
                        });
                        menuItem.add(tempItem);
                    }
                }
            }
        }
    }

    private void initClosableTabs(JTabbedPane tabbedPane) {
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSABLE, true);
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_CALLBACK,
                (BiConsumer<JTabbedPane, Integer>) (tabPane, tabIndex) -> {
                    if (tabIndex != 0) {
                        mainTabbedPane.removeTabAt(tabIndex);
                    }
                });

    }

    /**
     * 内部监听器
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == 3) {
            popupMenu = new JPopupMenu();
            popupMenu.add(new AbstractAction("关闭当前") {
                @Override
                public void actionPerformed(ActionEvent e) {

                }
            });
            popupMenu.add(new AbstractAction("关闭其他") {
                @Override
                public void actionPerformed(ActionEvent e) {

                }
            });
            popupMenu.add(new AbstractAction("关闭所有") {
                @Override
                public void actionPerformed(ActionEvent e) {

                }
            });
            popupMenu.add(new AbstractAction("复制当前") {
                @Override
                public void actionPerformed(ActionEvent e) {

                }
            });
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }


    /**
     * 定义
     */
    public static JTabbedPane mainTabbedPane;
    public static JPanel statusBar;

    // TODO JFrame 组件定义
    private JMenuBar menuBar = new JMenuBar();
    private JMenu terminalMenu = new JMenu("终端");
    private JMenu viewMenu = new JMenu("查看");
    private JMenu optionMenu = new JMenu("选项");
    private JMenu toolMenu = new JMenu("工具");
    private JMenu pluginMenu = new JMenu("插件");
    private JMenu helpMenu = new JMenu("帮助");
    private JMenu externalSubMenu = new JMenu("外部集成工具");
    private JPopupMenu popupMenu = new JPopupMenu();

    private String latestVersion;

    // TODO 菜单动作
    private AbstractAction myNewAction = new AbstractAction("新建会话") {
        public void actionPerformed(final ActionEvent e) {
            mainTabbedPane.insertTab("新建选项卡", new FlatSVGIcon("com/g3g4x5x6/ui/icons/addToDictionary.svg"), new NewTabbedPane(), "新建选项卡", mainTabbedPane.getTabCount());
            mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
        }
    };

    private AbstractAction mySessionAction = new AbstractAction("会话管理") {
        public void actionPerformed(final ActionEvent e) {
            mainTabbedPane.insertTab("会话管理", new FlatSVGIcon("com/g3g4x5x6/ui/icons/addList.svg"), new SessionsManager(mainTabbedPane), "会话管理", mainTabbedPane.getTabCount());
            mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
        }
    };

    private AbstractAction myEditorAction = new AbstractAction("内置编辑器") {
        public void actionPerformed(final ActionEvent e) {
            // TODO 内置编辑器
            DialogUtil.info("敬请期待");
        }
    };

    private AbstractAction encodeConversionAction = new AbstractAction("文件编码转换") {
        @Override
        public void actionPerformed(ActionEvent e) {
//            EncodeConversion encodeConversion = new EncodeConversion();
            DialogUtil.info("敬请期待");
        }
    };

    private AbstractAction colorPickerAction = new AbstractAction("截图取色") {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("取色器");
            SwingUtilities.invokeLater(() -> {
                        ColorPicker colorPicker = new ColorPicker();
                        colorPicker.setVisible(true);
                    }
            );
        }
    };

    private AbstractAction qrCodePickerAction = new AbstractAction("二维码") {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("QR Code");
            QRTool qrCode = new QRTool();
            qrCode.setVisible(true);
        }
    };

    private AbstractAction tightVNCAction = new AbstractAction("TightVNC Viewer") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (MacUtils.isMac()) {
                // do mac os specific things
                try {
                    MacApplicationWrapper application = MacApplicationWrapper.getApplication();
                    application.setEnabledAboutMenu(false);
                    MacUtils.setName("TightVNC Viewer");
                    application.setDockIconImage(MacUtils.getIconImage());
                } catch (CommonException ex) {
                    log.warn(ex.getMessage());
                }
            }
            Parser parser = new Parser();
            ParametersHandler.completeParserOptions(parser);

            parser.parse(new String[]{});
            Viewer viewer = new Viewer(parser);
            SwingUtilities.invokeLater(viewer);
        }
    };

    private AbstractAction freeRDPAction = new AbstractAction("FreeRDP") {
        @Override
        public void actionPerformed(ActionEvent e) {
            FreeRdp freeRdp = new FreeRdp();
            freeRdp.setVisible(true);
        }
    };

    private AbstractAction themeAction = new AbstractAction("切换主题") {
        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO 主题切换
            ThemeDialog themeDialog = new ThemeDialog();
            themeDialog.setVisible(true);
        }
    };

    private AbstractAction settingsAction = new AbstractAction("全局配置") {
        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO 全局设置
            SettingsDialog settingsDialog = new SettingsDialog();
            settingsDialog.setVisible(true);
        }
    };

    private AbstractAction importSessionAction = new AbstractAction("导入会话") {
        @Override
        public void actionPerformed(ActionEvent e) {
            FileInputStream fis = null;
            try {
                // 创建一个默认的文件选取器
                JFileChooser fileChooser = new JFileChooser();
                // 设置默认显示的文件夹为当前文件夹
                fileChooser.setCurrentDirectory(new File(ConfigUtil.getWorkPath() + "/export"));
                // 设置文件选择的模式（只选文件、只选文件夹、文件和文件均可选）
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
                int result = fileChooser.showOpenDialog(App.mainFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    fis = new FileInputStream(file);
                    Workbook workbook = new HSSFWorkbook(fis);
                    Sheet sessionSheet = workbook.getSheet("session");
                    Sheet tagSheet = workbook.getSheet("tag");
                    Sheet relationSheet = workbook.getSheet("relation");
                    for (int i = 0; i <= sessionSheet.getLastRowNum(); i++) {
                        String[] rowStr = new String[12];
                        for (int j = 0; j < sessionSheet.getRow(i).getLastCellNum(); j++) {
                            rowStr[j] = sessionSheet.getRow(i).getCell(j).getStringCellValue();
                        }
                        // 更新 session 表
                        String sql_session = "INSERT INTO session VALUES (null , " +    // id, 自增
                                "'" + rowStr[0] + "', " +     // session name
                                "'" + rowStr[1] + "', " +    // protocol
                                "'" + rowStr[2] + "', " +        // host
                                "'" + rowStr[3] + "', " +        // port
                                "'" + rowStr[4] + "', " +        // auth
                                "'" + rowStr[5] + "', " +        // user
                                "'" + rowStr[6] + "', " +        // pass
                                "'" + rowStr[7] + "', " +  // private key
                                "'" + rowStr[8] + "', " + // create time
                                "'" + rowStr[9] + "', " + // access time
                                "'" + rowStr[10] + "', " + // modified time
                                "'" + rowStr[11] + "');";  // comment
                        log.debug("sql_session: " + sql_session);
                        ExcelUtil.importBackup(sql_session);
                    }
                    for (int i = 0; i <= tagSheet.getLastRowNum(); i++) {
                        String[] rowStr = new String[1];
                        for (int j = 0; j < tagSheet.getRow(i).getLastCellNum(); j++) {
                            rowStr[j] = tagSheet.getRow(i).getCell(j).getStringCellValue();
                        }
                        // 更新 session 表
                        String sql_tag = "INSERT INTO tag VALUES (null , " +    // id, 自增
                                "'" + rowStr[0] + "');";
                        log.debug("sql_tag: " + sql_tag);
                        if (!rowStr[0].strip().equals("会话标签"))
                            ExcelUtil.importBackup(sql_tag);
                    }
                    for (int i = 0; i <= relationSheet.getLastRowNum(); i++) {
                        String[] rowStr = new String[2];
                        for (int j = 0; j < relationSheet.getRow(i).getLastCellNum(); j++) {
                            rowStr[j] = relationSheet.getRow(i).getCell(j).getStringCellValue();
                        }
                        // 更新 session 表
                        String sql_relation = "INSERT INTO relation VALUES (null , " +    // id, 自增
                                "'" + rowStr[0] + "', " +
                                "'" + rowStr[1] + "');";
                        log.debug("sql_relation: " + sql_relation);
                        ExcelUtil.importBackup(sql_relation);
                    }
                }
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    };

    private AbstractAction exportSessionAction = new AbstractAction("导出会话") {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 导出文件
            String file = ConfigUtil.getWorkPath() + "/export/backup_" + String.valueOf(new Date().getTime()) + ".xls";
            // 1.创建workbook
            Workbook workbook = new HSSFWorkbook();
            // 2.根据workbook创建sheet
            Sheet sessionSheet = workbook.createSheet("session");
            Sheet tagSheet = workbook.createSheet("tag");
            Sheet relationSheet = workbook.createSheet("relation");
            // 3.写入数据到sheet
            ExcelUtil.exportSession(sessionSheet);
            ExcelUtil.exportTag(tagSheet);
            ExcelUtil.exportRelation(relationSheet);

            // 4.通过输出流写到文件里去
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                workbook.write(fos);
                fos.close();
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            DialogUtil.info("会话导出完成");
        }
    };

    private AbstractAction loadPluginAction = new AbstractAction("加载插件") {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("加载插件");
            DialogUtil.info("敬请期待");
        }
    };

    private AbstractAction managePluginAction = new AbstractAction("插件管理") {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("插件管理");
            DialogUtil.info("敬请期待");
        }
    };

    private AbstractAction apiPluginAction = new AbstractAction("插件规范") {
        @SneakyThrows
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("插件规范");
            Desktop.getDesktop().browse(new URL("https://github.com/G3G4X5X6/ultimateshell/wiki/Plugin-development-specification").toURI());
        }
    };

    private AbstractAction githubAction = new AbstractAction("GitHub") {
        @SneakyThrows
        @Override
        public void actionPerformed(ActionEvent e) {
            Desktop.getDesktop().browse(new URL("https://github.com/G3G4X5X6/ultimateshell").toURI());
        }
    };

    private AbstractAction gitpageAction = new AbstractAction("GitPage") {
        @SneakyThrows
        @Override
        public void actionPerformed(ActionEvent e) {
            Desktop.getDesktop().browse(new URL("https://g3g4x5x6.github.io/ultimateshell/").toURI());
        }
    };

    private AbstractAction myAboutAction = new AbstractAction("关于 UltimateShell") {
        public void actionPerformed(final ActionEvent e) {
            DialogUtil.msg("About",
                    "<html>UltimateShell " + Version.VERSION + " <br>" +
                            "Build on " + Version.BUILD_TIMESTAMP + "#" + Version.BUILD_NUMBER + "<br><br>" +
                            "Powered by <a href='https://github.com/G3G4X5X6'>G3G4X5X6</a><br>" +
                            "Email to <a href='mailto://g3g4x5x6@foxmail.com'>g3g4x5x6@foxmail.com</a></html>");
        }
    };
}

/**
 * this.setUndecorated(true);                      //去处边框
 * this.setLayout(null);
 * this.setExtendedState(JFrame.MAXIMIZED_BOTH);   //最大化
 * this.setAlwaysOnTop(true);                      //总在最前面
 * this.setResizable(false);                       //不能改变大小
 */
