package com.g3g4x5x6.ui.panels.dashboard.quickstarter;


import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;


/**
 *
 */
public class RecentSessionsPane extends JPanel {
    private BorderLayout borderLayout;
    private JTabbedPane mainTabbedPane;

    private JScrollPane tableScroll;
    private JTable recentTable;
    private DefaultTableModel tableModel;
    private String[] columnNames = { "访问时间", "协议", "地址", "端口","登录用户", "登录密码"};

    public RecentSessionsPane(JTabbedPane mainTabbedPane) {
        this.borderLayout = new BorderLayout();
        this.setLayout(borderLayout);
        this.mainTabbedPane = mainTabbedPane;

        // 初始化表格
        initTable();

        // 初始化右键菜单
        initPopupMenu();
    }

    private void initTable(){
        recentTable = new JTable();
        tableModel = new DefaultTableModel(){
            // 不可编辑
            @Override
            public boolean isCellEditable(int row,int column){
                return false;
            }
        };
        tableModel.setColumnIdentifiers(columnNames);

        tableModel.addRow(new String[]{"2021-8-16 23:01", "SSH", "192.168.83.137", "22", "root", "12345678"});
        tableModel.addRow(new String[]{"2021-7-15 20:01", "SSH", "192.168.83.13", "22", "kali", "kali"});
        recentTable.setModel(tableModel);

        tableScroll = new JScrollPane(recentTable);
        tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        DefaultTableCellRenderer  centerRenderer  =  new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JTextField.CENTER);
        recentTable.getColumn("访问时间").setCellRenderer(centerRenderer );
        recentTable.getColumn("协议").setCellRenderer(centerRenderer );
        recentTable.getColumn("地址").setCellRenderer(centerRenderer );
        recentTable.getColumn("端口").setCellRenderer(centerRenderer );
        recentTable.getColumn("登录用户").setCellRenderer(centerRenderer );
        recentTable.getColumn("登录密码").setCellRenderer(centerRenderer );

        this.add(tableScroll, BorderLayout.CENTER);
    }

    private void initPopupMenu(){
        AbstractAction reopenAction = new AbstractAction("重新打开") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("重新打开");
            }
        };
        // 会话树右键菜单
        JPopupMenu recentPopupMenu = new JPopupMenu();
        recentPopupMenu.add(reopenAction);

        recentTable.setComponentPopupMenu(recentPopupMenu);
    }
}
