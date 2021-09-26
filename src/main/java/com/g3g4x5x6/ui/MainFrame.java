package com.g3g4x5x6.ui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatToggleButton;
import com.g3g4x5x6.App;
import com.g3g4x5x6.ui.dialog.AboutDialog;
import com.g3g4x5x6.ui.settings.SettingsDialog;
import com.g3g4x5x6.ui.dialog.ThemeDialog;
import com.g3g4x5x6.ui.panels.dashboard.quickstarter.SessionsManager;
import com.g3g4x5x6.ui.panels.dashboard.DashboardPane;
import com.g3g4x5x6.ui.panels.session.NewTabbedPane;
import com.g3g4x5x6.ui.panels.tools.FreeRdp;
import com.g3g4x5x6.utils.CommonUtil;
import com.g3g4x5x6.utils.ConfigUtil;
import com.g3g4x5x6.utils.DialogUtil;
import com.g3g4x5x6.utils.ExcelUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.function.BiConsumer;

import static com.formdev.flatlaf.FlatClientProperties.*;


/**
 * 主界面
 */
@Slf4j
public class MainFrame extends JFrame {

    private int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    private int height = Toolkit.getDefaultToolkit().getScreenSize().height;

    private String lastestVersion;

    // TODO JFrame 组件定义
    private JMenuBar menuBar = new JMenuBar();
    private JMenu terminalMenu = new JMenu("终端");
    private JMenu viewMenu = new JMenu("查看");
    private JMenu optionMenu = new JMenu("选项");
    private JMenu toolMenu = new JMenu("工具");
    private JMenu pluginMenu = new JMenu("插件");
    private JMenu helpMenu = new JMenu("帮助");

    private JPanel statusBar;

    // TODO 菜单弹出面板
    private AboutDialog about = new AboutDialog();

    private JTabbedPane mainTabbedPane;

    private Integer count = 1;

    // TODO 菜单动作
    private AbstractAction myOpenAction = new AbstractAction("新建会话") {
        public void actionPerformed(final ActionEvent e) {
            mainTabbedPane.insertTab("新建选项卡", new FlatSVGIcon("com/g3g4x5x6/ui/icons/addToDictionary.svg"), new NewTabbedPane(mainTabbedPane), "新建选项卡", mainTabbedPane.getTabCount());
            mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount()-1);
        }
    };

    private AbstractAction mysessionAction = new AbstractAction("会话管理") {
        public void actionPerformed(final ActionEvent e) {
            mainTabbedPane.insertTab("会话管理", new FlatSVGIcon("com/g3g4x5x6/ui/icons/addList.svg"), new SessionsManager(mainTabbedPane), "会话管理", mainTabbedPane.getTabCount());
            mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
        }
    };

    private AbstractAction dashboardAction = new AbstractAction("仪表板") {
        @Override
        public void actionPerformed(ActionEvent e) {
            mainTabbedPane.setSelectedIndex(0);
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

    private AbstractAction tightVNCAction = new AbstractAction("TightVNC Viewer") {
        @Override
        public void actionPerformed(ActionEvent e) {
            DialogUtil.info("敬请期待");
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

    private AbstractAction installPluginAction = new AbstractAction("加载插件") {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("加载插件");
        }
    };

    private AbstractAction uninstallPluginAction = new AbstractAction("卸载插件") {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("卸载插件");
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
                    "<html>UltimateShell " + CommonUtil.getCurrentVersion() + " <br>" +
                            "Build on " + CommonUtil.getBuildOn() + "<br><br>" +
                            "Powered by <a href='https://github.com/G3G4X5X6'>G3G4X5X6</a></html>");
        }
    };

    private void init() {
        // TODO 提示确认退出
        this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);

        this.setSize(new Dimension(1000, 700));
        this.setMinimumSize(new Dimension(900, 600));
        this.setPreferredSize(new Dimension(1000, 600));
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon(this.getClass().getClassLoader().getResource("icon.png")).getImage());

        UIManager.put("TabbedPane.tabInsets", new Insets(0, 10, 0, 10));
        mainTabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        mainTabbedPane.setEnabled(true);
        initClosableTabs(mainTabbedPane);
        customComponents();

        // TODO JTabbedPane's PopupMenu
        JPopupMenu tabPopupMenu = new JPopupMenu();
        AbstractAction renameAction = new AbstractAction("重命名") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("重命名Tab标题");
            }
        };
        mainTabbedPane.setComponentPopupMenu(tabPopupMenu);

        // TODO 主面板
        this.add(mainTabbedPane);

        // 添加主仪表盘
        mainTabbedPane.addTab("仪表板",
                new FlatSVGIcon("com/g3g4x5x6/ui/icons/homeFolder.svg"),
                new DashboardPane(mainTabbedPane));

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

        // TODO 添加菜单动作
        terminalMenu.add(myOpenAction);
        terminalMenu.add(mysessionAction);
        //
        viewMenu.add(dashboardAction);
        //
        optionMenu.add(themeAction);
        optionMenu.add(settingsAction);
        optionMenu.addSeparator();
        optionMenu.add(importSessionAction);
        optionMenu.add(exportSessionAction);
        //
        helpMenu.add(githubAction);
        helpMenu.add(gitpageAction);
        helpMenu.addSeparator();
        helpMenu.add(myAboutAction);
        //
        toolMenu.add(myEditorAction);
        toolMenu.add(encodeConversionAction);
        toolMenu.addSeparator();
        toolMenu.add(tightVNCAction);
        toolMenu.add(freeRDPAction);
        //
        pluginMenu.add(installPluginAction);
        pluginMenu.add(uninstallPluginAction);

        // TODO 菜单栏
        this.setJMenuBar(menuBar);
        menuBar.add(terminalMenu);
        menuBar.add(viewMenu);
        menuBar.add(optionMenu);
        menuBar.add(toolMenu);
        menuBar.add(pluginMenu);
        menuBar.add(helpMenu);
        menuBar.add(Box.createGlue());
        menuBar.add(usersButton);
        menuBar.add(toggleButton);

        // 添加更新按钮
        new Thread(new Runnable() {
            @Override
            public void run() {
                lastestVersion = CommonUtil.getLastestVersion();
                String currentVersion = CommonUtil.getCurrentVersion();
                if (!CommonUtil.getCurrentVersion().equals(lastestVersion)) {
                    menuBar.add(updateBtn);
                    log.debug("添加更新按钮");
                    updateBtn.addActionListener(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // 检查更新
                            String msg = null;
                            msg = "<html>当前版本：  <font color='red'>" + currentVersion + "</font<br>" +
                                    "最新版本： <font color='green'>" + lastestVersion + "</font><br><br>" +
                                    "是否现在下载更新？</html>";
                            log.debug("Msg: " + msg);
                            int code = JOptionPane.showConfirmDialog(App.mainFrame, msg, "更新", JOptionPane.YES_NO_OPTION);
                            if (code == 0) {
                                // TODO 更新
                                log.debug("马上下载更新");
                            } else {
                                // TODO 暂不更新
                                log.debug("暂不下载更新");
                            }
                        }
                    });
                }
            }
        }).start();


        // TODO 工具栏

        // TODO 状态栏
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        statusBar = new JPanel();
        statusBar.setLayout(flowLayout);
        statusBar.add(new JLabel("状态栏"));
//        this.add(statusBar, BorderLayout.SOUTH);
    }

    private void initClosableTabs(JTabbedPane tabbedPane) {
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSABLE, true);
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_CALLBACK,
                (BiConsumer<JTabbedPane, Integer>) (tabPane, tabIndex) -> {
                    if (tabIndex != 0){
                        mainTabbedPane.removeTabAt(tabIndex);
                    }
                });

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
                mainTabbedPane.insertTab("新建选项卡", new FlatSVGIcon("com/g3g4x5x6/ui/icons/addToDictionary.svg"), new NewTabbedPane(mainTabbedPane), "新建选项卡", mainTabbedPane.getTabCount());
                mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount()-1);
            }
        });

        leading.add(dashboardBtn);
        trailing.add(addBtn);
        trailing.add(Box.createHorizontalGlue());
        trailing.add(new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/commit.svg")));
        trailing.add(new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/diff.svg")));
        trailing.add(new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/listFiles.svg")));
//        mainTabbedPane.putClientProperty(TABBED_PANE_LEADING_COMPONENT, leading);
        mainTabbedPane.putClientProperty(TABBED_PANE_TRAILING_COMPONENT, trailing);
    }


    /**
     * this.setUndecorated(true);                      //去处边框
     * this.setLayout(null);
     * this.setExtendedState(JFrame.MAXIMIZED_BOTH);   //最大化
     * this.setAlwaysOnTop(true);                      //总在最前面
     * this.setResizable(false);                       //不能改变大小
     */
    public MainFrame() throws HeadlessException {

        // UtimateShell 工作目录
        createDir(getWorkDir());

        // 界面初始化
        init();

    }

    private void createDir(String path) {
        File file = new File(path);
        if (!file.exists() && !file.isDirectory()) {
            log.debug("工作目录:" + " 不存在，创建目录：" + path);
            file.mkdir();
        } else {
            log.debug("工作目录: " + path + " 已存在");
        }
    }

    public static String getUserHome() {
        return System.getProperty("user.home");
    }

    public static String getWorkDir() {
        return getUserHome() + "/.ultimateshell";
    }
}
