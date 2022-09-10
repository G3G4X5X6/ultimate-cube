package com.g3g4x5x6.nuclei;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatToggleButton;
import com.g3g4x5x6.nuclei.model.GlobalConfigModel;
import com.g3g4x5x6.nuclei.panel.EditTemplatePanel;
import com.g3g4x5x6.nuclei.panel.RunningPanel;
import com.g3g4x5x6.nuclei.panel.SettingsPanel;
import com.g3g4x5x6.nuclei.panel.TemplatesPanel;
import com.g3g4x5x6.nuclei.panel.connector.ConsolePanel;
import com.g3g4x5x6.nuclei.ultils.NucleiConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.util.OsUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.BiConsumer;

import static com.formdev.flatlaf.FlatClientProperties.*;

@Slf4j
public class NucleiFrame extends JFrame {
    public static NucleiFrame nucleiFrame = new NucleiFrame();
    public static JTabbedPane frameTabbedPane;

    public static String reportDir = NucleiConfig.getProperty("nuclei.report.path");
    public static String templatesDir = NucleiConfig.getProperty("nuclei.templates.path");
    public static GlobalConfigModel globalConfigModel = new GlobalConfigModel();

    // look to the master,follow the master,walk with the master,see through the master,become the master.
    // 寻找大师，追随大师，与师偕行，领悟大师，成为大师
    private final JLabel mottoLabel = new JLabel("追随大师，成为大师，超越大师");

    private final TemplatesPanel templatesPanel = new TemplatesPanel();
    private final SettingsPanel settingsPanel = new SettingsPanel();
    private final RunningPanel runningPanel = new RunningPanel();

    private final JMenu fileMenu = new JMenu("开始");
    private final JMenu editMenu = new JMenu("编辑");
    private final JMenu searchMenu = new JMenu("搜索");
    private final JMenu viewMenu = new JMenu("视图");
    private final JMenu encodeMenu = new JMenu("编码");
    private final JMenu langMenu = new JMenu("语言");
    private final JMenu settingsMenu = new JMenu("设置");
    private final JMenu macroMenu = new JMenu("宏");
    private final JMenu runMenu = new JMenu("运行");
    private final JMenu pluginMenu = new JMenu("插件");
    private final JMenu winMenu = new JMenu("窗口");
    private final JMenu aboutMenu = new JMenu("关于");

    private final JPopupMenu trailPopupMenu = new JPopupMenu();

    public NucleiFrame() {
        this.setLayout(new BorderLayout());
        this.setTitle("PoC-概念验证框架");
        this.setSize(new Dimension(1200, 700));
        this.setPreferredSize(new Dimension(1200, 700));
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("icon.jpg"))).getImage());

        initMenuBar();

        initToolBar();

        initTabbedPane();
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(searchMenu);
        menuBar.add(viewMenu);
        menuBar.add(encodeMenu);
        menuBar.add(langMenu);
        menuBar.add(settingsMenu);
        menuBar.add(macroMenu);
        menuBar.add(runMenu);
        menuBar.add(pluginMenu);
        menuBar.add(winMenu);
        menuBar.add(aboutMenu);

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
        menuBar.add(Box.createGlue());
        menuBar.add(toggleButton);

        // 初始化一级菜单
        initMenu();

        this.setJMenuBar(menuBar);
    }

    private void initMenu() {
        // pluginIcon.svg
        JMenuItem openSpace = new JMenuItem("打开工作空间");
        openSpace.setIcon(new FlatSVGIcon("icons/pluginIcon.svg"));
        openSpace.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    try {
                        Desktop.getDesktop().open(new File(NucleiConfig.getProperty("nuclei.bin.path")));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }).start();
            }
        });
        fileMenu.add(openSpace);
    }
    private void initToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton executeBtn = new JButton(new FlatSVGIcon("icons/execute.svg"));
        executeBtn.setToolTipText("默认新建终端运行（右键可选择已有终端运行）");
        executeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                log.debug("GlobalConfigModel: \n" + globalConfigModel.toString());

                if (e.getButton() == 3){
                    JPopupMenu popupMenu = new JPopupMenu();

                    LinkedHashMap<String, ConsolePanel> consolePanels = runningPanel.getConsolePanels();
                    for (String title : consolePanels.keySet()) {
                        JMenuItem tempItem = new JMenuItem(title);
                        tempItem.addActionListener(new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                log.debug("Execute in " + title);
                                // runTemplatesInSelectedConsole(consolePanels.get(title));

                                // 跳转至运行终端
                                NucleiFrame.frameTabbedPane.setSelectedIndex(2);
                                RunningPanel.tabbedPane.setSelectedComponent(consolePanels.get(title));
                            }
                        });
                        popupMenu.add(tempItem);
                    }
                    popupMenu.show(executeBtn, e.getX(), e.getY());
                }else {

                    // 创建终端执行任务
                    ConsolePanel consolePanel = runningPanel.createConsole();

                    // TODO 组装配置文件：GlobalConfigModel

                    // TODO 执行扫描任务

                    // 跳转至运行终端
                    NucleiFrame.frameTabbedPane.setSelectedIndex(2);
                    RunningPanel.tabbedPane.setSelectedComponent(consolePanel);
                }
            }
        });

        // Target.svg
        JButton targetBtn = new JButton(new FlatSVGIcon("icons/Target.svg"));
        targetBtn.setToolTipText("设置全局目标");
        targetBtn.addActionListener(e -> {
            NucleiFrame.frameTabbedPane.setSelectedIndex(1);
            SettingsPanel.tabbedPane.setSelectedIndex(0);
        });

        toolBar.add(executeBtn);
        toolBar.add(targetBtn);

        toolBar.add(Box.createGlue());
        toolBar.add(new JLabel(""));
        toolBar.add(Box.createGlue());
        toolBar.add(mottoLabel);
        toolBar.add(Box.createGlue());

        this.add(toolBar, BorderLayout.NORTH);
    }

    private void initTabbedPane() {
        frameTabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        initClosableTabs(frameTabbedPane);
        customComponents();
        frameTabbedPane.addTab("Templates", new FlatSVGIcon("icons/pinTab.svg"), templatesPanel);
        frameTabbedPane.addTab("Settings", new FlatSVGIcon("icons/pinTab.svg"), settingsPanel);
        frameTabbedPane.addTab("Running", new FlatSVGIcon("icons/pinTab.svg"), runningPanel);

        this.add(frameTabbedPane, BorderLayout.CENTER);
    }

    private void initClosableTabs(JTabbedPane frameTabbedPane) {
        frameTabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSABLE, true);
        frameTabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
        frameTabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_CALLBACK,
                (BiConsumer<JTabbedPane, Integer>) (tabPane, tabIndex) -> {
                    if (tabIndex >= 3) {
                        frameTabbedPane.removeTabAt(tabIndex);
                    }
                });
    }

    private void customComponents() {
        JToolBar trailing;
        trailing = new JToolBar();
        trailing.setFloatable(false);
        trailing.setBorder(null);

        JButton addBtn = new JButton(new FlatSVGIcon("icons/add.svg"));
        addBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Add EditPanel");
                // TODO 添加 Template
                EditTemplatePanel editPanel = new EditTemplatePanel();
                frameTabbedPane.addTab(editPanel.getTitle(), editPanel.getIcon(), editPanel);
                frameTabbedPane.setSelectedIndex(frameTabbedPane.getTabCount() - 1);
            }
        });

        JMenuItem reportItem = new JMenuItem("查看扫描报告");
        reportItem.setIcon(new FlatSVGIcon("icons/MarkdownPlugin.svg"));
        reportItem.addActionListener(new AbstractAction() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                Desktop.getDesktop().open(new File(reportDir));
            }
        });
        trailPopupMenu.add(reportItem);

        JMenuItem templateItem = new JMenuItem("打开模板目录");
        templateItem.setIcon(new FlatSVGIcon("icons/template.svg"));
        templateItem.addActionListener(new AbstractAction() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                Desktop.getDesktop().open(new File(templatesDir));
            }
        });
        trailPopupMenu.add(templateItem);

        // TODO 选项卡面板后置工具栏
        String iconPath = null;
        if (OsUtils.isWin32()) {
            // windows.svg
            iconPath = "icons/windows.svg";
        } else if (OsUtils.isUNIX()) {
            // linux.svg
            iconPath = "icons/linux.svg";
        } else if (OsUtils.isOSX()) {
            // macOS.svg
            iconPath = "icons/macOS.svg";
        }
        JButton trailMenuBtn = new JButton(new FlatSVGIcon(iconPath));
        trailMenuBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                trailPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        trailing.add(addBtn);
        trailing.add(Box.createHorizontalGlue());
        trailing.add(trailMenuBtn);
        frameTabbedPane.putClientProperty(TABBED_PANE_TRAILING_COMPONENT, trailing);
    }

}
