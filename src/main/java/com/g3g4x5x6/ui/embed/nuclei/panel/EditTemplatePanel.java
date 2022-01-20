package com.g3g4x5x6.ui.embed.nuclei.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.MainFrame;
import com.g3g4x5x6.ui.embed.nuclei.NucleiFrame;
import com.g3g4x5x6.ui.embed.nuclei.NucleiYamlCompletionProvider;
import com.g3g4x5x6.utils.DialogUtil;
import com.g3g4x5x6.utils.os.OsInfoUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
public class EditTemplatePanel extends JPanel implements SearchListener {
    private final JButton openBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/menu-open.svg"));
    private final JButton saveBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/menu-saveall.svg"));
    private final JButton searchBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/search.svg"));
    private final JButton replaceBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/replace.svg"));
    private final JToggleButton lineWrapBtn = new JToggleButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/toggleSoftWrap.svg"));
    private final JButton executeBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/execute.svg"));
    private final JButton startDebuggerBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/startDebugger.svg"));
    private final JButton targetBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/targetTest.svg"));

    private String title = "NewTemplate.yaml";
    private String tips = "Nuclei's Template";
    private String savePath = "";
    private final String uuid = UUID.randomUUID().toString();
    private final FlatSVGIcon icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/file-yaml.svg");
    private final RSyntaxTextArea textArea;
    private FindDialog findDialog;
    private ReplaceDialog replaceDialog;

    public EditTemplatePanel() {
        this.setLayout(new BorderLayout());
        JToolBar toolBar = new JToolBar();
        this.add(toolBar, BorderLayout.NORTH);
        toolBar.setFloatable(false);
        toolBar.add(openBtn);
        toolBar.add(saveBtn);
        toolBar.addSeparator();
        toolBar.add(lineWrapBtn);
        toolBar.addSeparator();
        toolBar.add(searchBtn);
        toolBar.add(replaceBtn);
        toolBar.addSeparator();
        toolBar.add(executeBtn);
        toolBar.add(startDebuggerBtn);
        toolBar.add(targetBtn);
        initToolBarAction();

        this.textArea = createTextArea();
        RTextScrollPane sp = new RTextScrollPane(textArea);
        sp.setBorder(null);
        this.add(sp, BorderLayout.CENTER);
        initSearchDialogs();
    }

    public EditTemplatePanel(String savePath) {
        this();
        this.setSavePath(savePath);
        if (OsInfoUtil.isWindows()) {
            this.setTitle(savePath.substring(savePath.lastIndexOf("\\") + 1));
        } else {
            this.setTitle(savePath.substring(savePath.lastIndexOf("/") + 1));
        }
        this.textArea.setText(getTextFromSavePath());

    }

    @SneakyThrows
    private String getTextFromSavePath() {
        StringBuilder str = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(savePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                str.append(line);
                str.append("\n");
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
            DialogUtil.error(ioException.getMessage());
        }
        return str.toString();
    }

    private void initToolBarAction() {
        openBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setCurrentDirectory(new File(NucleiFrame.templatesDir));
                fileChooser.setMultiSelectionEnabled(false);
                int result = fileChooser.showOpenDialog(NucleiFrame.nucleiFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    setTitle(file.getName());
                    NucleiFrame.frameTabbedPane.setTitleAt(NucleiFrame.frameTabbedPane.getSelectedIndex(), title);
                    savePath = file.getAbsolutePath();
                    textArea.setText(getTextFromSavePath());
                }
                log.debug("打开 Template......");
            }
        });
        searchBtn.setToolTipText("搜索......");
        searchBtn.addActionListener(showFindDialogAction);
        searchBtn.registerKeyboardAction(showFindDialogAction, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
        replaceBtn.setToolTipText("替换......");
        replaceBtn.addActionListener(showReplaceDialogAction);
        replaceBtn.registerKeyboardAction(showReplaceDialogAction, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        lineWrapBtn.addChangeListener(e -> {
            textArea.setLineWrap(lineWrapBtn.isSelected());
        });

        saveBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (savePath.equalsIgnoreCase("")) {
                    // 新建保存
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fileChooser.setMultiSelectionEnabled(false);
                    fileChooser.setSelectedFile(new File("example.yaml"));
                    int result = fileChooser.showOpenDialog(NucleiFrame.nucleiFrame);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        savePath = file.getAbsolutePath();
                        try {
                            Files.write(Path.of(savePath), textArea.getText().getBytes(StandardCharsets.UTF_8));
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }else{
                    try {
                        Files.write(Path.of(savePath), textArea.getText().getBytes(StandardCharsets.UTF_8));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                log.debug("保存 Template：" + savePath);
            }
        });

        executeBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("执行 Template......");
            }
        });

        startDebuggerBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("调试模式执行 Template......");
            }
        });

        targetBtn.setToolTipText("设置测试目标URL(高优先级)");
        JPopupMenu targetPopupMenu = new JPopupMenu();
        targetPopupMenu.setBorder(null);
        targetPopupMenu.setSize(new Dimension(600, 200));
        targetPopupMenu.setPreferredSize(new Dimension(600, 200));
        targetPopupMenu.add(new TargetPanel());
        targetBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                targetPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

    }

    AbstractAction showFindDialogAction = new AbstractAction("查找") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (replaceDialog.isVisible()) {
                replaceDialog.setVisible(false);
            }
            findDialog.setVisible(true);
        }
    };

    AbstractAction showReplaceDialogAction = new AbstractAction("替换") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (findDialog.isVisible()) {
                findDialog.setVisible(false);
            }
            replaceDialog.setVisible(true);
        }
    };

    private RSyntaxTextArea createTextArea() {
        RSyntaxTextArea textArea = new RSyntaxTextArea();
        textArea.requestFocusInWindow();
        textArea.setCaretPosition(0);
        textArea.setMarkOccurrences(true);
        textArea.setCodeFoldingEnabled(true);
        textArea.setClearWhitespaceLinesEnabled(false);
        textArea.setCodeFoldingEnabled(true);
        textArea.setSyntaxEditingStyle("text/yaml");

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

        AutoCompletion ac = new AutoCompletion(new NucleiYamlCompletionProvider());
        ac.install(textArea);
        // TODO 快捷键与自动激活作为一个用户设置，二选一
//        ac.setAutoActivationEnabled(true);  // 找到唯一符合的关键字，将直接自动完成
        ac.setTriggerKey(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));

        return textArea;
    }

    private Action createCopyAsStyledTextAction(String themeName) throws IOException {
        String resource = "/org/fife/ui/rsyntaxtextarea/themes/" + themeName + ".xml";
        Theme theme = Theme.load(this.getClass().getResourceAsStream(resource));
        return new RSyntaxTextAreaEditorKit.CopyCutAsStyledTextAction(themeName, theme, true);
    }

    private void initSearchDialogs() {
        log.debug("initSearchDialogs");
        findDialog = new FindDialog(NucleiFrame.nucleiFrame, this);
        replaceDialog = new ReplaceDialog(MainFrame.embedEditor, this);

        // This ties the properties of the two dialogs together (match case,
        // regex, etc.).
        SearchContext context = findDialog.getSearchContext();
        replaceDialog.setSearchContext(context);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getTips() {
        return tips;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getUuid() {
        return uuid;
    }

    public FlatSVGIcon getIcon() {
        return icon;
    }

    @Override
    public String getSelectedText() {
        return textArea.getSelectedText();
    }

    @Override
    public void searchEvent(SearchEvent e) {
        SearchEvent.Type type = e.getType();
        SearchContext context = e.getSearchContext();
        SearchResult result;

        switch (type) {
            default: // Prevent FindBugs warning later
            case MARK_ALL:
                result = SearchEngine.markAll(textArea, context);
                break;
            case FIND:
                result = SearchEngine.find(textArea, context);
                if (!result.wasFound() || result.isWrapped()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textArea);
                }
                break;
            case REPLACE:
                result = SearchEngine.replace(textArea, context);
                if (!result.wasFound() || result.isWrapped()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textArea);
                }
                break;
            case REPLACE_ALL:
                result = SearchEngine.replaceAll(textArea, context);
                JOptionPane.showMessageDialog(null, result.getCount() +
                        " occurrences replaced.");
                break;
        }

        String text;
        if (result.wasFound()) {
            text = "Text found; occurrences marked: " + result.getMarkedCount();
        } else if (type == SearchEvent.Type.MARK_ALL) {
            if (result.getMarkedCount() > 0) {
                text = "Occurrences marked: " + result.getMarkedCount();
            } else {
                text = "";
            }
        } else {
            text = "Text not found";
        }
        MainFrame.embedEditor.setSearchStatusLabelStr(text);
    }
}
