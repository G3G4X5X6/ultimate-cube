package com.g3g4x5x6.panel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatTreeClosedIcon;
import com.formdev.flatlaf.icons.FlatTreeLeafIcon;
import com.g3g4x5x6.App;
import com.g3g4x5x6.MainFrame;
import com.g3g4x5x6.ssh.SessionInfo;
import com.g3g4x5x6.ssh.panel.NewSshPane;
import com.g3g4x5x6.ssh.panel.SshTabbedPane;
import com.g3g4x5x6.utils.AppConfig;
import com.g3g4x5x6.utils.DialogUtil;
import com.g3g4x5x6.utils.SessionUtil;
import com.g3g4x5x6.utils.SshUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;

@Slf4j
public class SessionManagerPanel extends JPanel {

    private final JTabbedPane mainTabbedPane;

    private final JSplitPane splitPane;

    private JTree sessionTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode root;

    private JTable sessionTable;
    private DefaultTableModel tableModel;
    private final String rootPath = AppConfig.getWorkPath() + "/sessions/ssh/";
    private final String[] columnNames = {"会话名称", "协议", "地址", "端口", "登录用户", "认证类型"}; // 添加<创建时间>

    private final JMenuItem refreshItem = new JMenuItem("刷新");
    private final JMenuItem addDirItem = new JMenuItem("新建分类");
    private final JMenuItem delDirItem = new JMenuItem("删除分类");
    private final JMenuItem addSessionItem = new JMenuItem("新增会话");
    private final JMenuItem delSessionItem = new JMenuItem("删除会话");
    private final JMenuItem openSessionItem = new JMenuItem("打开会话");
    private final JMenuItem testSessionItem = new JMenuItem("测试会话");
    private final JMenuItem editSessionItem = new JMenuItem("编辑会话");

    /**
     * TODO 已展开的节点添加目录时无法添加节点。
     */
    public SessionManagerPanel(JTabbedPane tabbedPane) {
        this.setLayout(new BorderLayout());
        this.mainTabbedPane = tabbedPane;

        this.splitPane = new JSplitPane();
        this.splitPane.setDividerLocation(200);
        this.add(splitPane, BorderLayout.CENTER);

        // 初始化会话树
        initTree();

        // 初始化会话列表
        initTable();

        //
        refreshContent(rootPath);

        // 初始化右键动作菜单
        initPopupMenuItem();

        sessionTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    displayPopupMenu(sessionTree, e);
                }
            }
        });
        sessionTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    displayPopupMenu(sessionTable, e);
                }
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
        sessionTree.setShowsRootHandles(true);
        // 设置树节点可编辑 TODO 重命名分类目录
        sessionTree.setEditable(true);

        JScrollPane treeScroll = new JScrollPane(sessionTree);
        treeScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        treeScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        DefaultTreeCellRenderer render = new DefaultTreeCellRenderer();
        render.setLeafIcon(new FlatTreeClosedIcon());
        sessionTree.setCellRenderer(render);
        splitPane.setLeftComponent(treeScroll);

        sessionTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                expand();
            }
        });
    }

    private void expand() {
        // 1. 获取被选中的相关节点信息
        TreePath path = sessionTree.getSelectionPath();
        log.debug("选中分类目录: " + path.toString());

        // 2. 获取当前目录
        String currentTag = convertPathToTag(path);

        // 3. 获取子目录
        HashSet<String> children = getChildrenTag(currentTag);
        // 避免 currentTreeNode 为 null 的问题
        if (!sessionTree.isSelectionEmpty()) {
            DefaultMutableTreeNode currentTreeNode = (DefaultMutableTreeNode) sessionTree.getLastSelectedPathComponent();
            currentTreeNode.removeAllChildren();
            DefaultMutableTreeNode temp;
            for (String tag : children) {
                temp = new DefaultMutableTreeNode(tag);
                currentTreeNode.add(temp);
            }
            sessionTree.expandPath(new TreePath(currentTreeNode.getPath()));
            sessionTree.setExpandsSelectedPaths(true);
        }
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
        for (File f : Objects.requireNonNull(file.listFiles())) {
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
                if (e.getClickCount() == 2) {
                    openSession(mainTabbedPane);
                }
            }
        });
        tableModel = new DefaultTableModel() {
            // 不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.setColumnIdentifiers(columnNames);

        sessionTable.setModel(tableModel);

        JScrollPane tableScroll = new JScrollPane(sessionTable);
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

    private void refreshContent(String path) {
        log.debug(path);

        tableModel.setRowCount(0);

        File rootFile = new File(path);
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) sessionTree.getLastSelectedPathComponent();

        HashSet<DefaultMutableTreeNode> children = new HashSet<>();
        for (int i = 0; i < parent.getChildCount(); i++) {
            children.add((DefaultMutableTreeNode) parent.getChildAt(i));
        }

        for (DefaultMutableTreeNode node : children) {
            treeModel.removeNodeFromParent(node);
        }

        for (File file : Objects.requireNonNull(rootFile.listFiles())) {
            if (file.isDirectory()) {
                log.debug(file.getAbsolutePath());
                DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(file.getName());
                treeModel.insertNodeInto(tempNode, parent, 0);
            }
            if (file.isFile()) {
                String[] row = getSessionFields(file);
                tableModel.addRow(row);
            }
        }
        sessionTree.expandPath(new TreePath(parent.getPath()));
        sessionTree.setSelectionPath(new TreePath(parent.getPath()));
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
    private void initPopupMenuItem() {
        AbstractAction refreshAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("刷新");
                refresh();
            }
        };

        AbstractAction addDirectory = new AbstractAction("新建目录") {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath treePath = sessionTree.getSelectionPath();
                String currentTag = convertPathToTag(treePath);
                String newTag = JOptionPane.showInputDialog(SessionManagerPanel.this, "目录名称：\n", "新建目录", JOptionPane.PLAIN_MESSAGE);

                if (newTag != null) {
                    String newPath;
                    if (currentTag.equals("分类目录")) {
                        newPath = rootPath + newTag;
                    } else {
                        newPath = rootPath + currentTag.substring(currentTag.indexOf("/")) + "/" + newTag;
                    }
                    if (Files.exists(Path.of(newPath))) {
                        log.debug("目录已存在");
                        JOptionPane.showMessageDialog(SessionManagerPanel.this, "目录已存在", "警告", JOptionPane.WARNING_MESSAGE);
                    } else {
                        try {
                            Files.createDirectories(Path.of(newPath));
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                        DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(newTag);
                        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) sessionTree.getLastSelectedPathComponent();
                        treeModel.insertNodeInto(tempNode, parent, 0);
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
                    int bool = JOptionPane.showConfirmDialog(SessionManagerPanel.this, "是否确认删除目录", "删除目录", JOptionPane.YES_NO_OPTION);
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

                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) sessionTree.getLastSelectedPathComponent();
                        sessionTree.setSelectionPath(Objects.requireNonNull(sessionTree.getSelectionPath()).getParentPath());
                        treeModel.removeNodeFromParent(node);
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
                NewSshPane sshPane = new NewSshPane(MainFrame.mainTabbedPane);
                sshPane.setCategory(category);
                mainTabbedPane.insertTab("编辑选项卡", new FlatSVGIcon("icons/addToDictionary.svg"), sshPane, "编辑会话", mainTabbedPane.getTabCount());
                mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
            }
        };

        AbstractAction delSession = new AbstractAction("删除会话") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 支持批量删除(多选)
                int[] indexs = sessionTable.getSelectedRows();
                int bool = JOptionPane.showConfirmDialog(SessionManagerPanel.this, "是否确认删除会话", "删除会话", JOptionPane.YES_NO_OPTION);
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
                openSession(mainTabbedPane);
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
                            NewSshPane sshPane = new NewSshPane(MainFrame.mainTabbedPane);
                            sshPane.setHostField(jsonObject.getString("sessionAddress"));
                            sshPane.setPortField(jsonObject.getString("sessionPort"));
                            sshPane.setUserField(jsonObject.getString("sessionUser"));
                            sshPane.setPassField(jsonObject.getString("sessionPass"));
                            sshPane.setKeyLabel(jsonObject.getString("sessionKeyPath"));
                            sshPane.setSessionName(jsonObject.getString("sessionName"));
                            sshPane.setCommentText(jsonObject.getString("sessionComment"));
                            sshPane.setAuthType(jsonObject.getString("sessionLoginType"));
                            sshPane.setCategory(finalCurrentTag.substring(finalCurrentTag.indexOf("/") + 1));
                            sshPane.setEditPath(file.getAbsolutePath());
                            mainTabbedPane.insertTab("编辑选项卡", new FlatSVGIcon("icons/addToDictionary.svg"), sshPane, "编辑会话", mainTabbedPane.getTabCount());
                            mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
                        }).start();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }

                }
            }
        };

        refreshItem.addActionListener(refreshAction);
        openSessionItem.addActionListener(openSession);
        testSessionItem.addActionListener(testSession);
        editSessionItem.addActionListener(editSession);
        addSessionItem.addActionListener(addSession);
        delSessionItem.addActionListener(delSession);
        addDirItem.addActionListener(addDirectory);
        delDirItem.addActionListener(delDirectory);
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        popupMenu.add(refreshItem);
        popupMenu.addSeparator();
        popupMenu.add(openSessionItem);
        popupMenu.add(testSessionItem);
        popupMenu.add(editSessionItem);
        popupMenu.addSeparator();
        popupMenu.add(addSessionItem);
        popupMenu.add(delSessionItem);
        popupMenu.addSeparator();
        popupMenu.add(addDirItem);
        popupMenu.add(delDirItem);

        return popupMenu;
    }

    private void configurePopupMenu() {
        if (sessionTable.getSelectedRow() == -1) {
//            addSessionItem.setEnabled(false);
            openSessionItem.setEnabled(false);
            testSessionItem.setEnabled(false);
            editSessionItem.setEnabled(false);
            delSessionItem.setEnabled(false);
        } else {
//            addSessionItem.setEnabled(true);
            openSessionItem.setEnabled(true);
            testSessionItem.setEnabled(true);
            editSessionItem.setEnabled(true);
            delSessionItem.setEnabled(true);
        }

        if (sessionTree.isSelectionEmpty()) {
//            addDirItem.setEnabled(false);
            delDirItem.setEnabled(false);
        } else {
//            addDirItem.setEnabled(true);
            delDirItem.setEnabled(true);
        }
    }

    private void displayPopupMenu(Component component, MouseEvent event) {
        JPopupMenu popupMenu = createPopupMenu();

        configurePopupMenu();

        popupMenu.show(component, event.getX(), event.getY());
    }

    private void refresh() {
        // 刷新树
        expand();

        // 刷新表
        TreePath treePath = sessionTree.getSelectionPath();
        String path = getPathFromTag(convertPathToTag(treePath));
        refreshContent(path);
    }

    private String getPathFromTag(String tag) {
        String path = "";
        if (!tag.equals("分类目录")) {
            path = tag.substring(tag.indexOf("/"));
        }
        return rootPath + path;
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

    private void openSession(JTabbedPane tabbedPane) {
        int[] indexs = sessionTable.getSelectedRows();
        for (int index : indexs) {
            String[] array = getRowFilePath(index);
            File file = new File(array[0]);
            if (file.exists()) {
                new Thread(() -> {
                    // 等待进度条
                    MainFrame.addWaitProgressBar();

                    SessionInfo sessionInfo = SessionUtil.openSshSession(file.getAbsolutePath());
                    if (SshUtil.testConnection(sessionInfo.getSessionAddress(), sessionInfo.getSessionPort()) == 1) {
                        String defaultTitle = sessionInfo.getSessionName().equals("") ? "未命名" : sessionInfo.getSessionName();
                        MainFrame.mainTabbedPane.addTab(defaultTitle, new FlatSVGIcon("icons/OpenTerminal_13x13.svg"),
                                new SshTabbedPane(sessionInfo)
                        );
                        MainFrame.mainTabbedPane.setSelectedIndex(MainFrame.mainTabbedPane.getTabCount() - 1);
                    }
                    App.sessionInfos.put(sessionInfo.getSessionId(), sessionInfo);

                    // 移除等待进度条
                    MainFrame.removeWaitProgressBar();
                }).start();
            }
        }
    }

    private String[] getRowFilePath(int index) {
        String address = (String) tableModel.getValueAt(index, 2);
        String port = (String) tableModel.getValueAt(index, 3);
        String user = (String) tableModel.getValueAt(index, 4);
        String authType = (String) tableModel.getValueAt(index, 5);

        TreePath treePath = sessionTree.getSelectionPath();
        String currentTag = convertPathToTag(treePath);
        String path = rootPath;
        if (!currentTag.equals("分类目录")) {
            path = rootPath + currentTag.substring(currentTag.indexOf("/"));
        } else {
            currentTag = "/";
        }
        String fileName = "ssh_" + address + "_" + port + "_" + user + "_" + authType + ".json";
        log.debug(path + fileName);
        return new String[]{path + "/" + fileName, currentTag};
    }
}
