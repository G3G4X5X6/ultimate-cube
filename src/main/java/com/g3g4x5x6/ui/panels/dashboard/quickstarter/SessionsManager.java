package com.g3g4x5x6.ui.panels.dashboard.quickstarter;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatTreeClosedIcon;
import com.formdev.flatlaf.icons.FlatTreeLeafIcon;
import com.g3g4x5x6.App;
import com.g3g4x5x6.ui.dialog.SessionDialog;
import com.g3g4x5x6.ui.panels.ssh.SshTabbedPane;
import com.g3g4x5x6.utils.DbUtil;
import com.g3g4x5x6.utils.DialogUtil;
import com.g3g4x5x6.utils.SshUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashSet;


@Slf4j
public class SessionsManager extends JPanel {

    private BorderLayout borderLayout;
    private JTabbedPane mainTabbedPane;

    private JSplitPane splitPane;

    private JScrollPane treeScroll;
    private JTree sessionTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode root;

    private JScrollPane tableScroll;
    private JTable sessionTable;
    private DefaultTableModel tableModel;
    private String[] columnNames = {"会话名称", "协议", "地址", "端口", "登录用户", "认证类型"}; // 添加<创建时间>

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private String sql;

    public SessionsManager(JTabbedPane mainTabbedPane) {
        borderLayout = new BorderLayout();
        this.setLayout(borderLayout);
        this.mainTabbedPane = mainTabbedPane;

        this.splitPane = new JSplitPane();
        this.splitPane.setDividerLocation(200);
        this.add(splitPane, BorderLayout.CENTER);

        // 初始化会话树
        initTree();

        // 初始化会话列表
        initTable();

        //
        initContent();

        // 初始化右键菜单
        initPopupMenu();
    }

    /**
     * TODO 会话树
     */
    private void initTree() {
        root = new DefaultMutableTreeNode("会话标签");
        treeModel = new DefaultTreeModel(root);

        sessionTree = new JTree();
        sessionTree.setModel(treeModel);
        sessionTree.setSelectionPath(new TreePath(root.getPath()));
        // 设置树显示根节点句柄
        sessionTree.setShowsRootHandles(false);
        // 设置树节点可编辑
        sessionTree.setEditable(true);

        treeScroll = new JScrollPane(sessionTree);
        treeScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        treeScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        DefaultTreeCellRenderer render = new DefaultTreeCellRenderer();
        render.setLeafIcon(new FlatTreeClosedIcon());
        sessionTree.setCellRenderer(render);
        splitPane.setLeftComponent(treeScroll);

        sessionTree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                // 1. 获取被选中的相关节点信息
                TreePath path = e.getPath();
                int row = sessionTree.getRowForPath(path);
                log.debug("选中标签: " + path.toString());

                // 2. 刷新会话列表
                flushTable(convertPathToTag(path));

                // 3. 展开子标签
                String currentTag = convertPathToTag(path);
                HashSet<String> children = getChildrenTag(currentTag);
                // 避免 currentTreeNode 为 null 的问题
                if (!sessionTree.isSelectionEmpty()) {
                    DefaultMutableTreeNode currentTreeNode = (DefaultMutableTreeNode) sessionTree.getLastSelectedPathComponent();
                    currentTreeNode.removeAllChildren();
                    DefaultMutableTreeNode temp = null;
                    for (String tag : children) {
                        temp = new DefaultMutableTreeNode(tag);
                        currentTreeNode.add(temp);
                    }
                }
            }
        });
    }

    private void initTable() {
        sessionTable = new JTable();
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(columnNames);

        sessionTable.setModel(tableModel);

        tableScroll = new JScrollPane(sessionTable);
        tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        DefaultTableCellRenderer sessionnameRenderer = new DefaultTableCellRenderer();
        sessionnameRenderer.setIcon(new FlatTreeLeafIcon());
        sessionTable.getColumn("会话名称").setCellRenderer(sessionnameRenderer);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JTextField.CENTER);
        sessionTable.getColumn("协议").setCellRenderer(centerRenderer);
        sessionTable.getColumn("地址").setCellRenderer(centerRenderer);
        sessionTable.getColumn("端口").setCellRenderer(centerRenderer);
        sessionTable.getColumn("登录用户").setCellRenderer(centerRenderer);
        sessionTable.getColumn("认证类型").setCellRenderer(centerRenderer);

        splitPane.setRightComponent(tableScroll);
    }

    private void initContent() {
        try {
            connection = DbUtil.getConnection();
            statement = connection.createStatement();
            sql = "select tag from tag";
            resultSet = statement.executeQuery(sql);

            HashSet<String> tempSet = new HashSet<>();
            while (resultSet.next()) {
                log.debug(resultSet.getString("tag"));
                String tempTag = resultSet.getString("tag");
                String[] tempTags = tempTag.split("/");

                if (tempTags.length > 1) {
                    tempSet.add(tempTags[1]);
                }
            }
            for (String node : tempSet) {
                DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(node);
                root.add(tempNode);
            }
            sessionTree.expandPath(sessionTree.getSelectionPath());

            // TODO 默认显示根目录用户列表
            flushTable("会话标签");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据标签刷新会话
     *
     * @param tag
     */
    private void flushTable(String tag) {
        tableModel.setRowCount(0);
        try {
            sql = "SELECT * FROM session WHERE id in (SELECT session FROM relation WHERE tag in (SELECT id FROM tag WHERE tag = '" + tag + "'))";
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                log.debug("Row: " + resultSet.getString("session_name") + ", " +
                        resultSet.getString("address") + ", " +
                        resultSet.getString("port") + ", " +
                        resultSet.getString("username") + ", " +
                        resultSet.getString("protocol") + ", " +
                        resultSet.getString("auth_type"));
                tableModel.addRow(new String[]{
                        resultSet.getString("session_name"),
                        resultSet.getString("protocol"),
                        resultSet.getString("address"),
                        resultSet.getString("port"),
                        resultSet.getString("username"),
                        resultSet.getString("auth_type"),
                });
            }
            DbUtil.close(statement, resultSet);
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }

    /**
     * 用于增删会话目录时重载Tree
     */
    @Deprecated
    private void flushTree() {
        // TODO 1. 获取当前选中的路径
        TreePath selectedTreePath = sessionTree.getSelectionPath();

        // TODO 2. 重载Tree
        root.removeAllChildren();
        loadTreeNode(root);
        treeModel.reload();

        // TODO 3. 展开选中的路径
        sessionTree.expandPath(selectedTreePath);

        // TODO 4. 刷新会话列表
        flushTable(convertPathToTag(selectedTreePath));
    }

    @Deprecated
    private void loadTreeNode(DefaultMutableTreeNode node) {
        String currentTag = node.toString();
        log.debug("**********当前标签********** " + currentTag);
        if (node.getChildCount() != 0) {
            // TODO 获取子标签
            HashSet<String> tags = getChildrenTag(currentTag);
            for (String tag : tags) {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(tag);
                node.add(newNode);
                loadTreeNode(newNode);
            }
        }
    }

    private HashSet<String> getChildrenTag(String tag) {
        HashSet<String> tags = new HashSet<>();
        try {
            sql = "select tag from tag where tag LIKE '" + tag + "/%'";
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                log.debug("Tags: " + resultSet.getString("tag"));
                String tagColumn = resultSet.getString("tag");
                String currentTag = tagColumn.split("/")[tag.split("/").length];
                tags.add(currentTag);
            }
            DbUtil.close(statement, resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return tags;
    }


    /**
     * 初始化右键菜单
     */
    private void initPopupMenu() {
        AbstractAction addDirectory = new AbstractAction("新增目录") {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath treePath = sessionTree.getSelectionPath();
                String currentTag = convertPathToTag(treePath);
                String newTag = JOptionPane.showInputDialog(App.mainFrame, "目录名称：\n", "新建目录", JOptionPane.PLAIN_MESSAGE);
                DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) sessionTree.getLastSelectedPathComponent();

                Boolean flag = true;
                // 取消添加标签
                if (newTag == null){
                    flag = false;
                }else{
                    // 判断空标签
                    if (newTag.strip().equals("")){
                        flag = false;
                        DialogUtil.warn("目录名称不能为空");
                    }
                    // 判断重复标签
                    for (String tag : getChildrenTag(convertPathToTag(treePath))) {
                        if (tag.equals(newTag)) {
                            flag = false;
                            DialogUtil.warn("已存在该标签");
                        }
                    }
                    // 判断特殊字符
                    if (newTag.indexOf("/") != -1){
                        flag = false;
                        DialogUtil.warn("<html>标签不能存在字符： <strong><font color='red'>/</font></strong></html>");
                    }
                }
                if (flag){
                    currentNode.add(new DefaultMutableTreeNode(newTag));
                    treeModel.reload(currentNode);
                    String tag = currentTag + "/" + newTag;
                    log.debug("newTag: " + tag);
                    try {
                        sql = "INSERT INTO tag VALUES(NULL, '" + tag + "')";
                        connection = DbUtil.getConnection();
                        statement = connection.createStatement();
                        int row = statement.executeUpdate(sql);
                        log.debug("新建目录: " + sql + ":" + row);
                        DbUtil.close(statement);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        };

        AbstractAction delDirectory = new AbstractAction("删除目录") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("目录不为空不能删除，该目录会话数：" + sessionTable.getRowCount());

                // TODO 不为空、包含子目录，无法删除
                DefaultMutableTreeNode currentTreeNode = (DefaultMutableTreeNode) sessionTree.getLastSelectedPathComponent();
                if (!currentTreeNode.isLeaf() || sessionTable.getRowCount() != 0) {
                    DialogUtil.warn("不为空、包含子目录，无法删除");
                } else {
                    // TODO 删除目录
                    int bool = JOptionPane.showConfirmDialog(App.mainFrame, "是否确认删除目录", "删除目录", JOptionPane.YES_NO_OPTION);
                    if (bool == 0) {
                        log.debug("确认删除目录");
                        // 删除数据库中的标签
                        try {
                            TreePath treePath = sessionTree.getSelectionPath();
                            String currentTag = convertPathToTag(treePath);
                            if (!currentTag.equals("会话标签")) {
                                connection = DbUtil.getConnection();
                                statement = connection.createStatement();

                                sql = "DELETE FROM tag WHERE tag = '" + currentTag + "'";
                                statement.executeUpdate(sql);
                                log.debug("删除目录：" + sql);
                                DbUtil.close(statement);
                            } else {
                                DialogUtil.warn("不能删除根标签");
                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        treeModel.removeNodeFromParent(currentTreeNode);
                    }
                }
            }
        };

        AbstractAction addSession = new AbstractAction("新增会话") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("新增会话");
                // TODO 1. 获取当前标签
                String currentTag = convertPathToTag(sessionTree.getSelectionPath());

                // TODO 2. 弹窗录取信息、添加会话列表（Table）、新增会话（数据库）
                SessionDialog sessionDialog = new SessionDialog(App.mainFrame, currentTag);

                // TODO 3. 刷新列表
                flushTable(currentTag);
            }
        };

        AbstractAction delSession = new AbstractAction("删除会话") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 支持批量删除(多选)
                int[] indexs = sessionTable.getSelectedRows();
                int bool = JOptionPane.showConfirmDialog(null, "是否确认删除会话", "删除会话", JOptionPane.YES_NO_OPTION);
                if (bool == 0) {
                    for (int index : indexs) {
                        String session = (String) tableModel.getValueAt(sessionTable.getSelectedRow(), 0);
                        String protocol = (String) tableModel.getValueAt(sessionTable.getSelectedRow(), 1);
                        String address = (String) tableModel.getValueAt(sessionTable.getSelectedRow(), 2);
                        String port = (String) tableModel.getValueAt(sessionTable.getSelectedRow(), 3);
                        String user = (String) tableModel.getValueAt(sessionTable.getSelectedRow(), 4);
                        String auth = (String) tableModel.getValueAt(sessionTable.getSelectedRow(), 5);
                        log.debug("删除：" + sessionTable.getSelectedRow() + " => session：" + session + ", protocol：" + protocol +
                                ", address：" + address + ", port：" + port + ", user：" + user + ", auth：" + auth);

                        // 移除出列表
                        tableModel.removeRow(sessionTable.getSelectedRow());

                        // 删除数据库中的会话
                        try {
                            connection = DbUtil.getConnection();
                            statement = connection.createStatement();

                            TreePath treePath = sessionTree.getSelectionPath();
                            String currentTag = convertPathToTag(treePath);

                            String tag_sql = "DELETE FROM relation WHERE session in (select id from session where " +
                                    "session_name = '" + session + "' AND " +
                                    "protocol = '" + protocol + "' AND " +
                                    "address = '" + address + "' AND " +
                                    "port = '" + port + "' AND " +
                                    "username = '" + user + "' AND " +
                                    "auth_type = '" + auth + "')";

                            String session_sql = "DELETE FROM session WHERE " +
                                    "session_name = '" + session + "' AND " +
                                    "protocol = '" + protocol + "' AND " +
                                    "address = '" + address + "' AND " +
                                    "port = '" + port + "' AND " +
                                    "username = '" + user + "' AND " +
                                    "auth_type = '" + auth + "'";
                            statement.executeUpdate(tag_sql);
                            statement.executeUpdate(session_sql);

                            DbUtil.close(statement);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                }
            }
        };

        AbstractAction openSession = new AbstractAction("打开会话") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 默认打开 SSH 会话, 未来实现会话自动类型鉴别
                int[] indexs = sessionTable.getSelectedRows();
                for (int index : indexs) {
                    String session = (String) tableModel.getValueAt(index, 0);
                    String protocol = (String) tableModel.getValueAt(index, 1);
                    String address = (String) tableModel.getValueAt(index, 2);
                    String port = (String) tableModel.getValueAt(index, 3);
                    String user = (String) tableModel.getValueAt(index, 4);
                    String auth = (String) tableModel.getValueAt(index, 5);
                    String pass = "";
                    log.debug("删除：" + index + " => session：" + session + ", protocol：" + protocol +
                            ", address：" + address + ", port：" + port + ", user：" + user + ", auth：" + auth);

                    // 数据库获取账户密码
                    try {
                        connection = DbUtil.getConnection();
                        statement = connection.createStatement();

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

                        DbUtil.close(statement);
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

                        String defaultTitle = address.equals("") ? "未命名" : address;
                        mainTabbedPane.insertTab(defaultTitle, new FlatSVGIcon("com/g3g4x5x6/ui/icons/OpenTerminal_13x13.svg"),
                                new SshTabbedPane(mainTabbedPane, SshUtil.createTerminalWidget(address, port, user, pass),
                                        address, port, user, pass),             // For Sftp
                                "快速连接", mainTabbedPane.getTabCount());
                        mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
                    } else {
                        DialogUtil.warn("连接失败");
                    }
                }
            }
        };

        AbstractAction testSession = new AbstractAction("测试会话") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String host = "";
                String port = "";
                int[] indices = sessionTable.getSelectedRows();
                for (int index : indices) {
                    host = (String) tableModel.getValueAt(index, 2);
                    port = (String) tableModel.getValueAt(index, 3);

                    if (SshUtil.testConnection(host, port) == 1) {
                        DialogUtil.info("连接成功: ssh://" + host + ":" + port);
                    } else {
                        DialogUtil.warn("连接失败: ssh://" + host + ":" + port);
                    }
                }
            }
        };

        // 会话树右键菜单
        JPopupMenu treePopupMenu = new JPopupMenu();
        treePopupMenu.add(addDirectory);
        treePopupMenu.add(delDirectory);
        treePopupMenu.add(addSession);
        sessionTree.setComponentPopupMenu(treePopupMenu);

        // 会话列表右键菜单
        JPopupMenu tablePopupMenu = new JPopupMenu();
        tablePopupMenu.add(openSession);
        tablePopupMenu.add(testSession);
        tablePopupMenu.add(addSession);
        tablePopupMenu.add(delSession);
        sessionTable.setComponentPopupMenu(tablePopupMenu);
    }

    private String convertPathToTag(TreePath treePath) {
        StringBuilder tempPath = new StringBuilder("");
        if (treePath == null) {
            return "";
        }

        String path = treePath.toString();
        String[] paths = path.substring(1, path.length() - 1).split(",");
        for (String temp : paths) {
            temp = temp.strip();
            tempPath.append(temp);
            tempPath.append("/");
        }
        tempPath.deleteCharAt(tempPath.length() - 1);

        log.debug(String.valueOf(tempPath));
        return tempPath.toString();
    }
}
