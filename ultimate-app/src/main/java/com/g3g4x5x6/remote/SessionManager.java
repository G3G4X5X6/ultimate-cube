package com.g3g4x5x6.remote;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.ToolBar;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;


@Slf4j
public class SessionManager extends JPanel {
    private final ToolBar toolBar = new ToolBar();

    private JTable sessionTable;
    private DefaultTableModel tableModel;

    public SessionManager() {
        this.setLayout(new BorderLayout());

        // 初始化工具栏
        initToolBar();


        sessionTable = new JTable();
        tableModel = new DefaultTableModel() {
            // 不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        // TODO 添加<创建时间>
        String[] columnNames = {"会话名称", "协议", "地址", "端口", "登录用户", "认证类型"};
        tableModel.setColumnIdentifiers(columnNames);
        sessionTable.setAutoCreateRowSorter(true);
        sessionTable.setUpdateSelectionOnSort(true);

        // TODO
        // initData();

        JScrollPane tableScroll = new JScrollPane(sessionTable);
        tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JTextField.CENTER);
        sessionTable.getColumn("访问时间").setCellRenderer(centerRenderer);
        sessionTable.getColumn("会话名称").setCellRenderer(centerRenderer);
        sessionTable.getColumn("协议").setCellRenderer(centerRenderer);
        sessionTable.getColumn("地址").setCellRenderer(centerRenderer);
        sessionTable.getColumn("端口").setCellRenderer(centerRenderer);
        sessionTable.getColumn("登录用户").setCellRenderer(centerRenderer);
        sessionTable.getColumn("认证类型").setCellRenderer(centerRenderer);

        this.add(tableScroll, BorderLayout.CENTER);
    }

    private void initToolBar() {
        
        JButton refreshBtn = new JButton();
        refreshBtn.setIcon(new FlatSVGIcon("icons/refresh.svg"));
        refreshBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("ToolBar: 刷新");
            }
        });

        // 2.
        toolBar.add(refreshBtn);

        // 3.
        this.add(toolBar, BorderLayout.NORTH);
    }
}
