package com.g3g4x5x6.ui.embed.nuclei.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.MainFrame;
import com.g3g4x5x6.ui.embed.editor.provider.BashCompletionProvider;
import com.g3g4x5x6.ui.embed.nuclei.NucleiFrame;
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
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class EditPanel extends JPanel implements SearchListener {
    private JToolBar toolBar = new JToolBar();
    private JButton openBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/menu-open.svg"));
    private JButton saveAllBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/menu-saveall.svg"));
    private JButton cutBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/menu-cut.svg"));
    private JButton copyBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/copy.svg"));
    private JButton pasteBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/menu-paste.svg"));
    private JButton undoBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/undo.svg"));
    private JButton redoBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/redo.svg"));
    private JButton searchBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/search.svg"));
    private JButton replaceBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/replace.svg"));
    private JToggleButton lineWrapBtn = new JToggleButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/toggleSoftWrap.svg"));
    private JButton terminalBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/changeView.svg"));

    private String title = "NewTemplate.yaml";
    private String tips = "Nuclei's Template";
    private String uuid = UUID.randomUUID().toString();
    private String savePath = "";
    private FlatSVGIcon icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/file-yaml.svg");
    private RSyntaxTextArea textArea;
    private RTextScrollPane sp;
    private FindDialog findDialog;
    private ReplaceDialog replaceDialog;
    private AutoCompletion ac = null;
    private String syntax = "text/yaml";

    public EditPanel() {
        this.setLayout(new BorderLayout());
        this.add(toolBar, BorderLayout.NORTH);
        toolBar.setFloatable(false);
        toolBar.add(openBtn);
        toolBar.add(saveAllBtn);
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
        toolBar.add(terminalBtn);
        initToolBarAction();

        this.textArea = createTextArea();
        this.sp = new RTextScrollPane(textArea);
        this.sp.setBorder(null);
        this.add(sp, BorderLayout.CENTER);
        initSearchDialogs();
    }

    private void initToolBarAction() {
        searchBtn.setToolTipText("搜索......");
        searchBtn.addActionListener(showFindDialogAction);
        searchBtn.registerKeyboardAction(showFindDialogAction, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
        replaceBtn.setToolTipText("替换......");
        replaceBtn.addActionListener(showReplaceDialogAction);
        replaceBtn.registerKeyboardAction(showReplaceDialogAction, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

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
        textArea.setSyntaxEditingStyle(syntax);

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

        ac = new AutoCompletion(new NucleiYamlCompletionProvider());
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
