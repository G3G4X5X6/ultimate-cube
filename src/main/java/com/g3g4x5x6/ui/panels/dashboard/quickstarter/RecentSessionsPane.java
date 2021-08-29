package com.g3g4x5x6.ui.panels.dashboard.quickstarter;


import com.g3g4x5x6.ui.CloseButton;
import com.g3g4x5x6.ui.TabbedTitlePane;
import com.g3g4x5x6.ui.panels.ssh.SshTabbedPane;
import com.g3g4x5x6.utils.DbUtil;
import com.g3g4x5x6.utils.DialogUtil;
import com.g3g4x5x6.utils.SshUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        try {
            Connection connection = DbUtil.getConnection();
            Statement statement = connection.createStatement();

            String sql = "SELECT * FROM session ORDER BY access_time DESC ";

            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                tableModel.addRow(new String[]{
                        simpleDateFormat.format(new Date(Long.parseLong(resultSet.getString("access_time")))),
                        resultSet.getString("session_name"),
                        resultSet.getString("protocol"),
                        resultSet.getString("address"),
                        resultSet.getString("port"),
                        resultSet.getString("username"),
                        resultSet.getString("auth_type")
                });
            }

            DbUtil.close(statement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
                    String session = (String) tableModel.getValueAt(index, 1);
                    String protocol = (String) tableModel.getValueAt(index, 2);
                    String address = (String) tableModel.getValueAt(index, 3);
                    String port = (String) tableModel.getValueAt(index, 4);
                    String user = (String) tableModel.getValueAt(index, 5);
                    String auth = (String) tableModel.getValueAt(index, 6);
                    String pass = "";
                    log.debug("删除：" + index + " => session：" + session + ", protocol：" + protocol +
                            ", address：" + address + ", port：" + port + ", user：" + user + ", auth：" + auth);

                    // 数据库获取账户密码
                    try {
                        Connection connection = DbUtil.getConnection();
                        Statement statement = connection.createStatement();

                        String session_sql = "SELECT password FROM session WHERE " +
                                "session_name = '" + session + "' AND " +
                                "protocol = '" + protocol + "' AND " +
                                "address = '" + address + "' AND " +
                                "port = '" + port + "' AND " +
                                "username = '" + user + "' AND " +
                                "auth_type = '" + auth + "'";

                        ResultSet resultSet = statement.executeQuery(session_sql);
                        while (resultSet.next()) {
                            pass = resultSet.getString("password");
                        }

                        DbUtil.close(statement, resultSet);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    // 更新最近会话访问时间
                    DbUtil.updateAccessTime(new Date().getTime(), "" +
                            "session_name = '" + session + "' AND " +
                            "protocol = '" + protocol + "' AND " +
                            "address = '" + address + "' AND " +
                            "port = '" + port + "' AND " +
                            "username = '" + user + "' AND " +
                            "auth_type = '" + auth + "'"
                    );

                    // 打开会话
                    if (SshUtil.testConnection(address, port) == 1) {

                        String defaultTitle = address.equals("") ? "未命名" : "(" + (mainTabbedPane.getTabCount()-1) + ") " + address;
                        mainTabbedPane.insertTab(defaultTitle, null,
                                new SshTabbedPane(mainTabbedPane, SshUtil.createTerminalWidget(address, port, user, pass),
                                        address, port, user, pass), // For Sftp
                                "快速连接", mainTabbedPane.getTabCount()-1);

                        mainTabbedPane.setTabComponentAt(mainTabbedPane.getTabCount()-2, new TabbedTitlePane(defaultTitle, mainTabbedPane, new CloseButton(defaultTitle, mainTabbedPane)));
                        mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount()-2);

                    } else {
                        DialogUtil.warn("连接失败");
                    }
                }
            }
        };
        // 会话树右键菜单
        JPopupMenu recentPopupMenu = new JPopupMenu();
        recentPopupMenu.add(reopenAction);

        recentTable.setComponentPopupMenu(recentPopupMenu);
    }
}
