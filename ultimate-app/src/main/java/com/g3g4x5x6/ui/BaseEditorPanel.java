package com.g3g4x5x6.ui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.utils.DialogUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.rsyntaxtextarea.MatchedBracketPopup;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.formdev.flatlaf.util.SystemInfo.isWindows;

@Slf4j
public class BaseEditorPanel extends JPanel implements SearchListener {
    private final JButton newBtn = new JButton(new FlatSVGIcon("icons/addFile.svg"));
    private final JButton openBtn = new JButton(new FlatSVGIcon("icons/menu-open.svg"));
    private final JButton saveBtn = new JButton(new FlatSVGIcon("icons/menu-saveall.svg"));
    private final JButton searchBtn = new JButton(new FlatSVGIcon("icons/find.svg"));
    private final JButton replaceBtn = new JButton(new FlatSVGIcon("icons/replace.svg"));
    private final JToggleButton lineWrapBtn = new JToggleButton(new FlatSVGIcon("icons/toggleSoftWrap.svg"));

    @Setter
    private String title = "EditorPanel";
    private String tips = "文本编辑面板";
    @Setter
    private String savePath = "";
    private String currentDirectory = System.getProperty("user.dir");

    @Setter
    @Getter
    private FlatSVGIcon icon = new FlatSVGIcon("icons/file-yaml.svg");
    private final RSyntaxTextArea textArea;
    private FindDialog findDialog;
    private ReplaceDialog replaceDialog;


    public BaseEditorPanel() {
        this.setLayout(new BorderLayout());
        JToolBar toolBar = new JToolBar();
        this.add(toolBar, BorderLayout.NORTH);
        toolBar.setFloatable(false);
        toolBar.add(newBtn);
        toolBar.add(openBtn);
        toolBar.add(saveBtn);
        toolBar.addSeparator();
        toolBar.add(lineWrapBtn);
        toolBar.addSeparator();
        toolBar.add(searchBtn);
        toolBar.add(replaceBtn);
        toolBar.addSeparator();
        toolBar.addSeparator();
        initToolBarAction();

        this.textArea = createTextArea();
        RTextScrollPane sp = new RTextScrollPane(textArea);
        sp.setBorder(null);
        this.add(sp, BorderLayout.CENTER);
        initSearchDialogs();
    }

    public BaseEditorPanel(String savePath) {
        this();
        this.setSavePath(savePath);
        if (isWindows) {
            this.setTitle(savePath.substring(savePath.lastIndexOf("\\") + 1));
        } else {
            this.setTitle(savePath.substring(savePath.lastIndexOf("/") + 1));
        }
        this.textArea.setText(getTextFromSavePath());
    }

    @SneakyThrows
    private String getTextFromSavePath() {
        StringBuilder str = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(savePath, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                str.append(line);
                str.append("\n");
            }
        } catch (IOException ioException) {
            System.out.println(ioException.getMessage());
            DialogUtil.error(ioException.getMessage());
        }
        return str.toString();
    }

    private void initToolBarAction() {
        newBtn.setToolTipText("新建文件");
        newBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("新建文件");
            }
        });
        openBtn.setToolTipText("打开文件");
        openBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setCurrentDirectory(new File(currentDirectory));
                fileChooser.setMultiSelectionEnabled(false);
                int result = fileChooser.showOpenDialog(BaseEditorPanel.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    setTitle(file.getName());
                    savePath = file.getAbsolutePath();
                    textArea.setText(getTextFromSavePath());
                }
                log.debug("打开文件......");
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

        saveBtn.setToolTipText("保存文件");
        saveBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (savePath.equalsIgnoreCase("")) {
                    // 新建保存
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fileChooser.setMultiSelectionEnabled(false);
                    fileChooser.setSelectedFile(new File("example.yaml"));
                    int result = fileChooser.showOpenDialog(BaseEditorPanel.this);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        savePath = file.getAbsolutePath();
                        title = file.getName();
                        try {
                            Files.write(Path.of(savePath), textArea.getText().getBytes(StandardCharsets.UTF_8));
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                } else {
                    try {
                        Files.write(Path.of(savePath), textArea.getText().getBytes(StandardCharsets.UTF_8));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                log.debug("保存配置：" + savePath);
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

        return textArea;
    }

    private Action createCopyAsStyledTextAction(String themeName) throws IOException {
        String resource = "/org/fife/ui/rsyntaxtextarea/themes/" + themeName + ".xml";
        Theme theme = Theme.load(this.getClass().getResourceAsStream(resource));
        return new RSyntaxTextAreaEditorKit.CopyCutAsStyledTextAction(themeName, theme, true);
    }

    private void initSearchDialogs() {
        log.debug("initSearchDialogs");
        findDialog = new FindDialog((Dialog) null, this);
        replaceDialog = new ReplaceDialog((Dialog) null, this);

        // This ties the properties of the two dialogs together (match case,
        // regex, etc.).
        SearchContext context = findDialog.getSearchContext();
        replaceDialog.setSearchContext(context);
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

    public void setTextArea(String text) {
        this.textArea.setText(text);
    }

    public void setSyntaxEditingStyle(String style) {
        this.textArea.setSyntaxEditingStyle(style);
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
            DialogUtil.warn(text);
        }
    }

}
