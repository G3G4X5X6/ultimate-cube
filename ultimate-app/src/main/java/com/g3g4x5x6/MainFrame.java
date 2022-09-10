package com.g3g4x5x6;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatToggleButton;
import com.g3g4x5x6.console.ConsolePane;
import com.g3g4x5x6.dashboard.DashboardPane;
import com.g3g4x5x6.dialog.LockDialog;
import com.g3g4x5x6.editor.EditorFrame;
import com.g3g4x5x6.editor.EditorPanel;
import com.g3g4x5x6.focus.FocusFrame;
import com.g3g4x5x6.panels.ssh.panel.RandomPassword;
import com.g3g4x5x6.panels.ssh.panel.SessionManagerPanel;
import com.g3g4x5x6.settings.SettingsDialog;
import com.g3g4x5x6.ssh.SessionInfo;
import com.g3g4x5x6.ssh.panel.SshTabbedPane;
import com.g3g4x5x6.tools.ColorPicker;
import com.g3g4x5x6.tools.QRTool;
import com.g3g4x5x6.tools.external.ExternalToolIntegration;
import com.g3g4x5x6.tools.xpack.FreeRdp;
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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static com.formdev.flatlaf.FlatClientProperties.*;


/**
 * 主界面
 */
@Slf4j
public class MainFrame extends JFrame implements MouseListener {

    public static JTabbedPane mainTabbedPane;
    public static EditorFrame editorFrame = EditorFrame.getInstance();
    public static JProgressBar waitProgressBar;
    public static AtomicInteger waitCount = new AtomicInteger(0);
    public static int focusIndex = 0;

    private final ExternalToolIntegration integration = new ExternalToolIntegration();

    // TODO JFrame 组件定义
    private final JMenuBar menuBar = new JMenuBar();
    private final JMenu terminalMenu = new JMenu("终端");
    private final JMenu viewMenu = new JMenu("查看");
    private final JMenu optionMenu = new JMenu("选项");
    private final JMenu toolMenu = new JMenu("工具");
    private final JMenu pluginMenu = new JMenu("插件");
    private final JMenu helpMenu = new JMenu("帮助");
    private final JMenu externalSubMenu = new JMenu("外部集成工具");
    private final JPopupMenu popupMenu = new JPopupMenu();
    private final JPopupMenu trailPopupMenu = new JPopupMenu();

    private String latestVersion;

    public MainFrame() throws HeadlessException {
        // 主窗口设置
        this.setSize(new Dimension(1000, 600));
        this.setPreferredSize(new Dimension(1000, 600));
        this.setMinimumSize(new Dimension(950, 600));
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("icon.jpg"))).getImage());

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

        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                log.debug("关闭窗口，Windows");
                if (App.properties.getProperty("app.quit.to.tray").equalsIgnoreCase("false")) {
                    System.exit(0);
                } else {
                    setVisible(false);
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
                log.debug("最小化窗口，Windows");
                if (App.properties.getProperty("app.iconified.to.tray").equalsIgnoreCase("true")) {
                    setVisible(false);
                }
            }
        };
        this.addWindowListener(exitListener);
    }

    /**
     * 添加菜单动作
     */
    private void initMenuBar() {
        // 终端菜单
        JMenu openSessionMenu = new JMenu("打开会话");
        String rootPath = AppConfig.getWorkPath() + "/sessions/ssh/";
        File dir = new File(rootPath);
        try {
            initOpenSessionMenu(dir, openSessionMenu);
        } catch (IOException e) {
            e.printStackTrace();
        }
        terminalMenu.add(openSessionMenu);
        terminalMenu.add(myNewAction);
        terminalMenu.add(mySessionAction);
        terminalMenu.add(myLocalTerminal);

        // 查看菜单
        JMenuItem focusItem = new JMenuItem("专注模式");
        focusItem.setIcon(new FlatSVGIcon("icons/fitContent.svg"));
        focusItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                focusAction();
            }
        });
        viewMenu.add(focusItem);

        // 选项菜单
        JMenuItem settingsItem = new JMenuItem("全局配置");
        settingsItem.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.ALT_DOWN_MASK));
        settingsItem.addActionListener(settingsAction);

        JMenuItem editSettingsItem = new JMenuItem("编辑配置文件");
        optionMenu.add(settingsItem);
        optionMenu.addSeparator();
        optionMenu.add(importSessionAction);
        optionMenu.add(exportSessionAction);

        // 帮助菜单 chromium.svg
        JMenuItem github = new JMenuItem("GitHub");
        github.addActionListener(githubAction);
        github.setIcon(new FlatSVGIcon("icons/BrowserSystemDefault(GrayDark).svg"));

        JMenuItem gitPage = new JMenuItem("GitPage");
        gitPage.addActionListener(gitPageAction);
        gitPage.setIcon(new FlatSVGIcon("icons/BrowserSystemDefault(GrayDark).svg"));

        // pluginIcon.svg
        JMenuItem openSpace = new JMenuItem("打开工作空间");
        openSpace.addActionListener(openWorkspace);
        openSpace.setIcon(new FlatSVGIcon("icons/pluginIcon.svg"));

        // relevantProposal.svg
        JMenuItem aboutMe = new JMenuItem("关于 UltimateShell");
        aboutMe.addActionListener(myAboutAction);
        aboutMe.setIcon(new FlatSVGIcon("icons/relevantProposal.svg"));

        helpMenu.add(github);
        helpMenu.add(gitPage);
        helpMenu.addSeparator();
        helpMenu.add(openSpace);
        helpMenu.addSeparator();
        helpMenu.add(aboutMe);

        // 工具菜单
        JMenuItem editorItem = new JMenuItem("简易编辑器");
        editorItem.setIcon(new FlatSVGIcon("icons/editScheme.svg"));
        editorItem.addActionListener(myEditorAction);

        JMenuItem randomPassItem = new JMenuItem("随机密码生成器");
        randomPassItem.setIcon(new FlatSVGIcon("icons/colGreyKey.svg"));
        randomPassItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRandomPasswordDialog();
            }
        });

        toolMenu.add(editorItem);
        toolMenu.add(randomPassItem);
        toolMenu.addSeparator();
        toolMenu.add(tightVNCAction);
        // 快捷键
        JMenuItem freeRdpItem = new JMenuItem("FreeRDP");
        freeRdpItem.setAccelerator(KeyStroke.getKeyStroke('D', InputEvent.ALT_DOWN_MASK));
        freeRdpItem.addActionListener(freeRDPAction);
        toolMenu.add(freeRdpItem);
        toolMenu.addSeparator();

        JMenu otherToolMenu = new JMenu("杂七杂八");
        otherToolMenu.add(encodeConversionAction);
        otherToolMenu.add(colorPickerAction);
        otherToolMenu.add(qrCodePickerAction);
        toolMenu.add(otherToolMenu);
        toolMenu.addSeparator();
        toolMenu.add(externalSubMenu);

        // 插件菜单
        pluginMenu.add(loadPluginAction);
        pluginMenu.add(managePluginAction);
        pluginMenu.addSeparator();
        pluginMenu.add(apiPluginAction);

        // 外部集成工具
        externalSubMenu.add(new AbstractAction("工具管理") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("工具管理");
                if (editorFrame == null) {
                    editorFrame = EditorFrame.getInstance();
                }
                editorFrame.setTitle(App.properties.getProperty("editor.title"));
                editorFrame.addAndSelectPanel(new EditorPanel(ExternalToolIntegration.settings_path));
                editorFrame.setVisible(true);
            }
        });
        externalSubMenu.addSeparator();
        integration.initExternalToolsMenu(externalSubMenu);

        menuBar.add(terminalMenu);
        menuBar.add(viewMenu);
        menuBar.add(optionMenu);
        menuBar.add(toolMenu);
        menuBar.add(pluginMenu);
        menuBar.add(helpMenu);
        this.setJMenuBar(menuBar);
    }

    private void initFuncIconButton() {
        // add "Users" button to menubar
        FlatButton usersButton = new FlatButton();
        usersButton.setIcon(new FlatSVGIcon("icons/users.svg"));
        usersButton.setButtonType(FlatButton.ButtonType.toolBarButton);
        usersButton.setFocusable(false);
        usersButton.addActionListener(e -> JOptionPane.showMessageDialog(MainFrame.this, "Hello User! How are you?", "User", JOptionPane.INFORMATION_MESSAGE));

        FlatButton lockBtn = new FlatButton();
        lockBtn.setIcon(new FlatSVGIcon("icons/lock.svg"));
        lockBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        lockBtn.setFocusable(false);
        lockBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Lock App!");
                LockDialog lockDialog = new LockDialog();
                lockDialog.setVisible(true);
            }
        });

        // 置顶图标按钮
        FlatToggleButton toggleButton = new FlatToggleButton();
        toggleButton.setIcon(new FlatSVGIcon("icons/pinTab.svg"));
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

        FlatButton updateBtn = new FlatButton();
        updateBtn.setIcon(new FlatSVGIcon("icons/ideUpdate.svg"));
        updateBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        updateBtn.setFocusable(false);

        FlatButton closeBtn = new FlatButton();
        closeBtn.setIcon(new FlatSVGIcon("icons/popFrame.svg"));
        closeBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        closeBtn.setFocusable(false);
        closeBtn.addActionListener(e -> {
            int i = JOptionPane.showConfirmDialog(App.mainFrame, "是否确认退出程序？", "退出", JOptionPane.OK_CANCEL_OPTION);
            if (i == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        // 添加更新按钮
        new Thread(() -> {
            latestVersion = CommonUtil.getLastestVersion();
            String currentVersion = "v" + Version.VERSION;
            if (!currentVersion.equals(latestVersion)) {
                menuBar.add(updateBtn);
                log.debug("添加更新按钮");
                updateBtn.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // 检查更新
                        String msg = "<html>当前版本：  <font color='red'>" + currentVersion + "</font<br>" +
                                "最新版本： <font color='green'>" + latestVersion + "</font><br><br>" +
                                "是否现在下载最新版本？</html>";
//                        log.debug("Msg: " + msg);
                        int code = JOptionPane.showConfirmDialog(App.mainFrame, msg, "更新", JOptionPane.YES_NO_OPTION);
                        if (code == 0) {
                            // TODO 更新
//                            log.debug("马上下载更新");
                            CommonUtil.getLatestJar();
                        } else {
                            // TODO 暂不更新
                            log.debug("暂不下载更新");
                        }
                    }
                });
            }
        }).start();

        menuBar.add(Box.createGlue());
        menuBar.add(usersButton);
        menuBar.add(lockBtn);
        menuBar.add(toggleButton);
        menuBar.add(closeBtn);
    }

    private void initToolBar() {
        // 暂不实现
    }

    private void initMainTabbedPane() {
        // 选项卡面板UI设置
        UIManager.put("TabbedPane.tabInsets", new Insets(0, 10, 0, 10));

        // 添加主选项卡面板
        mainTabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        mainTabbedPane.setEnabled(true);
        mainTabbedPane.addMouseListener(this);
        initClosableTabs(mainTabbedPane);
        initTrailPopupMenu();
        customComponents();     // 定制 ”选项卡面板“ 功能组件按钮
        initTabPopupMenu();     // 定制 ”选项卡面板“ 标签右键功能

        // 添加 ”仪表盘“ 面板
        mainTabbedPane.addTab("仪表板",
                new FlatSVGIcon("icons/homeFolder.svg"),
                new DashboardPane());

        this.getContentPane().add(mainTabbedPane);
    }

    private void initTrailPopupMenu() {
        JMenuItem item = new JMenuItem("生成随机密码");
        item.setIcon(new FlatSVGIcon("icons/colGreyKey.svg"));
        item.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRandomPasswordDialog();
            }
        });
        trailPopupMenu.add(item);
    }

    private void customComponents() {
        JToolBar leading;
        JToolBar trailing;
        leading = new JToolBar();
        leading.setFloatable(false);
        leading.setBorder(null);
        trailing = new JToolBar();
        trailing.setFloatable(false);
        trailing.setBorder(null);

        JButton dashboardBtn = new JButton(new FlatSVGIcon("icons/homeFolder.svg"));
        dashboardBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainTabbedPane.setSelectedIndex(0);
            }
        });

        JButton addBtn = new JButton(new FlatSVGIcon("icons/add.svg"));
        addBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainTabbedPane.insertTab("新建选项卡", new FlatSVGIcon("icons/addToDictionary.svg"), new NewTabbedPane(mainTabbedPane), "新建选项卡", mainTabbedPane.getTabCount());
                mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
            }
        });
        // swiftPackage.svg
        JButton sessionManagerBtn = new JButton(new FlatSVGIcon("icons/swiftPackage.svg"));
        sessionManagerBtn.setToolTipText("会话管理面板");
        sessionManagerBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainTabbedPane.insertTab("会话管理", new FlatSVGIcon("icons/addList.svg"), new SessionManagerPanel(mainTabbedPane), "会话管理", mainTabbedPane.getTabCount());
                mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
            }
        });

        JButton fullScreenBtn = new JButton(new FlatSVGIcon("icons/fitContent.svg"));
        fullScreenBtn.setToolTipText("专注模式");
        fullScreenBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                focusAction();
            }
        });

        // TODO 选项卡面板前置工具栏，暂不使用
        leading.add(dashboardBtn);

        JButton editorBtn = new JButton(new FlatSVGIcon("icons/editScheme.svg"));
        editorBtn.addActionListener(myEditorAction);

        waitProgressBar = new JProgressBar();
        waitProgressBar.setIndeterminate(true);
        waitProgressBar.setVisible(false);
        waitProgressBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 为了异常情况下隐藏等待进度条
                if (e.getClickCount() == 2) {
                    int count = waitCount.decrementAndGet();
                    if (count <= 0) {
                        waitProgressBar.setVisible(false);
                    }
                }
            }
        });

        trailing.add(addBtn);
        trailing.add(sessionManagerBtn);
        trailing.add(Box.createHorizontalGlue());
        trailing.add(waitProgressBar);
        trailing.add(Box.createHorizontalGlue());
        trailing.add(editorBtn);
        trailing.add(fullScreenBtn);
        mainTabbedPane.putClientProperty(TABBED_PANE_TRAILING_COMPONENT, trailing);
    }

    private void focusAction() {
        log.debug("专注模式");
        setFocusIndex();
        FocusFrame focusFrame = new FocusFrame();
        focusFrame.setVisible(true);
    }

    private void setFocusIndex() {
        int selectIndex = mainTabbedPane.getSelectedIndex();
        for (int i = 0; i < selectIndex; i++) {
            if (mainTabbedPane.getComponentAt(i) instanceof SshTabbedPane) {
                focusIndex += 1;
            }
        }
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
                                new Thread(() -> {
                                    SessionInfo sessionInfo = SessionUtil.openSshSession(f.getAbsolutePath());
                                    if (SshUtil.testConnection(sessionInfo.getSessionAddress(), sessionInfo.getSessionPort()) == 1) {
                                        String defaultTitle = sessionInfo.getSessionName().equals("") ? "未命名" : sessionInfo.getSessionName();
                                        MainFrame.mainTabbedPane.addTab(defaultTitle, new FlatSVGIcon("icons/OpenTerminal_13x13.svg"),
                                                new SshTabbedPane(sessionInfo)
                                        );
                                        MainFrame.mainTabbedPane.setSelectedIndex(MainFrame.mainTabbedPane.getTabCount() - 1);
                                    }
                                    App.sessionInfos.put(sessionInfo.getSessionId(), sessionInfo);
                                }).start();
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
                        if (mainTabbedPane.getComponentAt(tabIndex) instanceof SshTabbedPane) {
                            SshTabbedPane sshTabbedPane = (SshTabbedPane) mainTabbedPane.getComponentAt(tabIndex);
                            sshTabbedPane.getSessionInfo().close();
                            App.sessionInfos.remove(sshTabbedPane.getSessionInfo().getSessionId());
                            log.debug("Close: " + App.sessionInfos.size());
                        }
                        mainTabbedPane.removeTabAt(tabIndex);
                    }
                });
    }

    private void renameTabTitle() {
        String input = JOptionPane.showInputDialog(App.mainFrame, "重命名 Tab 标题",
                mainTabbedPane.getTitleAt(mainTabbedPane.getSelectedIndex()));
        if (input != null && !input.strip().equalsIgnoreCase("")) {
            JLabel newTabTitle = new JLabel(input);
            newTabTitle.setIcon(new FlatSVGIcon("icons/OpenTerminal_13x13.svg"));
            mainTabbedPane.setTabComponentAt(mainTabbedPane.getSelectedIndex(), newTabTitle);
        }
    }

    private void initTabPopupMenu() {
        AbstractAction renameCurrentTabAction = new AbstractAction("命名标签") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 判断是否为 SshTabbedPane 实例
                if (mainTabbedPane.getComponentAt(mainTabbedPane.getSelectedIndex()) instanceof SshTabbedPane) {
                    renameTabTitle();
                }
            }
        };
        AbstractAction copyCurrentTabAction = new AbstractAction("复制会话") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(()->{
                    // 等待进度条
                    MainFrame.addWaitProgressBar();

                    SshTabbedPane selectedTabbedPane = (SshTabbedPane) mainTabbedPane.getSelectedComponent();
                    mainTabbedPane.addTab(
                            "复制-" + mainTabbedPane.getTitleAt(mainTabbedPane.getSelectedIndex()),
                            new FlatSVGIcon("icons/OpenTerminal_13x13.svg"),
                            new SshTabbedPane(selectedTabbedPane.getSessionInfo().copy())
                    );
                    mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);

                    // 移除等待进度条
                    MainFrame.removeWaitProgressBar();
                }).start();
            }
        };
        AbstractAction reconnectAction = new AbstractAction("<html><font style='color:green'>重新连接</font></html>") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("重新连接");
                try {
                    SshTabbedPane selectedTabbedPane = (SshTabbedPane) mainTabbedPane.getSelectedComponent();
                    selectedTabbedPane.resetSession();
                } catch (ClassCastException classCastException) {
                    if (classCastException.getMessage().contains("be cast to class")) {
                        log.debug("不是远程会话面板无法刷新重连");
                    }
                }
            }
        };
        AbstractAction closeCurrentTabAction = new AbstractAction("关闭当前") {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainTabbedPane.removeTabAt(mainTabbedPane.getSelectedIndex());
            }
        };
        AbstractAction closeLeftAction = new AbstractAction("关闭左边") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currentIndex = mainTabbedPane.getSelectedIndex();
                for (int i = currentIndex - 1; i > 0; i--) {
                    SshTabbedPane tmp = (SshTabbedPane) mainTabbedPane.getComponentAt(i);
                    if (tmp != null) {
                        mainTabbedPane.removeTabAt(i);
                    }
                }
            }
        };
        AbstractAction closeRightAction = new AbstractAction("关闭右边") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currentIndex = mainTabbedPane.getSelectedIndex();
                for (int i = currentIndex + 1; i < mainTabbedPane.getTabCount(); i++) {
                    SshTabbedPane tmp = (SshTabbedPane) mainTabbedPane.getComponentAt(i);
                    if (tmp != null) {
                        mainTabbedPane.removeTabAt(i);
                    }
                }
            }
        };
        AbstractAction closeAllTabAction = new AbstractAction("关闭所有") {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = mainTabbedPane.getTabCount() - 1; i > 0; i--) {
                    SshTabbedPane tmp = (SshTabbedPane) mainTabbedPane.getComponentAt(i);
                    if (tmp != null) {
                        mainTabbedPane.removeTabAt(i);
                    }
                }
            }
        };

        popupMenu.add(renameCurrentTabAction);
        popupMenu.add(copyCurrentTabAction);
        popupMenu.add(reconnectAction);
        popupMenu.addSeparator();
        popupMenu.add(closeCurrentTabAction);
        popupMenu.add(closeLeftAction);
        popupMenu.add(closeRightAction);
        popupMenu.add(closeAllTabAction);
    }

    /**
     * 内部监听器
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            // 判断是否为 SshTabbedPane 实例
            if (mainTabbedPane.getComponentAt(mainTabbedPane.getSelectedIndex()) instanceof SshTabbedPane) {
                renameTabTitle();
            }
        }
        if (e.getButton() == 3) {
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

    public static void addWaitProgressBar() {
        waitCount.incrementAndGet();
        waitProgressBar.setVisible(true);
    }

    public static void removeWaitProgressBar() {
        int count;
        if (waitCount.get() > 0) {
            count = waitCount.decrementAndGet();
            if (count == 0) {
                waitProgressBar.setVisible(false);
            }
        }
    }


    private void showRandomPasswordDialog(){
        JDialog dialog = new JDialog(MainFrame.this);
        dialog.setTitle("随机密码生成器");
        dialog.setSize(new Dimension(450, 145));
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(MainFrame.this);
        dialog.setContentPane(new RandomPassword());
        dialog.setVisible(true);
    }

    // TODO 菜单动作
    private final AbstractAction myNewAction = new AbstractAction("新建会话") {
        public void actionPerformed(final ActionEvent e) {
            mainTabbedPane.insertTab("新建选项卡", new FlatSVGIcon("icons/addToDictionary.svg"), new NewTabbedPane(mainTabbedPane), "新建选项卡", mainTabbedPane.getTabCount());
            mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
        }
    };

    private final AbstractAction mySessionAction = new AbstractAction("会话管理") {
        public void actionPerformed(final ActionEvent e) {
            mainTabbedPane.insertTab("会话管理", new FlatSVGIcon("icons/addList.svg"), new SessionManagerPanel(mainTabbedPane), "会话管理", mainTabbedPane.getTabCount());
            mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
        }
    };

    private final AbstractAction myLocalTerminal = new AbstractAction("本地终端") {
        @Override
        public void actionPerformed(ActionEvent e) {
            mainTabbedPane.insertTab("<html><font style='color:green'><strong>本地终端</strong></font></html>",
                    new FlatSVGIcon("icons/console.svg"),
                    new ConsolePane(),
                    "本地终端",
                    mainTabbedPane.getTabCount());
            mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
        }
    };

    private final AbstractAction myEditorAction = new AbstractAction("简易编辑器") {
        public void actionPerformed(final ActionEvent e) {
            new Thread(() -> {
                // TODO 内置编辑器
                if (editorFrame == null) {
                    editorFrame = new EditorFrame();
                }
                if (editorFrame.getTabbedPane().getTabCount() == 0) {
                    EditorPanel editorPanel = new EditorPanel();
                    editorFrame.getTabbedPane().addTab(editorPanel.getTitle(), editorPanel.getIcon(), editorPanel, editorPanel.getTips());
                    editorFrame.getTabbedPane().setSelectedIndex(editorFrame.getTabbedPane().getTabCount() - 1);
                }
                editorFrame.setTitle(App.properties.getProperty("editor.title"));
                editorFrame.setVisible(true);
            }).start();
        }
    };

    private final AbstractAction encodeConversionAction = new AbstractAction("文件编码转换") {
        @Override
        public void actionPerformed(ActionEvent e) {
            DialogUtil.info("敬请期待");
        }
    };

    private final AbstractAction colorPickerAction = new AbstractAction("截图取色") {
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

    private final AbstractAction qrCodePickerAction = new AbstractAction("二维码") {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("QR Code");
            QRTool qrCode = new QRTool();
            qrCode.setVisible(true);
        }
    };

    private final AbstractAction tightVNCAction = new AbstractAction("TightVNC Viewer") {
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

    private final AbstractAction freeRDPAction = new AbstractAction("FreeRDP") {
        @Override
        public void actionPerformed(ActionEvent e) {
            FreeRdp freeRdp = new FreeRdp();
            freeRdp.setVisible(true);
        }
    };

    private final AbstractAction settingsAction = new AbstractAction("全局配置") {
        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO 全局设置
            SettingsDialog settingsDialog = new SettingsDialog();
            settingsDialog.setVisible(true);
        }
    };

    private final AbstractAction importSessionAction = new AbstractAction("导入会话") {
        @Override
        public void actionPerformed(ActionEvent e) {
            FileInputStream fis;
            try {
                // 创建一个默认的文件选取器
                JFileChooser fileChooser = new JFileChooser();
                // 设置默认显示的文件夹为当前文件夹
                fileChooser.setCurrentDirectory(new File(AppConfig.getWorkPath() + "/export"));
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
                        // TODO
//                        ExcelUtil.importBackup(sql_session);
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
                        if (!rowStr[0].strip().equals("会话标签")) {
                            // TODO
//                            ExcelUtil.importBackup(sql_tag);
                        }
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
                        // TODO
//                        ExcelUtil.importBackup(sql_relation);
                    }
                }
            } catch (IOException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }
    };

    private final AbstractAction exportSessionAction = new AbstractAction("导出会话") {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 导出文件
            String file = AppConfig.getWorkPath() + "/export/backup_" + String.valueOf(new Date().getTime()) + ".xls";
            // 1.创建workbook
            Workbook workbook = new HSSFWorkbook();
            // 2.根据workbook创建sheet
            Sheet sessionSheet = workbook.createSheet("session");
            Sheet tagSheet = workbook.createSheet("tag");
            Sheet relationSheet = workbook.createSheet("relation");
            // TODO 3.写入数据到sheet
//            ExcelUtil.exportSession(sessionSheet);
//            ExcelUtil.exportTag(tagSheet);
//            ExcelUtil.exportRelation(relationSheet);

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

    private AbstractAction gitPageAction = new AbstractAction("GitPage") {
        @SneakyThrows
        @Override
        public void actionPerformed(ActionEvent e) {
            Desktop.getDesktop().browse(new URL("https://g3g4x5x6.github.io/ultimateshell/").toURI());
        }
    };

    private AbstractAction openWorkspace = new AbstractAction("打开工作空间") {
        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(() -> {
                try {
                    Desktop.getDesktop().open(new File(AppConfig.getWorkPath()));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }).start();
        }
    };

    private final AbstractAction myAboutAction = new AbstractAction("关于 UltimateShell") {
        public void actionPerformed(final ActionEvent e) {
            JOptionPane.showMessageDialog(MainFrame.this,
                    "<html>ultimate-cube v" + Version.VERSION + " <br>" +
                            "Build on " + Version.BUILD_TIMESTAMP + "#" + Version.BUILD_NUMBER + "<br><br>" +
                            "Powered by <a href='https://github.com/G3G4X5X6'>G3G4X5X6</a><br>" +
                            "Email to <a href='mailto://g3g4x5x6@foxmail.com'>g3g4x5x6@foxmail.com</a></html>",
                    "About", JOptionPane.INFORMATION_MESSAGE);
        }
    };
}

