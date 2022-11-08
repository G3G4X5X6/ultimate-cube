package com.g3g4x5x6.dashboard;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.g3g4x5x6.App;
import com.g3g4x5x6.AppConfig;
import com.g3g4x5x6.panel.editor.SearchDialog;
import com.g3g4x5x6.utils.DialogUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

import static com.g3g4x5x6.editor.util.EditorUtil.createTextArea;


/**
 * 备忘笔记
 */
@Slf4j
public class NotePane extends JPanel {
    private final String savePath = AppConfig.getWorkPath() + "/note";
    private String current_note_title = "";

    private final JToolBar toolBar;
    private final JPanel editorPane;

    private JTextField titleField;
    private JButton themeBtn;
    private RSyntaxTextArea textArea;

    public NotePane() {
        BorderLayout borderLayout = new BorderLayout();
        toolBar = new JToolBar();
        editorPane = new JPanel();

        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEADING);

        initToolBar();
        initEditorPane();

        this.setLayout(borderLayout);
        this.add(toolBar, BorderLayout.NORTH);
        this.add(editorPane, BorderLayout.CENTER);
    }

    private void initToolBar() {
        toolBar.setFloatable(false);
        FlatButton listBtn = new FlatButton();
        listBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        listBtn.setIcon(new FlatSVGIcon("icons/listFiles.svg"));
        listBtn.setToolTipText("笔记列表");
        listBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("打开备忘录列表");
                NoteDialog noteDialog = new NoteDialog();
                noteDialog.setVisible(true);
            }
        });

        FlatButton addBtn = new FlatButton();
        addBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        addBtn.setIcon(new FlatSVGIcon("icons/addFile.svg"));
        addBtn.setToolTipText("新建笔记");
        addBtn.addActionListener(new AbstractAction() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("新建笔记");
                // TODO 1. 判断是否为空，不为空则提示是否保存现有备忘内容，否则清空
                if (!textArea.getText().strip().equals("") || !current_note_title.equals("")) {
                    int ret = JOptionPane.showConfirmDialog(App.mainFrame, "是否保存现有备忘内容？", "提示", JOptionPane.YES_NO_CANCEL_OPTION);
                    log.debug(">>>>>>>>>>>>>>>>" + ret);
                    if (ret == 0) {
                        log.debug("保存现有备忘内容");
                        if (titleField.getText().strip().equalsIgnoreCase("")) {
                            titleField.setText(String.valueOf(UUID.randomUUID()));
                        }
                        insertOrUpdate();
                        // 先保存, 再清空
                        current_note_title = "";
                        titleField.setText("");
                        textArea.setText("");
                    } else if (ret == 1) {
                        // 不保存, 清空
                        current_note_title = "";
                        titleField.setText("");
                        textArea.setText("");
                    }
                    // 取消
                } else {
                    current_note_title = "";
                    titleField.setText("");
                    textArea.setText("");
                }
            }
        });

        FlatButton saveBtn = new FlatButton();
        saveBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        saveBtn.setIcon(new FlatSVGIcon("icons/Save.svg"));
        saveBtn.setToolTipText("保存笔记");
        saveBtn.addActionListener(new AbstractAction() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("保存笔记");
                insertOrUpdate();
            }
        });

        titleField = new JFormattedTextField("");
        titleField.setColumns(15);
        titleField.putClientProperty("JTextField.placeholderText", "备忘标题（保存文件名）");

        themeBtn = new JButton(new FlatSVGIcon("icons/eye.svg"));
        String[] theme_list = new String[]{"default", "dark", "default-alt", "druid", "eclipse", "idea", "monokai", "vs"};
        JPopupMenu lanuageMenu = new JPopupMenu();
        for (String item : theme_list) {
            JMenuItem temp = new JMenuItem(item);
            temp.addActionListener(new ActionListener() {
                @SneakyThrows
                @Override
                public void actionPerformed(ActionEvent e) {
                    Theme theme = Theme.load(this.getClass().getClassLoader().getResourceAsStream("org/fife/ui/rsyntaxtextarea/themes/" + temp.getText() + ".xml"));
                    theme.apply(textArea);
                    themeBtn.setToolTipText(temp.getText());
                }
            });
            lanuageMenu.add(temp);
        }
        themeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                lanuageMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        JButton searchBtn = new JButton(new FlatSVGIcon("icons/find.svg"));
        searchBtn.addActionListener(e -> {
            SearchDialog searchDialog = new SearchDialog(textArea);
            searchDialog.setVisible(true);
            searchDialog.setLocationRelativeTo(NotePane.this);
        });

        toolBar.add(listBtn);
        toolBar.add(addBtn);
        toolBar.add(saveBtn);
        toolBar.add(searchBtn);
        toolBar.addSeparator();
        toolBar.add(titleField);
        toolBar.addSeparator();
        toolBar.add(themeBtn);
    }

    private void initEditorPane() {
        editorPane.setLayout(new BorderLayout());
        textArea = createTextArea();
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_MARKDOWN);
        textArea.setCodeFoldingEnabled(true);
        RTextScrollPane sp = new RTextScrollPane(textArea);
        editorPane.add(sp);
    }

    @SneakyThrows
    private void insertOrUpdate() {
        String title = titleField.getText();
        String content = textArea.getText();
        if (current_note_title.equals("")) {
            // TODO 1. 新建保存
            log.debug("插入新备忘");
            current_note_title = title;
            Files.write(Path.of(savePath + "/" + title + ".md"), content.getBytes(StandardCharsets.UTF_8));
        } else {
            if (!current_note_title.equalsIgnoreCase(titleField.getText())) {
                Files.move(Path.of(savePath + "/" + current_note_title + ".md"), Path.of(savePath + "/" + current_note_title + ".backup.md"));
                current_note_title = title;
                log.debug("备份旧笔记");
            }
            Files.write(Path.of(savePath + "/" + title + ".md"), content.getBytes(StandardCharsets.UTF_8));
            log.debug("更新备忘");
        }
    }

    private void openNote(String path) {
        File file = new File(path);
        titleField.setText(file.getName().replace(".md", ""));
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            StringBuilder text = new StringBuilder();
            while ((line = reader.readLine()) != null)
                text.append(line).append("\n");
            textArea.setText(text.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class NoteDialog extends JDialog {
        private JTable noteTable;
        private DefaultTableModel tableModel;
        private final String[] columnNames = {"ID", "标题"};

        public NoteDialog() {
            super(App.mainFrame);
            this.setLayout(new BorderLayout());
            this.setPreferredSize(new Dimension(600, 270));
            this.setSize(new Dimension(600, 270));
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
                        String filePath = "";
                        if (row != -1) {
                            filePath = (String) tableModel.getValueAt(row, 1);
                            log.debug("row: " + filePath);
                        }
                        if (current_note_title.equals("")) {
                            if (textArea.getText().strip().equals("")) {
                                // 直接打开
                                log.debug("openNote DIR");
                                openNote(filePath);
                                dispose();
                            } else {
                                // 询问是否保存现有备忘笔记
                                log.debug("Tips");
                                if (DialogUtil.yesOrNo(App.mainFrame, "是否保存已有备忘笔记？") == 0) {
                                    insertOrUpdate();
                                    openNote(filePath);
                                    dispose();
                                }

                            }
                        } else {
                            if (DialogUtil.yesOrNo(App.mainFrame, "是否保存已有备忘笔记？") == 0) {
                                insertOrUpdate();
                                openNote(filePath);
                                dispose();
                            } else {
                                openNote(filePath);
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
            int row = 1;
            File noteDir = new File(savePath);
            for (File file : Objects.requireNonNull(noteDir.listFiles())) {
                tableModel.addRow(new String[]{String.valueOf(row), file.getAbsolutePath()});
                row += 1;
            }
            noteTable.setModel(tableModel);
        }

        private void initControlButton() {
            JPanel controlPane = new JPanel();
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment(FlowLayout.LEFT);
            controlPane.setLayout(flowLayout);
            JButton delButton = new JButton("删除笔记");
            JButton closeButton = new JButton("关闭窗口");
            controlPane.add(delButton);
            controlPane.add(closeButton);
            this.add(controlPane, BorderLayout.SOUTH);

            delButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (DialogUtil.yesOrNo(App.mainFrame, "是否删除选中备忘记录？") == 0) {
                        int[] rows = noteTable.getSelectedRows();
                        for (int row : rows) {
                            if (row != -1) {
                                String filePath = (String) tableModel.getValueAt(row, 1);
                                File delFile = new File(filePath);
                                log.debug("Selected note: " + filePath);
                                if (delFile.delete()) {
                                    log.info("删除备忘记录成功");
                                } else {
                                    log.info("删除备忘记录失败");
                                }
                                // 删除的笔记如果正在编辑，需重置 current_note_title = ""
                                if (delFile.getName().equals(current_note_title + ".md")) {
                                    current_note_title = "";
                                }
                            }
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
    }
}
