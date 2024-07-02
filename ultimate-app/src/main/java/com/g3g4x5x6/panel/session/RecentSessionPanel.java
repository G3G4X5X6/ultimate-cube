package com.g3g4x5x6.panel.session;


import cn.hutool.core.swing.clipboard.ClipboardUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.AppConfig;
import com.g3g4x5x6.remote.utils.session.SessionOpenTool;
import com.g3g4x5x6.ui.ToolBar;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 *
 */
@Slf4j
public class RecentSessionPanel extends JPanel {
    private final ToolBar toolBar = new ToolBar();

    private final String[] columnNames = {"访问时间", "会话名称", "协议", "地址", "端口", "登录用户", "认证类型", "会话保存路径"};
    private JTable recentTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private WatchService watchService;

    public RecentSessionPanel() {
        this.setLayout(new BorderLayout());

        // 初始化工具栏
        initToolBar();

        // 初始化表格
        initTable();

        // 初始化右键菜单
        initPopupMenu();

        recentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int modelIndex = sorter.convertRowIndexToModel(recentTable.getSelectedRow());
                    String sessionPath = tableModel.getValueAt(modelIndex, 7).toString();
                    String sessionProtocol = tableModel.getValueAt(modelIndex, 2).toString();
                    SessionOpenTool.OpenSessionByProtocol(sessionPath, sessionProtocol);
                    initData();
                }
            }
        });
    }

    private void initToolBar() {
        JButton recentBtn = new JButton("最近会话");
        recentBtn.setSelected(true);
        recentBtn.setEnabled(false);

        JButton refreshBtn = new JButton();
        refreshBtn.setIcon(new FlatSVGIcon("icons/refresh.svg"));
        refreshBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("ToolBar: 刷新");
                initData();
            }
        });

        toolBar.add(recentBtn);
        toolBar.add(refreshBtn);

        this.add(toolBar, BorderLayout.NORTH);
    }

    private void initTable() {
        recentTable = new JTable();
        tableModel = new DefaultTableModel() {
            // 不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.setColumnIdentifiers(columnNames);
        // 搜索功能
        sorter = new TableRowSorter<>(tableModel);
        sorter.setSortsOnUpdates(true);
        recentTable.setRowSorter(sorter);

        initData();

        // 移除并隐藏
        TableColumn idColumn = recentTable.getColumnModel().getColumn(7);
        recentTable.removeColumn(idColumn);

        JScrollPane tableScroll = new JScrollPane(recentTable);
        tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // 设置列宽
        TableColumn changedTimeColumn = recentTable.getColumnModel().getColumn(0);
        changedTimeColumn.setPreferredWidth(120); // 设置第一列的宽度
        changedTimeColumn.setMinWidth(120);
        TableColumn sessionNameColumn = recentTable.getColumnModel().getColumn(1);
        sessionNameColumn.setPreferredWidth(150); // 设置第一列的宽度
        sessionNameColumn.setMinWidth(150);

        DefaultTableCellRenderer leadingRenderer = new DefaultTableCellRenderer();
        leadingRenderer.setHorizontalAlignment(JTextField.LEADING);
        recentTable.getColumn("会话名称").setCellRenderer(leadingRenderer);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JTextField.CENTER);
        recentTable.getColumn("访问时间").setCellRenderer(centerRenderer);
        recentTable.getColumn("协议").setCellRenderer(centerRenderer);
//        recentTable.getColumn("地址").setCellRenderer(centerRenderer);
        recentTable.getColumn("端口").setCellRenderer(centerRenderer);
        recentTable.getColumn("登录用户").setCellRenderer(centerRenderer);
        recentTable.getColumn("认证类型").setCellRenderer(centerRenderer);

        this.add(tableScroll, BorderLayout.CENTER);
    }

    @SneakyThrows
    private void initData() {
        tableModel.setRowCount(0);

        File sessionDir = new File(AppConfig.getSessionPath());
        for (File f : Objects.requireNonNull(sessionDir.listFiles())) {
            if (f.isFile() && f.getName().startsWith("recent_")) {
                BasicFileAttributes attrs = Files.readAttributes(Paths.get(f.getPath()), BasicFileAttributes.class);
                FileTime time = attrs.lastModifiedTime();

                LocalDateTime localDateTime = time.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

                try {
                    String json = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
                    JSONObject jsonObject = JSON.parseObject(json);
                    tableModel.addRow(new String[]{localDateTime.format(DATE_FORMATTER), jsonObject.getString("sessionName"), jsonObject.getString("sessionProtocol"), jsonObject.getString("sessionAddress"), jsonObject.getString("sessionPort"), jsonObject.getString("sessionUser"), jsonObject.getString("sessionLoginType"), f.getAbsolutePath()});
                } catch (IOException e) {
                    log.debug(e.getMessage());
                }
            }
        }
        if (tableModel.getRowCount() == 0) {
            tableModel.addRow(new String[]{"空", "空", "空", "空", "空", "空", "空", "空"});
        }

        recentTable.setModel(tableModel);
        sorter.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.DESCENDING)));
    }


    private void initPopupMenu() {
        AbstractAction refreshAction = new AbstractAction("刷新面板") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("刷新最近会话面板");
                initData();
            }
        };

        AbstractAction reopenAction = new AbstractAction("打开会话") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("再次打开会话");
                // TODO 默认打开 SSH 会话, 未来实现会话自动类型鉴别
                int[] indexs = recentTable.getSelectedRows();

                for (int index : indexs) {
                    int modelIndex = sorter.convertRowIndexToModel(index);
                    String sessionPath = recentTable.getValueAt(modelIndex, 7).toString();
                    String sessionProtocol = recentTable.getValueAt(modelIndex, 2).toString();
                    SessionOpenTool.OpenSessionByProtocol(sessionPath, sessionProtocol);
                }
                initData();
            }
        };

        AbstractAction copyAction = new AbstractAction("复制会话路径") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("复制会话路径");
                String sessionFilePath = (String) tableModel.getValueAt(recentTable.convertRowIndexToModel(recentTable.getSelectedRow()), 7);
                ClipboardUtil.setStr(sessionFilePath);
            }
        };

        AbstractAction deleteMultiAction = new AbstractAction("删除全部") {
            @Override
            public void actionPerformed(ActionEvent e) {
                File sessionDir = new File(AppConfig.getSessionPath());
                for (File f : Objects.requireNonNull(sessionDir.listFiles())) {
                    if (f.isFile()) {
                        if (f.getName().startsWith("recent_")) {
                            f.delete();
                        }
                    }
                }
                initData();
            }
        };

        AbstractAction deleteAllAction = new AbstractAction("删除选中") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] indexes = recentTable.getSelectedRows();
                for (int index : indexes) {
                    int modelRowIndex = recentTable.convertRowIndexToModel(index);
                    String sessionPath = (String) tableModel.getValueAt(modelRowIndex, 7);

                    File sessionFile = new File(sessionPath);
                    if (sessionFile.getName().startsWith("recent_")) {
                        sessionFile.delete();
                    }
                }
                initData();
            }
        };

        // 会话树右键菜单
        JPopupMenu recentPopupMenu = new JPopupMenu();
        recentPopupMenu.add(refreshAction);
        recentPopupMenu.addSeparator();
        recentPopupMenu.add(reopenAction);
        recentPopupMenu.add(copyAction);
        recentPopupMenu.addSeparator();
        recentPopupMenu.add(deleteAllAction);
        recentPopupMenu.add(deleteMultiAction);

        recentTable.setComponentPopupMenu(recentPopupMenu);
    }

    private void openSession(int index) {
        SessionOpenTool.OpenSessionByProtocol(Objects.requireNonNull(getSessionObject(index)).getString("sessionFilePath"), Objects.requireNonNull(getSessionObject(index)).getString("sessionProtocol"));
    }

    private JSONObject getSessionObject(int index) {
        String sessionPath = (String) tableModel.getValueAt(index, 7);
        JSONObject jsonObject;
        try {
            jsonObject = JSONObject.parseObject(Files.readString(Path.of(sessionPath)));
            jsonObject.put("sessionFilePath", sessionPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }
}
