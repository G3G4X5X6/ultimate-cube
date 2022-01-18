package com.g3g4x5x6.ui.embed.nuclei;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.embed.nuclei.panel.*;
import com.g3g4x5x6.utils.ConfigUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.util.OsUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Objects;
import java.util.function.BiConsumer;

import static com.formdev.flatlaf.FlatClientProperties.*;

@Slf4j
public class NucleiFrame extends JFrame {
    public static NucleiFrame nucleiFrame = new NucleiFrame();
    public static JTabbedPane frameTabbedPane;
    public static String reportDir = ConfigUtil.getWorkPath() + "/report/nuclei";
    private JMenu fileMenu = new JMenu("文件");
    private JMenu editMenu = new JMenu("编辑");
    private JMenu searchMenu = new JMenu("搜索");
    private JMenu viewMenu = new JMenu("视图");
    private JMenu encodeMenu = new JMenu("编码");
    private JMenu langMenu = new JMenu("语言");
    private JMenu settingsMenu = new JMenu("设置");
    private JMenu macroMenu = new JMenu("宏");
    private JMenu runMenu = new JMenu("运行");
    private JMenu pluginMenu = new JMenu("插件");
    private JMenu winMenu = new JMenu("窗口");
    private JMenu aboutMenu = new JMenu("关于");

    private final JPopupMenu trailPopupMenu = new JPopupMenu();
    private final TargetPanel targetPanel = new TargetPanel();

    public NucleiFrame() {
        this.setLayout(new BorderLayout());
        this.setTitle("Nuclei");
        this.setSize(new Dimension(1200, 700));
        this.setPreferredSize(new Dimension(1200, 700));
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("icon.png"))).getImage());

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

        frameTabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        initClosableTabs(frameTabbedPane);
        customComponents();
        frameTabbedPane.addTab("Templates", new FlatSVGIcon("com/g3g4x5x6/ui/icons/pinTab.svg"), new TemplatesPanel());
        frameTabbedPane.addTab("Settings", new FlatSVGIcon("com/g3g4x5x6/ui/icons/pinTab.svg"), new SettingsPanel());
        frameTabbedPane.addTab("Running", new FlatSVGIcon("com/g3g4x5x6/ui/icons/pinTab.svg"), new RunningPanel());

        this.setJMenuBar(menuBar);
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

        JButton addBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/add.svg"));
        addBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Add EditPanel");
                // TODO 添加 Template
                EditPanel editPanel = new EditPanel();
                frameTabbedPane.addTab(editPanel.getTitle(), editPanel.getIcon(), editPanel);
                frameTabbedPane.setSelectedIndex(frameTabbedPane.getTabCount() - 1);
            }
        });

        // Target.svg
        JButton targetBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/Target.svg"));
        targetBtn.setToolTipText("设置目标URL");
        JPopupMenu targetPopupMenu = new JPopupMenu();
        targetPopupMenu.setBorder(null);
        targetPopupMenu.setSize(new Dimension(500, 200));
        targetPopupMenu.setPreferredSize(new Dimension(500, 200));
        targetPopupMenu.add(targetPanel);
        targetBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                targetPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        JMenuItem reportItem = new JMenuItem("查看扫描报告");
        reportItem.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/MarkdownPlugin.svg"));
        reportItem.addActionListener(new AbstractAction() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                Desktop.getDesktop().open(new File(reportDir));
            }
        });
        trailPopupMenu.add(reportItem);

        // TODO 选项卡面板后置工具栏
        String iconPath = null;
        if (OsUtils.isWin32()) {
            // windows.svg
            iconPath = "com/g3g4x5x6/ui/icons/windows.svg";
        } else if (OsUtils.isUNIX()) {
            // linux.svg
            iconPath = "com/g3g4x5x6/ui/icons/linux.svg";
        } else if (OsUtils.isOSX()) {
            // macOS.svg
            iconPath = "com/g3g4x5x6/ui/icons/macOS.svg";
        }
        JButton trailMenuBtn = new JButton(new FlatSVGIcon(iconPath));
        trailMenuBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                trailPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        trailing.add(addBtn);
        trailing.add(targetBtn);
        trailing.add(Box.createHorizontalGlue());
        trailing.add(Box.createHorizontalGlue());
        trailing.add(trailMenuBtn);
        frameTabbedPane.putClientProperty(TABBED_PANE_TRAILING_COMPONENT, trailing);
    }

    public static void main(String[] args) {
        initFlatLaf();
        NucleiFrame nuclei = new NucleiFrame();
        nuclei.setDefaultCloseOperation(NucleiFrame.EXIT_ON_CLOSE);
        nuclei.setVisible(true);
    }

    private static void initFlatLaf() {
        try {
            if (ConfigUtil.isEnableTheme()) {
                UIManager.setLookAndFeel(ConfigUtil.getThemeClass());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
        } catch (Exception ex) {
            log.error("Failed to initialize LaF !!!!!!!! \n" + ex.getMessage());
        }
    }
}
