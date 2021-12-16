package com.g3g4x5x6.ui.panels;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatTreeClosedIcon;
import com.formdev.flatlaf.icons.FlatTreeLeafIcon;
import com.g3g4x5x6.App;
import com.g3g4x5x6.ui.panels.ssh.SshPane;
import com.g3g4x5x6.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
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
    private String rootPath = ConfigUtil.getWorkPath() + "/sessions/ssh/";
    private String[] columnNames = {"会话名称", "协议", "地址", "端口", "登录用户", "认证类型"}; // 添加<创建时间>

    private Connection connection;
    private Statement statement;

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

        sessionTree.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                log.debug("再次获取焦点");
            }
        });
    }

    /**
     * TODO 会话树
     */
    private void initTree() {
        root = new DefaultMutableTreeNode("分类目录");
        treeModel = new DefaultTreeModel(root);

        sessionTree = new JTree();
        sessionTree.setModel(treeModel);
        sessionTree.setSelectionPath(new TreePath(root.getPath()));
        // 设置树显示根节点句柄
        sessionTree.setShowsRootHandles(false);
        // 设置树节点可编辑
//        sessionTree.setEditable(true);

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
                log.debug("选中分类目录: " + path.toString());

                // 2. 获取当前目录
                String currentTag = convertPathToTag(path);

                // 3. 获取子目录
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
                    sessionTree.expandPath(new TreePath(currentTreeNode.getPath()));
                    sessionTree.setExpandsSelectedPaths(true);
                }
            }
        });
    }


    private HashSet<String> getChildrenTag(String tag) {
        tableModel.setRowCount(0);
        HashSet<String> tags = new HashSet<>();
        String path = "";
        if (!tag.equals("分类目录")) {
            path = tag.substring(tag.indexOf("/"));
        }
        String dir = rootPath + path;
        log.debug(dir);
        File file = new File(dir);
        if (!file.exists()) {
            return tags;
        }
        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                tags.add(f.getName());
            }
            if (f.isFile()) {
                tableModel.addRow(getSessionFields(f));
            }
        }
        return tags;
    }

    private void initTable() {
        sessionTable = new JTable();
        sessionTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2){
                    openSession();
                }
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
        File rootFile = new File(rootPath);

        for (File file : rootFile.listFiles()) {
            if (file.isDirectory()) {
                DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(file.getName());
                root.add(tempNode);
            }
            if (file.isFile()) {
                String[] row = getSessionFields(file);
                tableModel.addRow(row);
            }
        }
        sessionTree.expandPath(new TreePath(root.getPath()));
        sessionTree.setSelectionPath(new TreePath(root.getPath()));
        sessionTree.setExpandsSelectedPaths(true);
    }

    private String[] getSessionFields(File file) {
        String[] row = new String[6];
        try {
            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            JSONObject jsonObject = JSON.parseObject(json);
            row[0] = jsonObject.getString("sessionName");
            row[1] = jsonObject.getString("sessionProtocol");
            row[2] = jsonObject.getString("sessionAddress");
            row[3] = jsonObject.getString("sessionPort");
            row[4] = jsonObject.getString("sessionUser");
            row[5] = jsonObject.getString("sessionLoginType");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return row;
    }


    /**
     * 初始化右键菜单
     */
    private void initPopupMenu() {
        AbstractAction addDirectory = new AbstractAction("新建目录") {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath treePath = sessionTree.getSelectionPath();
                String currentTag = convertPathToTag(treePath);
                String newTag = JOptionPane.showInputDialog(App.mainFrame, "目录名称：\n", "新建目录", JOptionPane.PLAIN_MESSAGE);

                if (newTag != null) {
                    String newPath = "";
                    if (currentTag.equals("分类目录")) {
                        newPath = rootPath + newTag;
                    } else {
                        newPath = rootPath + currentTag.substring(currentTag.indexOf("/")) + "/" + newTag;
                    }
                    if (Files.exists(Path.of(newPath))) {
                        log.debug("目录已存在");
                    } else {
                        try {
                            Files.createDirectories(Path.of(newPath));
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                        DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(newTag);
                        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) sessionTree.getLastSelectedPathComponent();
                        currentNode.add(tempNode);
                        sessionTree.expandPath(new TreePath(currentNode.getPath()));
                        sessionTree.setExpandsSelectedPaths(true);
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
                        TreePath treePath = sessionTree.getSelectionPath();
                        String currentTag = convertPathToTag(treePath);
                        if (!currentTag.equals("分类目录")) {
                            String path = rootPath + currentTag.substring(currentTag.indexOf("/"));
                            if (Files.exists(Path.of(path))) {
                                try {
                                    Files.delete(Path.of(path));
                                } catch (IOException exception) {
                                    exception.printStackTrace();
                                }
                            } else {
                                log.debug("目录不存在");
                            }
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
                TreePath treePath = sessionTree.getSelectionPath();
                String currentTag = convertPathToTag(treePath);
                String category = "";
                int index = currentTag.indexOf("/");
                if (index != -1)
                    category = Path.of(currentTag.substring(index + 1)).toString();
                SshPane sshPane = new SshPane();
                sshPane.setCategory(category);
                mainTabbedPane.insertTab("编辑选项卡", new FlatSVGIcon("com/g3g4x5x6/ui/icons/addToDictionary.svg"), sshPane, "编辑会话", mainTabbedPane.getTabCount());
                mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
            }
        };

        AbstractAction delSession = new AbstractAction("删除会话") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 支持批量删除(多选)
                int[] indexs = sessionTable.getSelectedRows();
                int bool = JOptionPane.showConfirmDialog(App.mainFrame, "是否确认删除会话", "删除会话", JOptionPane.YES_NO_OPTION);
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

                        TreePath treePath = sessionTree.getSelectionPath();
                        String currentTag = convertPathToTag(treePath);
                        String path = rootPath;
                        if (!currentTag.equals("分类目录")) {
                            path = rootPath + currentTag.substring(currentTag.indexOf("/"));
                        }
                        File dir = new File(path);
                        if (dir.exists()) {
                            for (File file : dir.listFiles()) {
                                if (file.getName().contains(address) && file.getName().contains(port) && file.getName().contains(user)) {
                                    file.delete();
                                }
                            }
                        }
                        // 移除出列表
                        tableModel.removeRow(sessionTable.getSelectedRow());

                    }
                }
            }
        };

        AbstractAction openSession = new AbstractAction("打开会话") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 默认打开 SSH 会话, 未来实现会话自动类型鉴别
                openSession();
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
                        DialogUtil.info("连接成功: SSH://" + host + ":" + port);
                    } else {
                        DialogUtil.warn("连接失败: SSH://" + host + ":" + port);
                    }
                }
            }
        };

        AbstractAction editSession = new AbstractAction("编辑会话") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] indexes = sessionTable.getSelectedRows();
                for (int index : indexes) {
                    String[] array = getRowFilePath(index);
                    File file = new File(array[0]);
                    try {
                        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                        JSONObject jsonObject = JSON.parseObject(json);

                        String finalCurrentTag = array[1];
                        new Thread(() -> {
                            SshPane sshPane = new SshPane();
                            sshPane.setHostField(jsonObject.getString("sessionAddress"));
                            sshPane.setPortField(jsonObject.getString("sessionPort"));
                            sshPane.setUserField(jsonObject.getString("sessionUser"));
                            sshPane.setPassField(jsonObject.getString("sessionPass"));
                            sshPane.setKeyLabel(jsonObject.getString("sessionKeyPath"));
                            sshPane.setSessionName(jsonObject.getString("sessionName"));
                            sshPane.setCommentText(jsonObject.getString("sessionComment"));
                            sshPane.setCategory(finalCurrentTag.substring(finalCurrentTag.indexOf("/") + 1));
                            mainTabbedPane.insertTab("编辑选项卡", new FlatSVGIcon("com/g3g4x5x6/ui/icons/addToDictionary.svg"), sshPane, "编辑会话", mainTabbedPane.getTabCount());
                            mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
                        }).start();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
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
        tablePopupMenu.add(editSession);
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

    private void openSession(){
        int[] indexs = sessionTable.getSelectedRows();
        for (int index : indexs) {
            String[] array = getRowFilePath(index);
            File file = new File(array[0]);
            if (file.exists()) {
                new Thread(() -> SessionUtil.openSshSession(file.getAbsolutePath())).start();
            }
        }
    }

    private String[] getRowFilePath(int index) {
        String address = (String) tableModel.getValueAt(index, 2);
        String port = (String) tableModel.getValueAt(index, 3);
        String user = (String) tableModel.getValueAt(index, 4);

        TreePath treePath = sessionTree.getSelectionPath();
        String currentTag = convertPathToTag(treePath);
        String path = rootPath;
        if (!currentTag.equals("分类目录")) {
            path = rootPath + currentTag.substring(currentTag.indexOf("/"));
        } else {
            currentTag = "/";
        }
        String fileName = "ssh_" + address + "_" + port + "_" + user + ".json";
        log.debug(path + fileName);
        return new String[]{path + "/" + fileName, currentTag};
    }
}
