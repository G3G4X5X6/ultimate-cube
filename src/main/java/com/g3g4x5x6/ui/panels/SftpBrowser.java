package com.g3g4x5x6.ui.panels;

import com.formdev.flatlaf.icons.FlatTreeClosedIcon;
import com.formdev.flatlaf.icons.FlatTreeLeafIcon;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.fs.SftpFileSystem;
import org.apache.sshd.sftp.client.fs.SftpFileSystemProvider;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class SftpBrowser extends JPanel {

    private BorderLayout borderLayout;
    private JSplitPane splitPane;
    private MyTree myTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode root;
    private MyTable myTable;
    private DefaultTableColumnModel columnModel;
    private DefaultTableModel tableModel;
    private String[] columns;

    private JDialog dialog;
    private JLabel msg;

    // TODO 右键菜单动作
    private JPopupMenu popMenu;
    private AbstractAction uploadAction;
    private AbstractAction downloadAction;
    private AbstractAction deleteAction;
    private AbstractAction openAction;

    private SshClient client;
    private SftpFileSystem fs;
    private String host;
    private int port;
    private String user;
    private String pass;

    public SftpBrowser(SftpFileSystem sftpFileSystem) {
        borderLayout = new BorderLayout();
        this.setLayout(borderLayout);

        this.fs = sftpFileSystem;

        initPane();
        initPopupMenu();
    }

    public SftpBrowser(String host, int port, String user, String pass) {
        borderLayout = new BorderLayout();
        this.setLayout(borderLayout);
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;

        createSftpFileSystem();

        initPane();
        initPopupMenu();
    }

    private void createSftpFileSystem() {
        client = SshClient.setUpDefaultClient();
        // TODO 配置 SshClient
        // override any default configuration...
//        client.setSomeConfiguration(...);
//        client.setOtherConfiguration(...);
        client.start();

        SftpFileSystemProvider provider = new SftpFileSystemProvider(client);
        URI uri = SftpFileSystemProvider.createFileSystemURI(host, port, user, pass);
        try {
            // TODO 配置 SftpFileSystem
            Map<String, Object> params = new HashMap<>();
//            params.put("param1", value1);
//            params.put("param2", value2);
            this.fs = provider.newFileSystem(uri, params);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void initPane() {
        splitPane = new JSplitPane();
        splitPane.setDividerLocation(200);

        init();
        myTable = new MyTable();
        initTable();
        myTree = new MyTree();
        initTree();

        JScrollPane treeScroll = new JScrollPane(myTree);
        treeScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        treeScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        JScrollPane tableScroll = new JScrollPane(myTable);
        tableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        splitPane.setLeftComponent(treeScroll);
        splitPane.setRightComponent(tableScroll);
        this.add(splitPane, BorderLayout.CENTER);
    }

    private void init() {
        // TODO Tree
        root = new DefaultMutableTreeNode("/");
        treeModel = new DefaultTreeModel(root);

        // TODO Table
        // 文件名, 权限, 大小, 类型, 属组, 修改时间
        columns = new String[]{"文件名", "权限", "大小", "类型", "属组", "修改时间"};
        columnModel = new DefaultTableColumnModel();
        tableModel = new DefaultTableModel();

        // TODO 弹窗提醒
        dialog = new JDialog();
        dialog.setTitle("SFTP浏览器");
        dialog.setModal(true);
        dialog.setSize(new Dimension(300, 200));
        dialog.setLocationRelativeTo(null);
        msg = new JLabel();
        dialog.add(msg);
    }

    private void initTree() {
        // TODO 设置叶子节点样式
        DefaultTreeCellRenderer render = new DefaultTreeCellRenderer();
        render.setLeafIcon(new FlatTreeClosedIcon());
        myTree.setCellRenderer(render);

        DefaultMutableTreeNode child = null;
        try {
            for (SftpClient.DirEntry entry : fs.getClient().readDir(root.toString())) {
                if (entry.getFilename().equals(".") || entry.getFilename().equals(".."))
                    continue;
                // TODO 添加文件至表格
                if (entry.getAttributes().isDirectory()) {
                    child = new DefaultMutableTreeNode(entry.getFilename());
                    root.add(child);
                } else {
                    tableModel.addRow(convertFileLongNameToStringArray(entry));
                    myTable.setModel(tableModel);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        myTree.setModel(treeModel);
    }

    private void initTable() {
        // TODO 设置表头
        tableModel.setColumnIdentifiers(columns);
        myTable.setColumnModel(columnModel);
        myTable.setModel(tableModel);

        // TODO 单元格渲染
        DefaultTableCellRenderer filenameRenderer = new DefaultTableCellRenderer();
        filenameRenderer.setIcon(new FlatTreeLeafIcon());
        myTable.getColumn("文件名").setCellRenderer(filenameRenderer);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JTextField.CENTER);
        myTable.getColumn("权限").setCellRenderer(centerRenderer);
        myTable.getColumn("大小").setCellRenderer(centerRenderer);
        myTable.getColumn("类型").setCellRenderer(centerRenderer);
        myTable.getColumn("属组").setCellRenderer(centerRenderer);
        myTable.getColumn("修改时间").setCellRenderer(centerRenderer);
    }

    /**
     * 右键菜单动作实现
     */
    private void initPopupMenu() {
        uploadAction = new AbstractAction("上传") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("上传文件...");

                // TODO 获取上传目标路径
                TreePath dstPath = myTree.getSelectionPath();
                String path = convertTreePathToString(dstPath);
                if (myTree.isSelectionEmpty()) {
                    msg.setText("尚未选择上传路径，将使用根目录 / ");
                    dialog.setVisible(true);

                    try {
                        // TODO 默认上传至用户目录 getDefaultDir()
                        path = fs.getDefaultDir().toRealPath().toString();
                        log.debug("默认用户目录：" + path);
                        log.debug(path);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                } else {
                }

                // TODO 获取上传文件路径
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int value = chooser.showOpenDialog(SftpBrowser.this);
                if (value == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    log.debug(file.getAbsolutePath());

                    // TODO 上传, 进度, 上传5M分段
                    try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
                        Path remotePath = fs.getPath(path + "/" + file.getName());
                        Files.copy(inputStream, remotePath);

                    } catch (FileNotFoundException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        };

        downloadAction = new AbstractAction("下载") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("下载文件...");

                if (myTree.isSelectionEmpty() || myTable.getSelectedRow() < 1) {
                    msg.setText("尚未选择文件或目录");
                    dialog.setVisible(true);
                } else {
                    // 下载文件名
                    String downloadFileName = myTable.getValueAt(myTable.getSelectedRow(), 0).toString();

                    // TODO 获取下载文件路径
                    TreePath dstPath = myTree.getSelectionPath();
                    String path = convertTreePathToString(dstPath) + "/" + downloadFileName;
                    log.info("下载的文件：" + path);

                    Path downloadPath = fs.getPath(path);

                    if (Files.isDirectory(downloadPath)) {
                        msg.setText("暂不支持下载文件夹");
                        dialog.setVisible(true);
                    } else {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        int value = chooser.showOpenDialog(SftpBrowser.this);
                        if (value == JFileChooser.APPROVE_OPTION) {
                            File outputFile = chooser.getSelectedFile();
                            log.debug(outputFile.getAbsolutePath());

                            // 保存文件路径
                            String destPath = outputFile.getPath() + "/" + downloadFileName;
                            log.info("保存的文件：" + destPath);

                            // TODO 下载, 进度, 下载5M分段
                            new Thread(() -> {
                                try {
                                    log.info("开始下载：" + path);
                                    BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destPath));

                                    InputStream inputStream = Files.newInputStream(downloadPath);

                                    byte[] buf = new byte[1024];
                                    int bytesRead;
                                    while ((bytesRead = inputStream.read(buf)) != -1) {
                                        outputStream.write(buf, 0, bytesRead);
                                    }

                                    outputStream.flush();
                                    outputStream.close();
                                    inputStream.close();
                                    log.info("下载完成：" + destPath);
                                } catch (FileNotFoundException fileNotFoundException) {
                                    fileNotFoundException.printStackTrace();
                                } catch (IOException exception) {
                                    exception.printStackTrace();
                                }
                            }).start();
                        }
                    }

                }


            }
        };

        deleteAction = new AbstractAction("删除") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("删除文件...");
            }
        };

        openAction = new AbstractAction("打开") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("打开文件...");
            }
        };
        popMenu = new JPopupMenu();
        popMenu.add(openAction);
        popMenu.addSeparator();
        popMenu.add(uploadAction);
        popMenu.add(downloadAction);
        popMenu.addSeparator();
        popMenu.add(deleteAction);

        myTree.setComponentPopupMenu(popMenu);
        myTable.setComponentPopupMenu(popMenu);
    }

    private Iterable<SftpClient.DirEntry> getDirEntry(String path) {
        Iterable<SftpClient.DirEntry> entry = null;
        try {
            entry = fs.getClient().readDir(path);
        } catch (IOException e) {

            e.printStackTrace();
        }
        return entry;
    }

    private String convertTreePathToString(TreePath treePath) {
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
        
        return tempPath.toString();
    }

    private String[] convertFileLongNameToStringArray(SftpClient.DirEntry entry) {
        // 文件名, 权限, 大小, 类型, 属组, 修改时间
        String[] temp = new String[6];
        temp[0] = entry.getFilename();
        temp[1] = entry.getLongFilename().split("\\s+")[0];
        log.debug(String.valueOf(entry.getLongFilename().split("\\s+")));

        // 大小单位转换
        String humanSize = "";
        long size = entry.getAttributes().getSize();
        if (size >= 1024 && size < 1024 * 1024) { // KB
            double d = size / 1024;
            humanSize = String.format("%.2f", d) + "KB";
        } else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {   // MB
            double d = size / 1024 / 1024;
            humanSize = String.format("%.2f", d) + "MB";
        } else if (size >= 1024 * 1024 * 1024) {  // GB
            double d = size / 1024 / 1024 / 1024;
            humanSize = String.format("%.2f", d) + "GB";
        }
        temp[2] = humanSize;
        temp[3] = String.valueOf(entry.getAttributes().getType());
        temp[4] = entry.getLongFilename().split("\\s+")[2] + "/" + entry.getLongFilename().split("\\s+")[3];
        temp[5] = entry.getAttributes().getModifyTime().toString();

        return temp;
    }


    private class MyTree extends JTree {
        public MyTree() {
            // 设置树显示根节点句柄
            this.setShowsRootHandles(true);
            // 设置树节点可编辑
            this.setEditable(true);

            addListener();

        }

        private void addListener() {
            /**
             * 节点展开/折叠监听器
             */
            this.addTreeExpansionListener(new TreeExpansionListener() {
                @Override
                public void treeExpanded(TreeExpansionEvent event) {
                    log.debug("展开的节点: " + event.getPath());
                }

                @Override
                public void treeCollapsed(TreeExpansionEvent event) {
                    log.debug("折叠的节点: " + event.getPath());
                }
            });

            /**
             * 节点展开/折叠监听器（比上一个监听器先执行）
             */
            this.addTreeWillExpandListener(new TreeWillExpandListener() {
                @Override
                public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                    log.debug("展开的节点: " + event.getPath());
                }

                @Override
                public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
                    log.debug("折叠的节点: " + event.getPath());
                }
            });

            /**
             * 节点被选中的监听器
             */
            this.addTreeSelectionListener(new TreeSelectionListener() {
                @Override
                public void valueChanged(TreeSelectionEvent e) {
                    // TODO Sftp 存活检测
                    if (fs.getClientSession().isClosed()) {
                        log.debug("================== fs.getClientSession().isClosed(), reopen now =====================");
                        createSftpFileSystem();
                    } else {
                        log.debug("================== client is open =====================");
                    }

                    // TODO 清空表中旧数据
                    tableModel.setRowCount(0);

                    // 获取被选中的相关节点
                    TreePath path = e.getPath();

                    DefaultMutableTreeNode currentTreeNode = (DefaultMutableTreeNode) myTree.getLastSelectedPathComponent();
                    log.debug(path.toString());

                    DefaultMutableTreeNode temp = null;
                    for (SftpClient.DirEntry entry : getDirEntry(convertTreePathToString(path))) {
//                        log.debug(entry.getLongFilename());
                        if (entry.getFilename().equals(".") || entry.getFilename().equals(".."))
                            continue;

                        // TODO 添加文件至表格
                        if (entry.getAttributes().isDirectory()) {
                            temp = new DefaultMutableTreeNode(entry.getFilename());
                            currentTreeNode.add(temp);
                        } else {
                            // TODO 显示目录下的文件
                            tableModel.addRow(convertFileLongNameToStringArray(entry));
                        }
                    }
                }
            });

            /**
             * 节点增删改监听器
             */
            this.getModel().addTreeModelListener(new TreeModelListener() {
                @Override
                public void treeNodesChanged(TreeModelEvent e) {
                    log.debug("节点改变: " + e.getTreePath());
                }

                @Override
                public void treeNodesInserted(TreeModelEvent e) {
                    log.debug("节点插入: " + e.getTreePath());
                }

                @Override
                public void treeNodesRemoved(TreeModelEvent e) {
                    log.debug("节点移除: " + e.getTreePath());
                }

                @Override
                public void treeStructureChanged(TreeModelEvent e) {
                    log.debug("结构改变: " + e.getTreePath());
                }
            });
        }
    }

    private class MyTable extends JTable {

    }
}
