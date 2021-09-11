package com.g3g4x5x6.ui.panels;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.g3g4x5x6.App;
import com.g3g4x5x6.ui.panels.dashboard.NotePane;
import com.g3g4x5x6.utils.ConfigUtil;
import com.g3g4x5x6.utils.DbUtil;
import com.g3g4x5x6.utils.DialogUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Date;


@Slf4j
public class EditorPane extends JPanel {
    private BorderLayout borderLayout;
    private JToolBar toolBar;
    private JPanel editorPane;
    private JPanel statusBar;

    // 新建 note
    private String current_note_id = "";

    // TODO toolBar
    private JTextField titleField;

    // TODO editorPane
    private RSyntaxTextArea textArea;
    private RTextScrollPane sp;

    // TODO statusBar
    private JTextField searchField;
    private JCheckBox regexCB;
    private JCheckBox matchCaseCB;

    public EditorPane() {
        borderLayout = new BorderLayout();
        toolBar = new JToolBar();
        editorPane = new JPanel();
        statusBar = new JPanel();

        initToolBar();
        initEditorPane();
        initStatusBar();

        this.setLayout(borderLayout);
        this.add(toolBar, BorderLayout.NORTH);
        this.add(editorPane, BorderLayout.CENTER);
        this.add(statusBar, BorderLayout.SOUTH);
    }

    private void initToolBar() {
        toolBar.setFloatable(false);

        FlatButton listBtn = new FlatButton();
        listBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        listBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/listFiles.svg"));
        listBtn.setToolTipText("笔记列表");
        listBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("打开备忘录列表");
            }
        });

        FlatButton addBtn = new FlatButton();
        addBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        addBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/addFile.svg"));
        addBtn.setToolTipText("新建笔记");
        addBtn.addActionListener(new AbstractAction() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("新建笔记");
                // TODO 1. 判断是否为空，不为空则提示是否保存现有备忘内容，否则清空
                if (!textArea.getText().strip().equals("") || !current_note_id.equals("")) {
                    int ret = JOptionPane.showConfirmDialog(App.mainFrame, "是否保存现有备忘内容？", "提示", JOptionPane.YES_NO_CANCEL_OPTION);
                    log.debug(">>>>>>>>>>>>>>>>" + ret);
                    if (ret == 0) {
                        log.debug("保存现有备忘内容");
                        insertOrUpdate();
                        // 先保存, 再清空
                        current_note_id = "";
                        titleField.setText("");
                        textArea.setText("");
                    } else if (ret == 1) {
                        // 不保存, 清空
                        current_note_id = "";
                        titleField.setText("");
                        textArea.setText("");
                    }
                    // 取消
                }else{
                    // TODO fixed
                    current_note_id = "";
                    titleField.setText("");
                    textArea.setText("");
                }
            }
        });

        FlatButton saveBtn = new FlatButton();
        saveBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        saveBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/Save.svg"));
        saveBtn.setToolTipText("保存笔记");
        saveBtn.addActionListener(new AbstractAction() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!textArea.getText().strip().equals("") || !titleField.getText().strip().equals(""))
                    insertOrUpdate();
            }
        });

        FlatButton importBtn = new FlatButton();
        importBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        importBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/import.svg"));
        importBtn.setToolTipText("导入笔记");
        importBtn.addActionListener(new AbstractAction() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("导入笔记");
                if (current_note_id.equals("")) {
                    if (textArea.getText().strip().equals("")) {
                        // 直接打开
                        log.debug("openNote DIR");
                        importFile();
                    } else {
                        // 询问是否保存现有备忘笔记
                        log.debug("Tips");
                        if (DialogUtil.yesOrNo(App.mainFrame, "是否保存已有备忘笔记？") == 0) {
                            insertOrUpdate();
                            current_note_id = "";
                            importFile();
                        }

                    }
                } else {
                    if (DialogUtil.yesOrNo(App.mainFrame, "是否保存已有备忘笔记？") == 0) {
                        insertOrUpdate();
                    }
                    current_note_id = "";
                    importFile();
                }
            }
        });

        FlatButton exportBtn = new FlatButton();
        exportBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        exportBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/export.svg"));
        exportBtn.setToolTipText("导出笔记");
        exportBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("导出笔记");
                // 创建一个默认的文件选取器
                JFileChooser fileChooser = new JFileChooser();
                // 设置默认显示的文件夹为当前文件夹
                fileChooser.setCurrentDirectory(new File(ConfigUtil.getWorkPath() + "/note"));
                // 设置文件选择的模式（只选文件、只选文件夹、文件和文件均可选）
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
                int result = fileChooser.showOpenDialog(App.mainFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    // 如果点击了"确定", 则获取选择的文件路径
                    File file = fileChooser.getSelectedFile();
                    String fileName = String.valueOf(new Date().getTime());
                    if (!titleField.getText().strip().equals("")) {
                        fileName = titleField.getText();
                    }
                    try (BufferedWriter writer = Files.newBufferedWriter(Path.of(file.getAbsolutePath() + "/" + fileName + ".md"), StandardCharsets.UTF_8)) {
                        writer.write(textArea.getText());
                        writer.flush();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });

        titleField = new JFormattedTextField("");
        titleField.setColumns(15);
        titleField.putClientProperty("JTextField.placeholderText", "备忘标题");

        toolBar.add(listBtn);
        toolBar.add(addBtn);
        toolBar.add(saveBtn);
        toolBar.add(importBtn);
        toolBar.add(exportBtn);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(titleField);
    }

    private void initEditorPane() {
        editorPane.setLayout(new BorderLayout());
        textArea = createTextArea();
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_MARKDOWN);
        textArea.setCodeFoldingEnabled(true);
        sp = new RTextScrollPane(textArea);
        editorPane.add(sp);
    }

    private void initStatusBar() {
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        statusBar.setLayout(flowLayout);

        statusBar.add(new JLabel("<html><font color='green'><strong>MARKDOWN</strong></font></html>"));
        statusBar.add(Box.createGlue());
        // 主题设置
        String[] theme_list = new String[]{"default", "dark", "default-alt", "druid", "eclipse", "idea", "monokai", "vs"};
        JComboBox<String> themeComboBxo = new JComboBox<>(theme_list);
        // 添加条目选中状态改变的监听器
        themeComboBxo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                // 只处理选中的状态
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    log.debug("选中: " + themeComboBxo.getSelectedIndex() + " = " + themeComboBxo.getSelectedItem());
                    try {
                        Theme theme = Theme.load(this.getClass().getClassLoader().getResourceAsStream("org/fife/ui/rsyntaxtextarea/themes/" + themeComboBxo.getSelectedItem() + ".xml"));
                        theme.apply(textArea);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
        statusBar.add(themeComboBxo);
        statusBar.add(Box.createGlue());
        statusBar.add(Box.createGlue());

        // Create a toolbar with searching options.
        searchField = new JTextField(30);
        statusBar.add(searchField);
        final FlatButton nextButton = new FlatButton();
        nextButton.setButtonType(FlatButton.ButtonType.toolBarButton);
        nextButton.setIcon(new FlatSVGIcon("icons/back.svg"));
        nextButton.setActionCommand("FindNext");
        nextButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // "FindNext" => search forward, "FindPrev" => search backward
                String command = e.getActionCommand();
                boolean forward = "FindNext".equals(command);

                // Create an object defining our search parameters.
                SearchContext context = new SearchContext();
                String text = searchField.getText();
                if (text.length() == 0) {
                    return;
                }
                context.setSearchFor(text);
                context.setMatchCase(matchCaseCB.isSelected());
                context.setRegularExpression(regexCB.isSelected());
                context.setSearchForward(forward);
                context.setWholeWord(false);

                boolean found = SearchEngine.find(textArea, context).wasFound();
                if (!found) {
                    JOptionPane.showMessageDialog(App.mainFrame, "Text not found");
                }
            }
        });
        statusBar.add(nextButton);
        searchField.addActionListener(e -> nextButton.doClick(0));
        FlatButton prevButton = new FlatButton();
        prevButton.setButtonType(FlatButton.ButtonType.toolBarButton);
        prevButton.setIcon(new FlatSVGIcon("icons/forward.svg"));
        prevButton.setActionCommand("FindPrev");
        prevButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // "FindNext" => search forward, "FindPrev" => search backward
                String command = e.getActionCommand();
                boolean forward = "FindNext".equals(command);

                // Create an object defining our search parameters.
                SearchContext context = new SearchContext();
                String text = searchField.getText();
                if (text.length() == 0) {
                    return;
                }
                context.setSearchFor(text);
                context.setMatchCase(matchCaseCB.isSelected());
                context.setRegularExpression(regexCB.isSelected());
                context.setSearchForward(forward);
                context.setWholeWord(false);

                boolean found = SearchEngine.find(textArea, context).wasFound();
                if (!found) {
                    JOptionPane.showMessageDialog(App.mainFrame, "Text not found");
                }
            }
        });
        statusBar.add(prevButton);
        regexCB = new JCheckBox("Regex");
        statusBar.add(regexCB);
        matchCaseCB = new JCheckBox("Match Case");
        statusBar.add(matchCaseCB);
    }

    private RSyntaxTextArea createTextArea() {

        RSyntaxTextArea textArea = new RSyntaxTextArea();
        textArea.setCaretPosition(0);
        textArea.requestFocusInWindow();
        textArea.setMarkOccurrences(true);
        textArea.setCodeFoldingEnabled(true);
        textArea.setClearWhitespaceLinesEnabled(false);

        InputMap im = textArea.getInputMap();
        ActionMap am = textArea.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), "decreaseFontSize");
        am.put("decreaseFontSize", new RSyntaxTextAreaEditorKit.DecreaseFontSizeAction());
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), "increaseFontSize");
        am.put("increaseFontSize", new RSyntaxTextAreaEditorKit.IncreaseFontSizeAction());

        int ctrlShift = InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK;
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, ctrlShift), "copyAsStyledText");
        am.put("copyAsStyledText", new RSyntaxTextAreaEditorKit.CopyAsStyledTextAction());

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
        return new RSyntaxTextAreaEditorKit.CopyAsStyledTextAction(themeName, theme);
    }

    private void insertOrUpdate() throws UnsupportedEncodingException {
        String title = titleField.getText();
        String content = Base64.getEncoder().encodeToString(textArea.getText().getBytes("utf-8"));
        String createTime = String.valueOf(new Date().getTime());
        String modifyTime = createTime;
        String comment = "暂无";
        if (current_note_id.equals("")) {
            // TODO 1. 新建保存
            String sql = "INSERT INTO note VALUES(null, '" + title + "', '" + content + "', '" + createTime + "', '" + modifyTime + "', '" + comment + "');";
            insertText(sql, createTime);
        } else {
            // TODO 2. 更新保存
            modifyTime = String.valueOf(new Date().getTime());
            String sql = "UPDATE note SET title='" + title + "', content='" + content + "', modify_time='" + modifyTime + "', comment='" + comment + "' WHERE id=" + current_note_id + ";";
            updateText(sql);
        }
    }

    private void insertText(String sql, String createTime) {
        log.debug("插入新备忘");
        if (DbUtil.insert(sql, "备忘笔记插入失败") == 1) {
            log.debug("备忘笔记插入成功");
            // 给 current_id 赋值
            String sqlForId = "SELECT * FROM note where create_time=" + createTime;
            try {
                Connection connection = DbUtil.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sqlForId);
                while (resultSet.next()) {
                    current_note_id = resultSet.getString("id");
                }
                DbUtil.close(statement);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
//            log.debug("备忘笔记插入失败");
        }
    }

    private void updateText(String sql) {
        log.debug("更新当前备忘");
        if (DbUtil.update(sql, "备忘笔记更新失败") == 1) {
            log.debug("备忘笔记更新成功");
        } else {
            log.debug("备忘笔记更新失败");
        }
    }

    private void openNote(String id) {
        current_note_id = id;
        String sqlForId = "SELECT * FROM note where id=" + id;
        try {
            Connection connection = DbUtil.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlForId);
            while (resultSet.next()) {
                titleField.setText(resultSet.getString("title"));
                textArea.setText(new String(Base64.getDecoder().decode(resultSet.getString("content")), "utf-8"));
            }
            DbUtil.close(statement);
        } catch (SQLException | UnsupportedEncodingException throwables) {
            throwables.printStackTrace();
        }

    }

    private void importFile() {
        // 创建一个默认的文件选取器
        JFileChooser fileChooser = new JFileChooser();
        // 设置默认显示的文件夹为当前文件夹
        fileChooser.setCurrentDirectory(new File(ConfigUtil.getWorkPath() + "/note"));
        // 设置文件选择的模式（只选文件、只选文件夹、文件和文件均可选）
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
        int result = fileChooser.showOpenDialog(App.mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            // 如果点击了"确定", 则获取选择的文件路径
            File file = fileChooser.getSelectedFile();
            String fileName = file.getName();
            StringBuffer sbf = new StringBuffer();
            try (BufferedReader reader = Files.newBufferedReader(Path.of(file.getAbsolutePath()), StandardCharsets.UTF_8)) {
                String tempStr;
                while ((tempStr = reader.readLine()) != null) {
                    sbf.append(tempStr + "\n");
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            int i = fileName.lastIndexOf('.');
            if (i > 0) {
                fileName = fileName.substring(0, i);
            }
            titleField.setText(fileName);
            textArea.setText(sbf.toString());
        }
    }

    private class NoteDialog extends JDialog {
        private JTable noteTable;
        private DefaultTableModel tableModel;
        private String[] columnNames = {"ID", "标题"};

        private JButton delButton;
        private JButton closeButton;

        public NoteDialog() {
            super(App.mainFrame);
            this.setLayout(new BorderLayout());
            this.setPreferredSize(new Dimension(300, 350));
            this.setSize(new Dimension(300, 350));
            this.setLocationRelativeTo(App.mainFrame);
            this.setModal(true);
            this.setTitle("备忘笔记");

            initEnableOption();

            initNoteListTable();

            initControlButton();

            noteTable.addMouseListener(new MouseAdapter() {
                @SneakyThrows
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        log.debug("双击打开备忘笔记");
                        // 双击动作: 打开选中笔记
                        int row = noteTable.getSelectedRow();
                        String noteId = "";
                        if (row != -1) {
                            noteId = (String) tableModel.getValueAt(row, 0);
                            log.debug("row: " + noteId);
                        }
                        if (current_note_id.equals("")) {
                            if (textArea.getText().strip().equals("")) {
                                // 直接打开
                                log.debug("openNote DIR");
                                openNote(noteId);
                                dispose();
                            } else {
                                // 询问是否保存现有备忘笔记
                                log.debug("Tips");
                                if (DialogUtil.yesOrNo(App.mainFrame, "是否保存已有备忘笔记？") == 0) {
                                    insertOrUpdate();
                                    openNote(noteId);
                                    dispose();
                                }

                            }
                        } else {
                            if (DialogUtil.yesOrNo(App.mainFrame, "是否保存已有备忘笔记？") == 0) {
                                insertOrUpdate();
                                openNote(noteId);
                                dispose();
                            } else {
                                openNote(noteId);
                                dispose();
                            }
                        }
                    }
                }
            });
        }

        private void initEnableOption() {
            // TODO Enable Option
            JPanel enablePanel = new JPanel();
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment(FlowLayout.LEFT);
            enablePanel.setLayout(flowLayout);

            JLabel tips = new JLabel("双击打开选中备忘笔记");
            tips.setEnabled(false);
            enablePanel.add(tips);

            this.add(enablePanel, BorderLayout.NORTH);
        }

        private void initNoteListTable() {
            noteTable = new JTable();
            tableModel = new DefaultTableModel() {
                // 不可编辑
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            tableModel.setColumnIdentifiers(columnNames);

            initTable();

            noteTable.getColumnModel().getColumn(0).setMaxWidth(50);
            noteTable.getColumnModel().getColumn(0).setMinWidth(50);

            JScrollPane tableScroll = new JScrollPane(noteTable);
            tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JTextField.CENTER);
            noteTable.getColumn("ID").setCellRenderer(centerRenderer);
            this.add(tableScroll, BorderLayout.CENTER);
        }

        private void initTable() {
            // 获取主题数据
            tableModel.setRowCount(0);
            int row = 0;
            try {
                Connection connection = DbUtil.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * From note");

                while (resultSet.next()) {
                    tableModel.addRow(new String[]{
                            resultSet.getString("id"),
                            resultSet.getString("id").equals(current_note_id) ?
                                    "<html><strong><font color='red'>" + getCurrentNote() + "</font></strong></html>" :
                                    resultSet.getString("title")
                    });
                    row = tableModel.getRowCount() - 1;
                }
                DbUtil.close(statement, resultSet);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            noteTable.setModel(tableModel);
        }

        private void initControlButton() {
            JPanel controlPane = new JPanel();
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment(FlowLayout.LEFT);
            controlPane.setLayout(flowLayout);
            delButton = new JButton("删除笔记");
            closeButton = new JButton("关闭窗口");
            controlPane.add(delButton);
            controlPane.add(closeButton);
            this.add(controlPane, BorderLayout.SOUTH);

            delButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (DialogUtil.yesOrNo(App.mainFrame, "是否删除选中备忘记录？") == 0) {
                        int[] rows = noteTable.getSelectedRows();
                        int ret = 0;
                        for (int row : rows) {
                            if (row != -1) {
                                String noteId = (String) tableModel.getValueAt(row, 0);
                                log.debug("Selected note: " + noteId);
                                String sql = "DELETE FROM note WHERE id=" + noteId;
                                try {
                                    Connection connection = DbUtil.getConnection();
                                    Statement statement = connection.createStatement();
                                    ret = statement.executeUpdate(sql);
                                    DbUtil.close(statement);
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }
                                // 删除的笔记如果正在编辑，需重置 current_note_id = ""
                                if (noteId.equals(current_note_id)){
                                    current_note_id = "";
                                }
                            }
                        }
                        if (ret == 1) {
                            DialogUtil.info("删除备忘记录成功");
                        }
                        initTable();
                    }
                }
            });

            closeButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
        }

        private String getCurrentNote() {
            String themeName = "";
            if (!current_note_id.equals("")) {
                try {
                    Connection connection = DbUtil.getConnection();
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * From note WHERE id =" + current_note_id);
                    while (resultSet.next()) {
                        themeName = resultSet.getString("title");
                    }
                    DbUtil.close(statement, resultSet);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            return themeName;
        }
    }
}
