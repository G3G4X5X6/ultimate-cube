package com.g3g4x5x6.panel.session;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatTreeClosedIcon;
import com.formdev.flatlaf.icons.FlatTreeLeafIcon;
import com.g3g4x5x6.AppConfig;
import com.g3g4x5x6.remote.NewTabbedPane;
import com.g3g4x5x6.remote.ssh.panel.NewSshPane;
import com.g3g4x5x6.remote.utils.CommonUtil;
import com.g3g4x5x6.remote.utils.session.SessionOpenTool;
import com.g3g4x5x6.remote.utils.SshUtil;
import com.g3g4x5x6.remote.utils.VaultUtil;
import com.g3g4x5x6.ui.ToolBar;
import com.g3g4x5x6.utils.DialogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
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
import java.util.*;
import java.util.List;

import static com.g3g4x5x6.MainFrame.mainTabbedPane;

@Slf4j
public class SessionManagerPanel extends JPanel {
    private final ToolBar toolBar = new ToolBar();
    private final JSplitPane splitPane;

    private JTree sessionTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode root;

    private JTable sessionTable;
    private DefaultTableModel tableModel;
    private final String rootPath = AppConfig.getWorkPath() + "/sessions/";
    private final String[] columnNames = {"会话名称", "协议", "地址", "端口", "登录用户", "认证类型"}; // 添加<创建时间>

    private final JMenuItem refreshItem = new JMenuItem("刷新");
    private final JMenuItem addDirItem = new JMenuItem("新建分类");
    private final JMenuItem delDirItem = new JMenuItem("删除分类");
    private final JMenuItem addSessionItem = new JMenuItem("新增会话");
    private final JMenuItem delSessionItem = new JMenuItem("删除会话");
    private final JMenuItem openSessionItem = new JMenuItem("打开会话");
    private final JMenuItem testSessionItem = new JMenuItem("测试会话");
    private final JMenuItem editSessionItem = new JMenuItem("编辑会话");
    private final JMenuItem copyPassItem = new JMenuItem("复制密码");

    /**
     * TODO 已展开的节点添加目录时无法添加节点。
     */
    public SessionManagerPanel() {
        this.setLayout(new BorderLayout());

        this.splitPane = new JSplitPane();
        this.splitPane.setDividerLocation(200);
        this.add(splitPane, BorderLayout.CENTER);

        // 初始化工具栏
        initToolBar();

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

    private void initToolBar() {
        // 1.
        JButton refreshBtn = new JButton();
        refreshBtn.setIcon(new FlatSVGIcon("icons/refresh.svg"));
        refreshBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("刷新会话");
                refresh();
            }
        });

        // 2.
        toolBar.add(refreshBtn);

        // 3.
        this.add(toolBar, BorderLayout.NORTH);
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
        log.debug("选中分类目录: {}", path);

        // 2. 获取子目录
        HashSet<String> children = getChildrenTag(Objects.requireNonNull(path));
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

    private HashSet<String> getChildrenTag(TreePath path) {
        HashSet<String> childrenNode = new HashSet<>();

        if (path.getPathCount() < 2) {
            // 点击 `目录分类` 不响应
            return childrenNode;
        }
        // 去掉方括号并按逗号分割
        String[] pathArray = path.toString().substring(1, path.toString().length() - 1).split(", ");
        // 转换为列表
        List<String> pathList = Arrays.asList(pathArray);
        String protocol = pathList.get(1);
        String category = String.join("/", pathList).replace("分类目录/" + protocol, "");
        if (category.startsWith("/")) {
            category = category.substring(1);
        }

        tableModel.setRowCount(0);
        HashMap<String, ArrayList<JSONObject>> sessionMaps = SessionFileUtil.getCategoriesMap();
        if (sessionMaps.get(category) != null) {
            for (JSONObject jsonObject : sessionMaps.get(category)) {
                if (jsonObject.getString("sessionProtocol").equals(protocol)) {
                    tableModel.addRow(new String[]{jsonObject.getString("sessionName"), jsonObject.getString("sessionProtocol"), jsonObject.getString("sessionAddress"), jsonObject.getString("sessionPort"), jsonObject.getString("sessionUser"), jsonObject.getString("sessionLoginType")});
                }
            }
        }
        HashMap<String, ArrayList<JSONObject>> protocolsMap = SessionFileUtil.getProtocolsMap();
        if (protocolsMap.get(protocol) != null) {
            for (JSONObject jsonObject : protocolsMap.get(protocol)) {
                if (jsonObject != null) {
                    String tmpCategory = jsonObject.getString("sessionCategory");
                    if (tmpCategory != null && tmpCategory.startsWith(category) && !tmpCategory.equals(category)) {
                        tmpCategory = tmpCategory.replace(category, "");
                        if (tmpCategory.contains("/")) {
                            if (category.isEmpty()) childrenNode.add(tmpCategory.split("/")[0]);
                            else childrenNode.add(tmpCategory.split("/")[1]);
                        } else {
                            childrenNode.add(tmpCategory);
                        }
                    }
                }
            }
        }
        return childrenNode;
    }

    private void initTable() {
        sessionTable = new JTable();
        sessionTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSession();
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

        // 设置列宽
        TableColumn sessionNameColumn = sessionTable.getColumnModel().getColumn(0);
        sessionNameColumn.setPreferredWidth(150); // 设置第一列的宽度
        sessionNameColumn.setMinWidth(150);

        DefaultTableCellRenderer sessionNameRenderer = new DefaultTableCellRenderer();
        sessionNameRenderer.setIcon(new FlatTreeLeafIcon());
        sessionTable.getColumn("会话名称").setCellRenderer(sessionNameRenderer);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JTextField.CENTER);
        sessionTable.getColumn("协议").setCellRenderer(centerRenderer);
//        sessionTable.getColumn("地址").setCellRenderer(centerRenderer);
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
                if (file.getName().startsWith("ssh_") || file.getName().startsWith("FreeRDP_") || file.getName().startsWith("Telnet_")) {
                    String[] row = getSessionFields(file);
                    tableModel.addRow(row);
                }
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

                mainTabbedPane.insertTab("新建会话", new FlatSVGIcon("icons/addToDictionary.svg"), new NewTabbedPane(mainTabbedPane), "新建会话", mainTabbedPane.getTabCount());
                mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
            }
        };

        AbstractAction delSession = new AbstractAction("删除会话") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 支持批量删除(多选)
                int[] indexes = sessionTable.getSelectedRows();
                int bool = JOptionPane.showConfirmDialog(SessionManagerPanel.this, "是否确认删除会话", "删除会话", JOptionPane.YES_NO_OPTION);
                if (bool == 0) {
                    for (int index : indexes) {
                        File file = new File(Objects.requireNonNull(getSessionObject(index)).getString("sessionFilePath"));
                        if (file.delete()) {
                            // 移除出列表
                            tableModel.removeRow(sessionTable.getSelectedRow());
                        } else {
                            DialogUtil.warn("删除会话失败");
                        }
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
                    JSONObject jsonObject = getSessionObject(index);

                    new Thread(() -> {
                        NewSshPane sshPane = new NewSshPane(mainTabbedPane);
                        sshPane.setHostField(jsonObject.getString("sessionAddress"));
                        sshPane.setPortField(jsonObject.getString("sessionPort"));
                        sshPane.setUserField(jsonObject.getString("sessionUser"));
                        sshPane.setPassField(VaultUtil.decryptPasswd(jsonObject.getString("sessionPass")));
                        sshPane.setKeyLabel(jsonObject.getString("sessionKeyPath"));
                        sshPane.setSessionName(jsonObject.getString("sessionName"));
                        sshPane.setCommentText(jsonObject.getString("sessionComment"));
                        sshPane.setAuthType(jsonObject.getString("sessionLoginType"));
                        sshPane.setCategory(jsonObject.getString("sessionCategory"));
                        sshPane.setEditPath(jsonObject.getString("sessionFilePath"));
                        mainTabbedPane.insertTab("编辑选项卡", new FlatSVGIcon("icons/addToDictionary.svg"), sshPane, "编辑会话", mainTabbedPane.getTabCount());
                        mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
                    }).start();
                }
            }
        };

        AbstractAction copyPassAction = new AbstractAction("复制密码") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JSONObject jsonObject = getSessionObject(sessionTable.getSelectedRow());
                CommonUtil.setClipboardText(VaultUtil.decryptPasswd(Objects.requireNonNull(jsonObject).getString("sessionPass")));
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
        copyPassItem.addActionListener(copyPassAction);
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
        popupMenu.addSeparator();
        popupMenu.add(copyPassItem);

        return popupMenu;
    }

    private void configurePopupMenu() {
        if (sessionTable.getSelectedRow() == -1) {
//            addSessionItem.setEnabled(false);
            openSessionItem.setEnabled(false);
            testSessionItem.setEnabled(false);
            editSessionItem.setEnabled(false);
            delSessionItem.setEnabled(false);
            copyPassItem.setEnabled(false);
        } else {
//            addSessionItem.setEnabled(true);
            openSessionItem.setEnabled(true);
            testSessionItem.setEnabled(true);
            editSessionItem.setEnabled(true);
            delSessionItem.setEnabled(true);
            copyPassItem.setEnabled(true);
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

    private void openSession() {
        int[] indexes = sessionTable.getSelectedRows();
        for (int index : indexes) {
            SessionOpenTool.OpenSessionByProtocol(Objects.requireNonNull(getSessionObject(index)).getString("sessionFilePath"), (String) tableModel.getValueAt(index, 1));
        }
    }

    private JSONObject getSessionObject(int index) {
        String protocol = (String) tableModel.getValueAt(index, 1);
        String address = (String) tableModel.getValueAt(index, 2);
        String port = (String) tableModel.getValueAt(index, 3);
        String user = (String) tableModel.getValueAt(index, 4);
        String authType = (String) tableModel.getValueAt(index, 5);

        HashMap<String, ArrayList<JSONObject>> protocolsMap = SessionFileUtil.getProtocolsMap();
        for (JSONObject jsonObject : protocolsMap.get(protocol)) {
            if (jsonObject.getString("sessionAddress").equals(address) && jsonObject.getString("sessionPort").equals(port) && jsonObject.getString("sessionUser").equals(user) && jsonObject.getString("sessionLoginType").equals(authType)) {
                return jsonObject;
            }
        }
        return null;
    }

}
