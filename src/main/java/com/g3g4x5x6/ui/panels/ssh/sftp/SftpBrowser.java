package com.g3g4x5x6.ui.panels.ssh.sftp;

import com.formdev.flatlaf.icons.FlatTreeClosedIcon;
import com.formdev.flatlaf.icons.FlatTreeLeafIcon;
import com.g3g4x5x6.utils.DialogUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.fs.SftpFileSystem;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;


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
    private JToolBar toolBar;

    // TODO 右键菜单动作
    private JPopupMenu popMenu;
    private AbstractAction uploadAction;
    private AbstractAction downloadAction;
    private AbstractAction deleteFilesAction;
    private AbstractAction deleteDirsAction;
    private AbstractAction openAction;

    private SftpFileSystem fs;

    public SftpBrowser(SftpFileSystem sftpFileSystem) {
        borderLayout = new BorderLayout();
        this.setLayout(borderLayout);

        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        this.fs = sftpFileSystem;

        initPane();
        initPopupMenu();
    }

//    public SftpBrowser(String host, int port, String user, String pass) {
//        borderLayout = new BorderLayout();
//        this.setLayout(borderLayout);
//
//        toolBar = new JToolBar();
//        toolBar.setFloatable(false);
//
//        this.host = host;
//        this.port = port;
//        this.user = user;
//        this.pass = pass;
//
//        createSftpFileSystem();
//
//        initPane();
//        initPopupMenu();
//    }

    public void setFs(SftpFileSystem fs) {
        this.fs = fs;
    }


    private void initPane() {
        toolBar.add(new AbstractAction("Test") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Test");
            }
        });
        this.add(toolBar, BorderLayout.NORTH);
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
        myTree.setSelectionPath(new TreePath(root));
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

    private void upload(File file, String path) throws IOException {
        if (!Files.exists(fs.getPath(path, file.getName())))
            Files.copy(Path.of(file.getAbsolutePath()), fs.getPath(path, file.getName()));
        File[] files = file.listFiles();
        for (File f : files) {
            log.debug(path + "/" + f.getName());
            if (f.isDirectory()) {
                upload(f, path + "/" + file.getName());
            } else {
                if (!Files.exists(fs.getPath(path, file.getName(), f.getName())))
                    Files.copy(Path.of(f.getAbsolutePath()), fs.getPath(path, file.getName(), f.getName()));
            }
        }
    }

    /**
     * 右键菜单动作实现
     */
    private void initPopupMenu() {
        uploadAction = new AbstractAction("上传") {
            @SneakyThrows
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
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                chooser.setMultiSelectionEnabled(true);
                int value = chooser.showOpenDialog(SftpBrowser.this);
                if (value == JFileChooser.APPROVE_OPTION) {
                    String finalPath = path;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            File[] files = chooser.getSelectedFiles();
                            for (File file : files) {
                                if (file.isDirectory()) {
                                    try {
                                        upload(file, finalPath);
                                    } catch (IOException exception) {
                                        if (exception.getMessage().equals("Permission denied"))
                                            DialogUtil.error("权限不足！！！");
                                    }
                                }
                                log.debug(finalPath + "/" + file.getName());
                                if (!Files.exists(fs.getPath(finalPath, file.getName()))) {
                                    try {
                                        Files.copy(Path.of(file.getAbsolutePath()), fs.getPath(finalPath, file.getName()));
                                    } catch (IOException exception) {
                                        if (exception.getMessage().equals("Permission denied"))
                                            DialogUtil.error("权限不足！！！");
                                    }
                                }
                            }
                            freshTable();
                            TreePath path = myTree.getSelectionPath();
                            myTree.expandPath(path);
                        }
                    }).start();
                }
            }
        };

        downloadAction = new AbstractAction("下载") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("下载文件...");
                // 至少选中目录，才能开始下载
                if (myTree.isSelectionEmpty()) {
                    DialogUtil.warn("请先选择文件目录");
                } else {
                    // 将下载整个目录
//                    log.debug("=========== myTree.hasFocus() ===========>" + myTree.hasFocus());
                    if (myTable.getSelectedRowCount() < 1) {
                        // TODO 批量下载（单目录、多目录下载）
                        int yesOrNo = DialogUtil.yesOrNo(SftpBrowser.this, "是否确认整目录下载？");
                        if (yesOrNo == 0) {
                            log.debug("================= 确认整目录下载 ================");

                        }
                    } else {
                        // TODO 文件下载（单文件，多文件下载）
                        log.debug("选中文件数：" + myTable.getSelectedRowCount());

                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        int value = chooser.showOpenDialog(SftpBrowser.this);
                        if (value == JFileChooser.APPROVE_OPTION) {
                            File outputFile = chooser.getSelectedFile();
                            log.debug(outputFile.getAbsolutePath());

                            for (int index : myTable.getSelectedRows()) {
                                String downloadFileName = myTable.getValueAt(index, 0).toString();

                                // TODO 获取下载文件路径
                                TreePath dstPath = myTree.getSelectionPath();
                                String path = convertTreePathToString(dstPath) + "/" + downloadFileName;
                                log.info("下载的文件：" + path);

                                Path downloadPath = fs.getPath(path);
                                // 保存文件路径
                                String destPath = outputFile.getPath() + "/" + downloadFileName;
                                log.info("保存的文件：" + destPath);

                                // TODO 下载, 进度, 下载5M分段
                                new Thread(() -> {
                                    try {
                                        log.info("开始下载：" + path);
                                        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destPath));

                                        InputStream inputStream = Files.newInputStream(downloadPath);
                                        log.debug("文件大小：" + inputStream.available());

                                        byte[] buf = new byte[1024 * 1024 * 5];
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
            }
        };

        deleteFilesAction = new AbstractAction("删除文件(s)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("删除文件...");
                for (int index : myTable.getSelectedRows()) {
                    String downloadFileName = myTable.getValueAt(index, 0).toString();
                    // TODO 获取下载文件路径
                    TreePath dstPath = myTree.getSelectionPath();
                    String path = convertTreePathToString(dstPath) + "/" + downloadFileName;
                    log.info("删除的文件：" + path);
                    try {
                        Files.delete(fs.getPath(path));
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
                freshTable();
            }
        };

        deleteDirsAction = new AbstractAction("删除目录(s)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("删除目录...");
            }
        };

        openAction = new AbstractAction("打开") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("打开文件...");
                DialogUtil.info("敬请期待！");
            }
        };
        popMenu = new JPopupMenu();
        popMenu.add(openAction);
        popMenu.addSeparator();
        popMenu.add(uploadAction);
        popMenu.add(downloadAction);
        popMenu.addSeparator();
        popMenu.add(deleteFilesAction);

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

    private void freshTable() {
        // TODO 清空表中旧数据
        tableModel.setRowCount(0);

        // 获取被选中的相关节点
        TreePath path = myTree.getSelectionPath();

        DefaultMutableTreeNode currentTreeNode = (DefaultMutableTreeNode) myTree.getLastSelectedPathComponent();
        log.debug(path.toString());

        DefaultMutableTreeNode temp = null;
        try {
            for (SftpClient.DirEntry entry : getDirEntry(convertTreePathToString(path))) {
                log.debug(entry.getLongFilename());
                if (entry.getFilename().equals(".") || entry.getFilename().equals(".."))
                    continue;

                if (entry.getAttributes().isDirectory()) {
                    // TODO 当 fs 断开重连时， 判断选中节点下是否已存在相同子节点，应避免重复添加子节点
                    if (!currentTreeNode.isLeaf()) {
                        log.debug("================== getChildAt() =>" + currentTreeNode.getChildAt(0).toString());
                        log.debug("================== getChildCount() =>" + currentTreeNode.getChildCount());
                        boolean flag = true;
                        for (int i = 0; i < currentTreeNode.getChildCount(); i++) {
                            if (currentTreeNode.getChildAt(i).toString().equals(entry.getFilename())) {
                                flag = false;
                            }
                        }
                        if (flag) {
                            temp = new DefaultMutableTreeNode(entry.getFilename());
                            currentTreeNode.add(temp);
                        }
                    } else {
                        temp = new DefaultMutableTreeNode(entry.getFilename());
                        currentTreeNode.add(temp);
                    }
                } else {
                    // TODO 显示目录下的文件
                    tableModel.addRow(convertFileLongNameToStringArray(entry));
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            if (exception.getMessage().strip().endsWith("Permission denied"))
                DialogUtil.error("权限不足！！！");
        }
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
                    // 刷新文件列表
                    freshTable();
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
