package com.g3g4x5x6.ui.panels.dashboard.quickstarter;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.g3g4x5x6.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *
 */
@Slf4j
public class RecentSessionsPane extends JPanel {
    private BorderLayout borderLayout;
    private JTabbedPane mainTabbedPane;

    private JScrollPane tableScroll;
    private JTable recentTable;
    private DefaultTableModel tableModel;
    private String[] columnNames = { "访问时间", "会话名称", "协议", "地址", "端口","登录用户", "认证类型"};

    public RecentSessionsPane(JTabbedPane mainTabbedPane) {
        this.borderLayout = new BorderLayout();
        this.setLayout(borderLayout);
        this.mainTabbedPane = mainTabbedPane;

        // 初始化表格
        initTable();

        // 初始化右键菜单
        initPopupMenu();

        recentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2){
                    openSession(recentTable.getSelectedRow());
                }
            }
        });
    }

    private void initTable(){
        recentTable = new JTable();
        recentTable.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                initData();
            }
        });
        tableModel = new DefaultTableModel(){
            // 不可编辑
            @Override
            public boolean isCellEditable(int row,int column){
                return false;
            }
        };
        tableModel.setColumnIdentifiers(columnNames);

        initData();

        tableScroll = new JScrollPane(recentTable);
        tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        DefaultTableCellRenderer  centerRenderer  =  new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JTextField.CENTER);
        recentTable.getColumn("访问时间").setCellRenderer(centerRenderer );
        recentTable.getColumn("会话名称").setCellRenderer(centerRenderer );
        recentTable.getColumn("协议").setCellRenderer(centerRenderer );
        recentTable.getColumn("地址").setCellRenderer(centerRenderer );
        recentTable.getColumn("端口").setCellRenderer(centerRenderer );
        recentTable.getColumn("登录用户").setCellRenderer(centerRenderer );
        recentTable.getColumn("认证类型").setCellRenderer(centerRenderer );

        this.add(tableScroll, BorderLayout.CENTER);
    }

    private void initData(){
        tableModel.setRowCount(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        File file = new File(ConfigUtil.getWorkPath() + "/sessions");
        if (file.exists()){
            for (File f : file.listFiles()){
                if (f.isFile()){
                    if (f.getName().startsWith("recent_ssh")){
                        Long lastModified = f.lastModified();
                        Date date = new Date(lastModified);
                        try {
                            String json = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
                            JSONObject jsonObject = JSON.parseObject(json);
                            tableModel.addRow(new String[]{
                                    simpleDateFormat.format(date),
                                    jsonObject.getString("sessionName"),
                                    jsonObject.getString("sessionProtocol"),
                                    jsonObject.getString("sessionAddress"),
                                    jsonObject.getString("sessionPort"),
                                    jsonObject.getString("sessionUser"),
                                    jsonObject.getString("sessionLoginType"),
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (tableModel.getRowCount() == 0){
                tableModel.addRow(
                        new String[]{"空", "空", "空", "空", "空", "空", "空", }
                );
            }
        }
        recentTable.setModel(tableModel);
    }

    private void initPopupMenu(){
        AbstractAction reopenAction = new AbstractAction("打开会话") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("再次打开会话");
                // TODO 默认打开 SSH 会话, 未来实现会话自动类型鉴别
                int[] indexs = recentTable.getSelectedRows();
                for (int index : indexs) {
                    openSession(index);
                }
            }
        };

        AbstractAction deleteMultiAction = new AbstractAction("清除全部") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String path = ConfigUtil.getWorkPath() + "/sessions";
                File file = new File(path);
                for (File f : file.listFiles()){
                    if (f.isFile()){
                        if (f.getName().startsWith("recent_ssh"))
                            f.delete();
                    }
                }
            }
        };

        AbstractAction deleteAllAction = new AbstractAction("清除选中") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String path = ConfigUtil.getWorkPath() + "/sessions";
                File file = new File(path);
                for (File f : file.listFiles()){
                    if (f.isFile()){
                        int[] indexs = recentTable.getSelectedRows();
                        for (int index : indexs) {
                            String protocol = (String) tableModel.getValueAt(recentTable.getSelectedRow(), 2);
                            String address = (String) tableModel.getValueAt(recentTable.getSelectedRow(), 3);
                            String port = (String) tableModel.getValueAt(recentTable.getSelectedRow(), 4);
                            String user = (String) tableModel.getValueAt(recentTable.getSelectedRow(), 5);
                            if (f.getAbsolutePath().contains(protocol.toLowerCase())
                                    && f.getAbsolutePath().contains(address.strip())
                                    && f.getAbsolutePath().contains(port.strip())
                                    && f.getAbsolutePath().contains(user.strip())
                            ){
                                f.delete();
                            }
                        }
                    }
                }
            }
        };

        // 会话树右键菜单
        JPopupMenu recentPopupMenu = new JPopupMenu();
        recentPopupMenu.add(reopenAction);
        recentPopupMenu.add(deleteAllAction);
        recentPopupMenu.add(deleteMultiAction);

        recentTable.setComponentPopupMenu(recentPopupMenu);
    }

    private void openSession(int index){
        String address = (String) tableModel.getValueAt(index, 3);
        String port = (String) tableModel.getValueAt(index, 4);
        String user = (String) tableModel.getValueAt(index, 5);

        File dir = new File(ConfigUtil.getWorkPath() + "/sessions/");
        if (dir.exists()){
            for (File file : dir.listFiles()){
                if (file.getName().contains(address) && file.getName().contains(port) && file.getName().contains(user)){
                    new Thread(() -> SessionUtil.openSshSession(file.getAbsolutePath())).start();
                }
            }
        }
    }
}
