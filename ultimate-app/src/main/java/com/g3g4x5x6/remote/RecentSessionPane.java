package com.g3g4x5x6.remote;


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
import java.util.List;
import java.util.Objects;


/**
 *
 */
@Slf4j
public class RecentSessionPane extends JPanel {
    private final ToolBar toolBar = new ToolBar();

    private final String[] columnNames = {"访问时间", "会话名称", "协议", "地址", "端口", "登录用户", "认证类型"};
    private JTable recentTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private WatchService watchService;

    public RecentSessionPane() {
        this.setLayout(new BorderLayout());

        // 初始化工具栏
        initToolBar();

        // 初始化表格
        initTable();

        // 启动会话访问变动监听
        monitorSessions();

        // 初始化右键菜单
        initPopupMenu();

        recentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSession(sorter.convertRowIndexToModel(recentTable.getSelectedRow()));
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

                try {
                    watchService.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);

                } finally {
                    log.info("刷新文件监控服务");
                    monitorSessions();
                }
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
        // 降序排序，但没啥效果
        sorter.setSortsOnUpdates(true);
        sorter.setSortKeys(java.util.Collections.singletonList(new RowSorter.SortKey(0, SortOrder.DESCENDING)));
        recentTable.setRowSorter(sorter);

        initData();

        JScrollPane tableScroll = new JScrollPane(recentTable);
        tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JTextField.CENTER);
        recentTable.getColumn("访问时间").setCellRenderer(centerRenderer);
        recentTable.getColumn("会话名称").setCellRenderer(centerRenderer);
        recentTable.getColumn("协议").setCellRenderer(centerRenderer);
        recentTable.getColumn("地址").setCellRenderer(centerRenderer);
        recentTable.getColumn("端口").setCellRenderer(centerRenderer);
        recentTable.getColumn("登录用户").setCellRenderer(centerRenderer);
        recentTable.getColumn("认证类型").setCellRenderer(centerRenderer);

        this.add(tableScroll, BorderLayout.CENTER);
    }

    @SneakyThrows
    private void initData() {
        tableModel.setRowCount(0);

        File file = new File(AppConfig.getWorkPath() + "/sessions");
        if (file.exists()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                if (f.isFile() && f.getName().startsWith("recent_")) {
                    BasicFileAttributes attrs = Files.readAttributes(Paths.get(f.getPath()), BasicFileAttributes.class);
                    FileTime time = attrs.lastModifiedTime();

                    LocalDateTime localDateTime = time.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

                    log.debug("time: " + localDateTime.format(DATE_FORMATTER));
                    try {
                        String json = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
                        JSONObject jsonObject = JSON.parseObject(json);
                        tableModel.addRow(new String[]{
                                localDateTime.format(DATE_FORMATTER),
                                jsonObject.getString("sessionName"),
                                jsonObject.getString("sessionProtocol"),
                                jsonObject.getString("sessionAddress"),
                                jsonObject.getString("sessionPort"),
                                jsonObject.getString("sessionUser"),
                                jsonObject.getString("sessionLoginType"),
                        });
                    } catch (IOException e) {
                        log.debug(e.getMessage());
                    }
                }
            }
            if (tableModel.getRowCount() == 0) {
                tableModel.addRow(
                        new String[]{"空", "空", "空", "空", "空", "空", "空",}
                );
            }
        }
        recentTable.setModel(tableModel);
    }

    @SneakyThrows
    private void monitorSessions() {
        // 需要监听的文件目录（只能监听目录）
        String path = Path.of(AppConfig.getWorkPath(), "/sessions").toString();

        watchService = FileSystems.getDefault().newWatchService();
        Path p = Paths.get(path);
        p.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_CREATE
        );

        Thread thread = getThread(path);
        thread.start();

        // 增加jvm关闭的钩子来关闭监听
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                watchService.close();
            } catch (Exception e) {
                // pass
            }
        }));
    }

    @NotNull
    private Thread getThread(String path) {
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    WatchKey watchKey = watchService.take();
                    List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                    for (WatchEvent<?> event : watchEvents) {
                        log.debug("[" + path + "/" + event.context() + "]文件发生了[" + event.kind() + "]事件");
                        // 刷新最近会话
                        initData();
                    }
                    watchKey.reset();
                }
            } catch (Exception e) {
//                e.printStackTrace();
                log.debug("关闭文件监控服务异常");
            }
        });
        thread.setDaemon(false);
        return thread;
    }

    private void initPopupMenu() {
        AbstractAction refreshAction = new AbstractAction("刷新") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("刷新");
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
                    openSession(sorter.convertRowIndexToModel(index));
                }
            }
        };

        AbstractAction deleteMultiAction = new AbstractAction("清除全部") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String path = AppConfig.getWorkPath() + "/sessions";
                File file = new File(path);
                for (File f : Objects.requireNonNull(file.listFiles())) {
                    if (f.isFile()) {
                        if (f.getName().startsWith("recent_ssh")) {
                            f.delete();
                        }
                    }
                }
            }
        };

        AbstractAction deleteAllAction = new AbstractAction("清除选中") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String path = AppConfig.getWorkPath() + "/sessions";
                File file = new File(path);
                for (File f : Objects.requireNonNull(file.listFiles())) {
                    if (f.isFile()) {
                        int[] indexs = recentTable.getSelectedRows();
                        for (int ignored : indexs) {
                            String protocol = (String) tableModel.getValueAt(recentTable.getSelectedRow(), 2);
                            String address = (String) tableModel.getValueAt(recentTable.getSelectedRow(), 3);
                            String port = (String) tableModel.getValueAt(recentTable.getSelectedRow(), 4);
                            String user = (String) tableModel.getValueAt(recentTable.getSelectedRow(), 5);
                            if (f.getAbsolutePath().contains(protocol.toLowerCase())
                                    && f.getAbsolutePath().contains(address.strip())
                                    && f.getAbsolutePath().contains(port.strip())
                                    && f.getAbsolutePath().contains(user.strip())
                            ) {
                                f.delete();
                            }
                        }
                    }
                }
            }
        };

        // 会话树右键菜单
        JPopupMenu recentPopupMenu = new JPopupMenu();
        recentPopupMenu.add(refreshAction);
        recentPopupMenu.addSeparator();
        recentPopupMenu.add(reopenAction);
        recentPopupMenu.addSeparator();
        recentPopupMenu.add(deleteAllAction);
        recentPopupMenu.add(deleteMultiAction);

        recentTable.setComponentPopupMenu(recentPopupMenu);
    }

    private void openSession(int index) {
        String address = (String) tableModel.getValueAt(index, 3);
        String port = (String) tableModel.getValueAt(index, 4);
        String user = (String) tableModel.getValueAt(index, 5);
        String auth = (String) tableModel.getValueAt(index, 6);

        File dir = new File(AppConfig.getWorkPath() + "/sessions/");
        if (dir.exists()) {
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (file.getName().contains(address) && file.getName().contains(port) && file.getName().contains(user) && file.getName().contains(auth)) {
                    log.debug("SessionPath: " + file.getAbsolutePath());
                    try {
                        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                        JSONObject jsonObject = JSON.parseObject(json);
                        String protocol = jsonObject.getString("sessionProtocol");
                        SessionOpenTool.OpenSessionByProtocol(file.getAbsolutePath(), protocol);
                    } catch (IOException e) {
                        log.debug(e.getMessage());
                    }
                }
            }
        }
    }
}
