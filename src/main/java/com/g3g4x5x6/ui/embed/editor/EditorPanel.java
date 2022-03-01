package com.g3g4x5x6.ui.embed.editor;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.MainFrame;
import com.g3g4x5x6.ui.embed.editor.provider.BashCompletionProvider;
import com.g3g4x5x6.ui.embed.editor.provider.JavaCompletionProvider;
import com.g3g4x5x6.utils.DialogUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.sftp.client.fs.SftpFileSystem;
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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.UUID;


@Slf4j
public class EditorPanel extends JPanel implements SearchListener {

    private String title = "新建文件.txt";
    private String tips = "默认提示文本";
    private String uuid = UUID.randomUUID().toString();
    private FlatSVGIcon icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/file-text.svg");
    private final RSyntaxTextArea textArea;
    private FindDialog findDialog;
    private ReplaceDialog replaceDialog;
    private AutoCompletion ac = null;
    private String syntax = "text/plain";
    private final LinkedList<String> allSyntax = new LinkedList<>();

    private SftpFileSystem fs;
    private String savePath;

    public EditorPanel() {
        this.setLayout(new BorderLayout());
        this.initSyntaxStyle();
        this.textArea = createTextArea();
        RTextScrollPane sp = new RTextScrollPane(textArea);
        sp.setBorder(null);
        this.add(sp, BorderLayout.CENTER);
        initSearchDialogs();
    }

    public EditorPanel(String savePath) {
        this();
        this.savePath = savePath;
        this.setTitle(savePath.substring(savePath.lastIndexOf("/") + 1));
        this.setTextArea(getTextFromPath());
    }

    public EditorPanel(String title, String tips) {
        this();
        this.setTitle(title);
        this.setTips(tips);
    }

    public EditorPanel(String title, String tips, FlatSVGIcon icon) {
        this(title, tips);
        this.setIcon(icon);
    }

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

        return textArea;
    }

    private Action createCopyAsStyledTextAction(String themeName) throws IOException {
        String resource = "/org/fife/ui/rsyntaxtextarea/themes/" + themeName + ".xml";
        Theme theme = Theme.load(this.getClass().getResourceAsStream(resource));
        return new RSyntaxTextAreaEditorKit.CopyCutAsStyledTextAction(themeName, theme, true);
    }

    private void initSearchDialogs() {
        log.debug("initSearchDialogs");
        findDialog = new FindDialog(MainFrame.embedEditor, this);
        replaceDialog = new ReplaceDialog(MainFrame.embedEditor, this);

        // This ties the properties of the two dialogs together (match case,
        // regex, etc.).
        SearchContext context = findDialog.getSearchContext();
        replaceDialog.setSearchContext(context);
    }

    private void initSyntaxStyle() {
        Class<SyntaxConstants> syntaxConstantsClass = SyntaxConstants.class;
        Field[] fields = syntaxConstantsClass.getDeclaredFields();
        for (Field field : fields) {
            try {
                String langStr = (String) field.get(syntaxConstantsClass);
                allSyntax.add(langStr);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    private String getTextFromPath() {
        StringBuilder str = new StringBuilder();
        if (getFs() == null) {
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
        }
        return str.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        // 自动设置编程语言风格
        autoSetSyntaxStyle(title);
    }

    public FlatSVGIcon getIcon() {
        return icon;
    }

    public void setIcon(FlatSVGIcon icon) {
        this.icon = icon;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public SftpFileSystem getFs() {
        return fs;
    }

    public void setFs(SftpFileSystem fs) {
        this.fs = fs;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public void setTextArea(String textArea) {
        this.textArea.setText(textArea);
    }

    public String getTextArea() {
        return this.textArea.getText();
    }

    public String getSyntax() {
        return syntax;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
        textArea.setSyntaxEditingStyle(syntax);
    }

    public void setLineWrap(boolean flag) {
        textArea.setLineWrap(flag);
    }

    private void autoSetSyntaxStyle(String title) {
        int index = title.lastIndexOf(".");
        if (index == -1) {
            if (title.equalsIgnoreCase("dockerfile")) {
                setSyntax("text/dockerfile");
//                String SYNTAX_STYLE_DOCKERFILE = "text/dockerfile";
            } else if (title.equalsIgnoreCase("hosts")) {
//                String SYNTAX_STYLE_HOSTS = "text/hosts";
                setSyntax("text/hosts");
            } else if (title.equalsIgnoreCase("makefile")) {
//                String SYNTAX_STYLE_MAKEFILE = "text/makefile";
                setSyntax("text/makefile");
            } else {
                // String SYNTAX_STYLE_UNIX_SHELL = "text/unix";
                // 默认：text/unix -> bash
                setSyntax("text/unix");
                icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/OpenTerminal_13x13.svg");
                ac = new AutoCompletion(new BashCompletionProvider());
                ac.install(textArea);
            }
        } else {
            // 造孽呀
            String ext = title.substring(title.lastIndexOf(".") + 1);
            switch (ext.toLowerCase()) {
//                String SYNTAX_STYLE_NONE = "text/plain";
                case "txt":
                    setSyntax("text/plain");
                    icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/file-text.svg");
                    break;
//                String SYNTAX_STYLE_ACTIONSCRIPT = "text/actionscript";
                case "as":
                    setSyntax("text/actionscript");
                    icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/file-as.svg");
                    break;
//                String SYNTAX_STYLE_ASSEMBLER_X86 = "text/asm";
                case "asm":
                    setSyntax("text/asm");
                    icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/asm.svg");
                    break;
//                String SYNTAX_STYLE_ASSEMBLER_6502 = "text/asm6502";
                case "asm6502":
                    setSyntax("text/asm6502");
                    break;
//                String SYNTAX_STYLE_BBCODE = "text/bbcode";
                case "bbcode":
                    setSyntax("text/bbcode");
                    break;
//                String SYNTAX_STYLE_C = "text/c";
                case "c":
                case "h":
                    setSyntax("text/c");
                    break;
//                String SYNTAX_STYLE_CLOJURE = "text/clojure";
                case "clj":
                    setSyntax("text/clojure");
                    break;
//                String SYNTAX_STYLE_CPLUSPLUS = "text/cpp";
                case "cpp":
                case "hpp":
                case "cc":
                    setSyntax("text/cpp");
                    break;
//                String SYNTAX_STYLE_CSHARP = "text/cs";
                case "cs":
                    setSyntax("text/cs");
                    break;
//                String SYNTAX_STYLE_CSS = "text/css";
                case "css":
                    setSyntax("text/css");
                    icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/file-css.svg");
                    break;
//                String SYNTAX_STYLE_CSV = "text/csv";
                case "csv":
                    setSyntax("text/csv");
                    break;
//                String SYNTAX_STYLE_D = "text/d";
                case "d":
                    setSyntax("text/d");
                    break;
//                String SYNTAX_STYLE_DART = "text/dart";
                case "dart":
                    setSyntax("text/dart");
                    break;
//                String SYNTAX_STYLE_DELPHI = "text/delphi";
                case "dpr":
                case "dfm":
                case "cfg":
                case "ddp ":
                case "pas":
                    setSyntax("text/delphi");
                    break;
//                String SYNTAX_STYLE_DTD = "text/dtd";
                case "dtd":
                    setSyntax("text/dtd");
                    icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/file-dtd.svg");
                    break;
//                String SYNTAX_STYLE_FORTRAN = "text/fortran";
                case "f":
                case "f90":
                case "f95":
                case "for":
                    setSyntax("text/fortran");
                    break;
//                String SYNTAX_STYLE_GO = "text/golang";
                case "go":
                    setSyntax("text/golang");
                    icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/file-go.svg");
                    break;
//                String SYNTAX_STYLE_GROOVY = "text/groovy";
                case "groovy":
                    setSyntax("text/groovy");
                    break;
                case "htaccess":
                    setSyntax("text/htaccess");
                    icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/file-htaccess.svg");
                    break;
//                String SYNTAX_STYLE_HTML = "text/html";
                case "html":
                case "ftl": // 模板文件
                    setSyntax("text/html");
                    icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/file-html.svg");
                    break;
//                String SYNTAX_STYLE_INI = "text/ini";
                case "ini":
                    setSyntax("text/ini");
                    break;
//                String SYNTAX_STYLE_JAVA = "text/java";
                case "java":
                    setSyntax("text/java");
                    icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/file-java.svg");
                    ac = new AutoCompletion(new JavaCompletionProvider());
                    ac.install(textArea);
                    break;
//                String SYNTAX_STYLE_JAVASCRIPT = "text/javascript";
                case "js":
                    setSyntax("text/javascript");
                    icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/file-js.svg");
                    break;
//                String SYNTAX_STYLE_JSON = "text/json";
                case "json":
                    setSyntax("text/json");
                    icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/file-json.svg");
                    break;
//                String SYNTAX_STYLE_JSON_WITH_COMMENTS = "text/jshintrc";
                case "jshintrc":
                    setSyntax("text/jshintrc");
                    break;
//                String SYNTAX_STYLE_JSP = "text/jsp";
                case "jsp":
                    setSyntax("text/jsp");
                    icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/file-jsp.svg");
                    break;
//                String SYNTAX_STYLE_KOTLIN = "text/kotlin";
                case "kt":
                    setSyntax("text/kotlin");
                    icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/file-kotlin.svg");
                    break;
//                String SYNTAX_STYLE_LATEX = "text/latex";
                case "latex":
                case "tex":
                case "sty":
                case "cls":
                case "bib":
                case "bst":
                    setSyntax("text/latex");
                    break;
//                String SYNTAX_STYLE_LESS = "text/less";
                case "less":
                    setSyntax("text/less");
                    break;
//                String SYNTAX_STYLE_LISP = "text/lisp";
                case "lisp":
                    setSyntax("text/lisp");
                    break;
//                String SYNTAX_STYLE_LUA = "text/lua";
                case "lua":
                    setSyntax("text/lua");
                    break;
//                String SYNTAX_STYLE_MARKDOWN = "text/markdown";
                case "md":
                    setSyntax("text/markdown");
                    icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/MarkdownPlugin.svg");
                    break;
//                String SYNTAX_STYLE_MXML = "text/mxml";
                case "mxml":
                    setSyntax("text/mxml");
                    break;
//                String SYNTAX_STYLE_NSIS = "text/nsis";
                case "nsis":
                    setSyntax("text/nsis");
                    break;
//                String SYNTAX_STYLE_PERL = "text/perl";
                case "pl":
                    setSyntax("text/perl");
                    break;
//                String SYNTAX_STYLE_PHP = "text/php";
                case "php":
                    setSyntax("text/php");
                    break;
//                String SYNTAX_STYLE_PROPERTIES_FILE = "text/properties";
                case "properties":
                    setSyntax("text/properties");
                    break;
//                String SYNTAX_STYLE_PYTHON = "text/python";
                case "py":
                    setSyntax("text/python");
                    icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/file-python.svg");
                    break;
//                String SYNTAX_STYLE_RUBY = "text/ruby";
                case "rb":
                    setSyntax("text/ruby");
                    break;
//                String SYNTAX_STYLE_SAS = "text/sas";
                case "sas":
                    setSyntax("text/sas");
                    break;
//                String SYNTAX_STYLE_SCALA = "text/scala";
                case "scala":
                    setSyntax("text/scala");
                    break;
//                String SYNTAX_STYLE_SQL = "text/sql";
                case "sql":
                    setSyntax("text/sql");
                    break;
//                String SYNTAX_STYLE_TCL = "text/tcl";
                case "tcl":
                    setSyntax("text/tcl");
                    break;
//                String SYNTAX_STYLE_TYPESCRIPT = "text/typescript";
                case "ts":
                    setSyntax("text/typescript");
                    icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/file-ts.svg");
                    break;
//                String SYNTAX_STYLE_UNIX_SHELL = "text/unix";
                case "sh":
                case "rc":
                case "bashrc":
                case "profile":
                case "viminfo":
                case "bash_logout":
                case "bash_history":
                case "bash_profile":
                    setSyntax("text/unix");
                    icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/OpenTerminal_13x13.svg");
                    ac = new AutoCompletion(new BashCompletionProvider());
                    ac.install(textArea);
                    break;
//                String SYNTAX_STYLE_VISUAL_BASIC = "text/vb";
                case "vb":
                    setSyntax("text/vb");
                    break;
//                String SYNTAX_STYLE_WINDOWS_BATCH = "text/bat";
                case "bat":
                    setSyntax("text/bat");
                    break;
//                String SYNTAX_STYLE_XML = "text/xml";
                case "xml":
                    setSyntax("text/xml");
                    icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/file-xml.svg");
                    break;
//                String SYNTAX_STYLE_YAML = "text/yaml";
                case "yaml":
                case "yml":
                    setSyntax("text/yaml");
                    icon = new FlatSVGIcon("com/g3g4x5x6/ui/icons/file-yaml.svg");
                    break;
            }
        }
        if (ac != null) {
            // TODO 快捷键与自动激活作为一个用户设置，二选一
//            ac.setAutoActivationEnabled(true);  // 找到唯一符合的关键字，将直接自动完成
            ac.setTriggerKey(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
            log.debug("setAutoActivationEnabled");
        }
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

    public FindDialog getFindDialog() {
        return findDialog;
    }

    public ReplaceDialog getReplaceDialog() {
        return replaceDialog;
    }
}
