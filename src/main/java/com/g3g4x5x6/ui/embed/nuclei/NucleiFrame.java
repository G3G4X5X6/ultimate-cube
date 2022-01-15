package com.g3g4x5x6.ui.embed.nuclei;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.embed.nuclei.panel.*;
import com.g3g4x5x6.utils.ConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.util.OsUtils;
import org.fife.ui.rsyntaxtextarea.MatchedBracketPopup;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit;
import org.fife.ui.rsyntaxtextarea.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Objects;
import java.util.function.BiConsumer;

import static com.formdev.flatlaf.FlatClientProperties.*;

@Slf4j
public class NucleiFrame extends JFrame {
    public static NucleiFrame nucleiFrame = new NucleiFrame();
    public static JTabbedPane tabbedPane;
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

    private JPopupMenu trailPopupMenu = new JPopupMenu();
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

        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        initClosableTabs(tabbedPane);
        customComponents();
        tabbedPane.addTab("Templates", new FlatSVGIcon("com/g3g4x5x6/ui/icons/pinTab.svg"), new TemplatesPanel());
        tabbedPane.addTab("Settings", new FlatSVGIcon("com/g3g4x5x6/ui/icons/pinTab.svg"), new SettingsPanel());
        tabbedPane.addTab("Running", new FlatSVGIcon("com/g3g4x5x6/ui/icons/pinTab.svg"), new RunningPanel());

        this.setJMenuBar(menuBar);
        this.add(tabbedPane, BorderLayout.CENTER);
    }

    private void initClosableTabs(JTabbedPane tabbedPane) {
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSABLE, true);
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_CALLBACK,
                (BiConsumer<JTabbedPane, Integer>) (tabPane, tabIndex) -> {
                    if (tabIndex >= 3) {
                        tabbedPane.removeTabAt(tabIndex);
                    }
                });
    }


    private void customComponents() {
        JToolBar trailing = null;
        trailing = new JToolBar();
        trailing.setFloatable(false);
        trailing.setBorder(null);

        JButton addBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/add.svg"));
        addBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 添加 Template
                EditPanel editorPanel = new EditPanel();
                tabbedPane.addTab(editorPanel.getTitle(),
                        editorPanel.getIcon(),
                        editorPanel);
                tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
            }
        });

        // Target.svg
        JButton targetBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/Target.svg"));
        targetBtn.setToolTipText("设置目标URL");
        JPopupMenu targetPopupMenu = new JPopupMenu();
        targetPopupMenu.setBorder(null);
        targetPopupMenu.setSize(new Dimension(700, 200));
        targetPopupMenu.setPreferredSize(new Dimension(700, 200));
        targetPopupMenu.add(targetPanel);
        targetBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                targetPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

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
        JMenuItem item = new JMenuItem("代码安全检查");
        item.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/shield.svg"));
        item.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(NucleiFrame.this, "敬请期待！", "信息", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        trailPopupMenu.add(item);
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
        tabbedPane.putClientProperty(TABBED_PANE_TRAILING_COMPONENT, trailing);
    }

    private RSyntaxTextArea createTextArea() {
        RSyntaxTextArea textArea = new RSyntaxTextArea();
        textArea.requestFocusInWindow();
        textArea.setCaretPosition(0);
        textArea.setMarkOccurrences(true);
        textArea.setCodeFoldingEnabled(true);
        textArea.setClearWhitespaceLinesEnabled(false);
        textArea.setCodeFoldingEnabled(true);
        textArea.setSyntaxEditingStyle("text/plain");

        InputMap im = textArea.getInputMap();
        ActionMap am = textArea.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), "decreaseFontSize");
        am.put("decreaseFontSize", new RSyntaxTextAreaEditorKit.DecreaseFontSizeAction());
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), "increaseFontSize");
        am.put("increaseFontSize", new RSyntaxTextAreaEditorKit.IncreaseFontSizeAction());

        int ctrlShift = InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK;
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, ctrlShift), "copyAsStyledText");
        am.put("copyAsStyledText", new RSyntaxTextAreaEditorKit.CopyCutAsStyledTextAction(true));

        try {

            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, ctrlShift), "copyAsStyledTextMonokai");
            am.put("copyAsStyledTextMonokai", createCopyAsStyledTextAction("monokai"));

            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, ctrlShift), "copyAsStyledTextEclipse");
            am.put("copyAsStyledTextEclipse", createCopyAsStyledTextAction("dark"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // Since this demo allows the LookAndFeel and RSyntaxTextArea Theme to
        // be toggled independently of one another, we set this property to
        // true so matched bracket popups look good.  In an app where the
        // developer ensures the RSTA Theme always matches the LookAndFeel as
        // far as light/dark is concerned, this property can be omitted.
        System.setProperty(MatchedBracketPopup.PROPERTY_CONSIDER_TEXTAREA_BACKGROUND, "true");

        return textArea;
    }

    private Action createCopyAsStyledTextAction(String themeName) throws IOException {
        String resource = "/org/fife/ui/rsyntaxtextarea/themes/" + themeName + ".xml";
        Theme theme = Theme.load(this.getClass().getResourceAsStream(resource));
        return new RSyntaxTextAreaEditorKit.CopyCutAsStyledTextAction(themeName, theme, true);
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
