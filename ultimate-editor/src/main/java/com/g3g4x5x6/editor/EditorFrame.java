package com.g3g4x5x6.editor;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatToggleButton;
import com.g3g4x5x6.editor.ui.StatusBar;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.util.OsUtils;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.BiConsumer;

import static com.formdev.flatlaf.FlatClientProperties.*;


@Slf4j
public class EditorFrame extends JFrame implements ActionListener {
    private static EditorFrame editorFrame = null;
    private JMenuBar menuBar = new JMenuBar();
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

    private JToolBar toolBar = new JToolBar();
    private JButton newBtn = new JButton(new FlatSVGIcon("icons/addFile.svg"));
    private JButton openBtn = new JButton(new FlatSVGIcon("icons/menu-open.svg"));
    private JButton saveBtn = new JButton(new FlatSVGIcon("icons/menu-saveall.svg"));
    private JButton cutBtn = new JButton(new FlatSVGIcon("icons/menu-cut.svg"));
    private JButton copyBtn = new JButton(new FlatSVGIcon("icons/copy.svg"));
    private JButton pasteBtn = new JButton(new FlatSVGIcon("icons/menu-paste.svg"));
    private JButton undoBtn = new JButton(new FlatSVGIcon("icons/undo.svg"));
    private JButton redoBtn = new JButton(new FlatSVGIcon("icons/redo.svg"));
    private JButton searchBtn = new JButton(new FlatSVGIcon("icons/find.svg"));
    private JButton replaceBtn = new JButton(new FlatSVGIcon("icons/replace.svg"));
    private JToggleButton lineWrapBtn = new JToggleButton(new FlatSVGIcon("icons/toggleSoftWrap.svg"));
    private JTabbedPane tabbedPane;
    private StatusBar statusBar;
    private JPopupMenu trailPopupMenu = new JPopupMenu();
    private JPopupMenu rightPopupMenu = new JPopupMenu();
    private Clipboard clipboard;
    private JLabel syntaxLabel;
    private JLabel searchStatusLabel;

    private final LinkedList<EditorPanel> globalWindows = new LinkedList<>();

    public EditorFrame() {
        this.setLayout(new BorderLayout());
        this.setTitle("简易编辑器");
        this.setSize(new Dimension(1200, 700));
        this.setPreferredSize(new Dimension(1200, 700));
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("icon.png"))).getImage());

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
        // TODO 置顶图标按钮
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
        initMenuBar();

        toolBar.setFloatable(false);
        toolBar.add(newBtn);
        toolBar.add(openBtn);
        toolBar.add(saveBtn);
        toolBar.addSeparator();
        toolBar.add(cutBtn);
        toolBar.add(copyBtn);
        toolBar.add(pasteBtn);
        toolBar.addSeparator();
        toolBar.add(undoBtn);
        toolBar.add(redoBtn);
        toolBar.addSeparator();
        toolBar.add(lineWrapBtn);
        toolBar.addSeparator();
        toolBar.add(searchBtn);
        toolBar.add(replaceBtn);
        toolBar.addSeparator();
        initToolbarAction();

        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        initClosableTabs(tabbedPane);
        customComponents();
        tabbedPane.addChangeListener(this::resetIcon);
        initRightPopupMenu();
        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3)
                    rightPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        statusBar = new StatusBar();
        initStatusBar();

        this.setJMenuBar(menuBar);
        this.add(toolBar, BorderLayout.NORTH);
        this.add(tabbedPane, BorderLayout.CENTER);
        this.add(statusBar, BorderLayout.SOUTH);
    }

    public static EditorFrame getInstance() {
        if (editorFrame == null)
            editorFrame = new EditorFrame();
        return editorFrame;
    }

    private void initMenuBar() {
        JCheckBoxMenuItem showToolBar = new JCheckBoxMenuItem("显示工具栏   ");
        showToolBar.setSelected(true);
        showToolBar.addActionListener(e -> toolBar.setVisible(showToolBar.isSelected()));
        viewMenu.add(showToolBar);
    }

    private void resetIcon(ChangeEvent e) {
        JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
        int index = tabbedPane.getSelectedIndex();
        if (index != -1) {
            EditorPanel editorPanel = (EditorPanel) tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
            syntaxLabel.setIcon(editorPanel.getIcon());
            syntaxLabel.setText(editorPanel.getSyntax());
        }
    }

    private void initToolbarAction() {
        newBtn.setToolTipText("新建(N)");
        newBtn.addActionListener(newAction);
        newBtn.registerKeyboardAction(newAction, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
        openBtn.setToolTipText("打开(O)");
        openBtn.addActionListener(openAction);
        openBtn.registerKeyboardAction(openAction, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
        saveBtn.setToolTipText("保存(S)");
        saveBtn.addActionListener(saveAction);
        saveBtn.registerKeyboardAction(saveAction, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
        lineWrapBtn.setToolTipText("自动换行");
        lineWrapBtn.addChangeListener(e -> {
            EditorPanel editorPanel = (EditorPanel) tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
            editorPanel.setLineWrap(lineWrapBtn.isSelected());
        });
        searchBtn.setToolTipText("搜索......");
        searchBtn.addActionListener(showFindDialogAction);
        searchBtn.registerKeyboardAction(showFindDialogAction, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
        replaceBtn.setToolTipText("替换......");
        replaceBtn.addActionListener(showReplaceDialogAction);
        replaceBtn.registerKeyboardAction(showReplaceDialogAction, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }


    private void initStatusBar() {

        // 搜索状态
        searchStatusLabel = new JLabel();
        searchStatusLabel.setIcon(new FlatSVGIcon("icons/green.svg"));
        searchStatusLabel.setToolTipText("查找状态提示");

        // 文件类型
        syntaxLabel = new JLabel("text/plain");
        syntaxLabel.setIcon(new FlatSVGIcon("icons/applicationExtension.svg"));
        JPopupMenu langMenu = new JPopupMenu();
        langMenu.setAutoscrolls(true);
//        langMenu.setSize(new Dimension(200, 1000));
//        langMenu.setPreferredSize(new Dimension(200, 1000));
        Class<SyntaxConstants> syntaxConstantsClass = SyntaxConstants.class;
        Field[] fields = syntaxConstantsClass.getDeclaredFields();
        for (Field field : fields) {
            try {
                String langStr = (String) field.get(syntaxConstantsClass);
                JMenuItem temp = new JMenuItem(langStr);
                temp.addActionListener(e -> {
                    EditorPanel editorPanel = (EditorPanel) tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
                    editorPanel.setSyntax(langStr);
                    syntaxLabel.setText(langStr);
                    syntaxLabel.setIcon(editorPanel.getIcon());
                });
                langMenu.add(temp);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        syntaxLabel.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3) {    // 右键鼠标
                    log.debug("改变编辑器语言语法风格");
                    langMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        statusBar.setComponent(new JLabel("  "));
        statusBar.setComponent(searchStatusLabel);
        statusBar.setGlue();
        statusBar.setComponent(syntaxLabel);
    }

    private void initClosableTabs(JTabbedPane tabbedPane) {
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSABLE, true);
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_CALLBACK,
                (BiConsumer<JTabbedPane, Integer>) (tabPane, tabIndex) -> {
                    globalWindows.remove(tabPane.getComponentAt(tabIndex));
                    this.tabbedPane.removeTabAt(tabIndex);
                    if (this.tabbedPane.getTabCount() == 0) {
                        addAndSelectPanel(new EditorPanel());
                    }
                });
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
                tabbedPane.setSelectedIndex(0);
            }
        });

        JButton addBtn = new JButton(new FlatSVGIcon("icons/add.svg"));
        addBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAndSelectPanel(new EditorPanel());
            }
        });

        // TODO 选项卡面板前置工具栏，暂不使用
        leading.add(dashboardBtn);
//        mainTabbedPane.putClientProperty(TABBED_PANE_LEADING_COMPONENT, leading);
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
        JMenuItem item = new JMenuItem("代码安全检查");
        item.setIcon(new FlatSVGIcon("icons/shield.svg"));
        item.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(com.g3g4x5x6.editor.EditorFrame.this, "敬请期待！", "信息", JOptionPane.INFORMATION_MESSAGE);
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
        trailing.add(Box.createHorizontalGlue());
        trailing.add(trailMenuBtn);
        tabbedPane.putClientProperty(TABBED_PANE_TRAILING_COMPONENT, trailing);
    }

    public void addAndSelectPanel(EditorPanel editorPanel) {
        boolean flag = true;
        for (EditorPanel editor : globalWindows) {
            if (editor.getSavePath() != null && editor.getSavePath().equals(editorPanel.getSavePath())) {
                log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>" + editor.getSavePath());
                if (editor.getFs() != null && editor.getFs().getId().equals(editorPanel.getFs().getId())) {
                    this.tabbedPane.setSelectedComponent(editor);
                    flag = false;
                    log.debug("已有编辑面板");
                }
            }
        }
        if (flag) {
            this.globalWindows.add(editorPanel);
            this.tabbedPane.addTab(editorPanel.getTitle(), editorPanel.getIcon(), editorPanel, editorPanel.getTips());
            this.tabbedPane.setSelectedIndex(this.tabbedPane.getTabCount() - 1);
        }
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    private boolean exist(EditorPanel editorPanel) {
        return false;
    }

    /**
     * 获取当前选中的Tab面板
     *
     * @return EditorPanel
     */
    private EditorPanel getCurrentEditorPanel() {
        return (EditorPanel) this.tabbedPane.getComponentAt(this.tabbedPane.getSelectedIndex());
    }

    private void initRightPopupMenu() {
        rightPopupMenu.add(copyPathAction);
    }

    private void quickAction(String command) throws IOException {
        switch (command) {
            case "newAction":
                log.debug("newAction");
                addAndSelectPanel(new EditorPanel());
                break;
            case "saveAction":
                log.debug("saveAction");
                EditorPanel editorPanel = getCurrentEditorPanel();
                if (editorPanel.getFs() != null) {
                    // 远程文件保存   Files.write(fs.getPath(titleField.getSelectedItem().toString().strip()), textArea.getText().getBytes(StandardCharsets.UTF_8));
                    Files.write(editorPanel.getFs().getPath(editorPanel.getSavePath()), editorPanel.getTextArea().getBytes(StandardCharsets.UTF_8));
                } else {
                    // 本地文件保存
                    if (editorPanel.getSavePath() == null) {
                        // 新建保存
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        fileChooser.setMultiSelectionEnabled(false);
                        fileChooser.setSelectedFile(new File("新建文件.txt"));
                        int result = fileChooser.showOpenDialog(this);
                        if (result == JFileChooser.APPROVE_OPTION) {
                            File file = fileChooser.getSelectedFile();
                            log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>" + file.getAbsolutePath());
                            editorPanel.setSavePath(file.getAbsolutePath());
                            Files.write(Path.of(file.getAbsolutePath()), editorPanel.getTextArea().getBytes(StandardCharsets.UTF_8));
                        }
                    } else {
                        // 更新保存
                        Files.write(Path.of(editorPanel.getSavePath()), editorPanel.getTextArea().getBytes(StandardCharsets.UTF_8));
                    }
                }
                break;
        }
    }


    /**
     * Getter & Setter
     */
    public void setSearchStatusLabelStr(String searchStatusLabel) {
        this.searchStatusLabel.setText(searchStatusLabel);
    }

    /**
     * toolBar Action 区域
     */
    @SneakyThrows
    @Override
    public void actionPerformed(ActionEvent e) {
        log.debug(e.getActionCommand());
    }

    AbstractAction newAction = new AbstractAction("newAction") {
        @SneakyThrows
        @Override
        public void actionPerformed(ActionEvent e) {
            quickAction("newAction");
        }
    };

    AbstractAction openAction = new AbstractAction("openAction") {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(true);
            int result = fileChooser.showOpenDialog(com.g3g4x5x6.editor.EditorFrame.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File[] files = fileChooser.getSelectedFiles();
                for (File file : files) {
                    String text = "";
                    try {
                        for (String line : Files.readAllLines(Path.of(file.getAbsolutePath()))) {
                            text += line + "\n";
                        }
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    EditorPanel editorPanel = new EditorPanel(file.getAbsolutePath());
                    editorPanel.setTitle(file.getName());
                    editorPanel.setTextArea(text);
                    addAndSelectPanel(editorPanel);
                }
            }
        }
    };

    AbstractAction saveAction = new AbstractAction("saveAction") {
        @SneakyThrows
        @Override
        public void actionPerformed(ActionEvent e) {
            quickAction("saveAction");
        }
    };

    /**
     * tabbedPane 右键菜单 Action 区域
     */
    AbstractAction copyPathAction = new AbstractAction("复制路径") {
        @Override
        public void actionPerformed(ActionEvent e) {
            EditorPanel editorPanel = getCurrentEditorPanel();
            if (clipboard == null)
                clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); //获得系统剪贴板
            Transferable transferable = new StringSelection(editorPanel.getSavePath());
            clipboard.setContents(transferable, null);
        }
    };

    AbstractAction showFindDialogAction = new AbstractAction("查找") {
        @Override
        public void actionPerformed(ActionEvent e) {
            EditorPanel editorPanel = getCurrentEditorPanel();
            if (editorPanel.getReplaceDialog().isVisible()) {
                editorPanel.getReplaceDialog().setVisible(false);
            }
            editorPanel.getFindDialog().setVisible(true);
        }
    };

    AbstractAction showReplaceDialogAction = new AbstractAction("替换") {
        @Override
        public void actionPerformed(ActionEvent e) {
            EditorPanel editorPanel = getCurrentEditorPanel();
            if (editorPanel.getFindDialog().isVisible()) {
                editorPanel.getFindDialog().setVisible(false);
            }
            editorPanel.getReplaceDialog().setVisible(true);
        }
    };

}
