package com.g3g4x5x6.sftp;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatTreeClosedIcon;
import com.formdev.flatlaf.icons.FlatTreeLeafIcon;
import com.g3g4x5x6.editor.EditorFrame;
import com.g3g4x5x6.sftp.actions.*;
import com.g3g4x5x6.utils.FileUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.fs.SftpFileSystem;
import org.apache.sshd.sftp.common.SftpConstants;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class SftpBrowser extends JPanel {
    private final EditorFrame editorFrame = EditorFrame.getInstance();

    private MyTree myTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode root;
    private MyTable myTable;
    private DefaultTableColumnModel columnModel;
    private DefaultTableModel tableModel;
    private String[] columns;

    private SftpTabbedPanel sftpTabbedPanel;

    private final JToolBar toolBar;
    private String finalPath;

    // TODO 右键菜单动作
    private JPopupMenu popupMenu;
    private JMenuItem refreshMenuItem;
    private JMenuItem openMenuItem;
    private JMenuItem copyPathItem;
    private JMenuItem uploadItem;
    private JMenuItem downloadItem;
    private JMenuItem newFileItem;
    private JMenuItem delFileItem;
    private JMenuItem newDirItem;
    private JMenuItem delDirItem;
    private JMenuItem forceDelItem;

    private JPopupMenu transferPopMenu;

    private SftpClient sftpClient;
    private SftpFileSystem fs;

    public JProgressBar waitBar;
    public AtomicInteger waitCount = new AtomicInteger(0);

    @SneakyThrows
    public SftpBrowser(SftpFileSystem sftpFileSystem) {
        BorderLayout borderLayout = new BorderLayout();
        this.setLayout(borderLayout);

        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        this.fs = sftpFileSystem;
        this.sftpClient = fs.getClient();

        initPane();

        registerDropTarget();

        myTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    displayPopupMenu(myTree, e.getPoint());
                }
            }
        });

        myTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    displayPopupMenu(myTable, e.getPoint());
                }
            }
        });
    }

    public void addWaitProgressBar() {
        waitCount.incrementAndGet();
        waitBar.setVisible(true);
    }

    public void removeWaitProgressBar() {
        int count;
        if (waitCount.get() > 0) {
            count = waitCount.decrementAndGet();
            if (count == 0) {
                waitBar.setVisible(false);
            }
        }
    }

    private void registerDropTarget() {
        // 创建拖拽目标监听器
        DropTargetListener listener = new DropTargetListenerImpl(this);
        // 在 JTable 上注册拖拽目标监听器
        DropTarget dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, listener, true);
    }

    private void createPopupMenu() {
        popupMenu = new JPopupMenu();

        refreshMenuItem = new JMenuItem("刷新");
        refreshMenuItem.addActionListener(new RefreshAction(this));

        openMenuItem = new JMenuItem("打开");
        openMenuItem.addActionListener(new OpenAction(this));

        copyPathItem = new JMenuItem("复制路径");
        copyPathItem.addActionListener(new CopyPathAction(this));

        uploadItem = new JMenuItem("上传");
        uploadItem.addActionListener(uploadsAction);

        downloadItem = new JMenuItem("下载");
//        downloadItem.addActionListener(downloadsAction);
        downloadItem.addActionListener(new DownloadAction(this));

        newFileItem = new JMenuItem("新建文件");
        newFileItem.addActionListener(new NewFileAction(this));

        newDirItem = new JMenuItem("新建目录");
        newDirItem.addActionListener(new NewDirAction(this));

        delFileItem = new JMenuItem("删除文件");
        delFileItem.addActionListener(new DelFileAction(this));

        delDirItem = new JMenuItem("删除目录");
        delDirItem.addActionListener(new DelDirAction(this));

        forceDelItem = new JMenuItem("强制删除 (rm -rf )");
        forceDelItem.addActionListener(new ForceDelAction(this));

        popupMenu.add(refreshMenuItem);
        popupMenu.addSeparator();
        popupMenu.add(openMenuItem);
        popupMenu.addSeparator();
        popupMenu.add(copyPathItem);
        popupMenu.addSeparator();
        popupMenu.add(uploadItem);
        popupMenu.add(downloadItem);
        popupMenu.addSeparator();
        popupMenu.add(newFileItem);
        popupMenu.add(newDirItem);
        popupMenu.addSeparator();
        popupMenu.add(delFileItem);
        popupMenu.add(delDirItem);
        popupMenu.addSeparator();
        popupMenu.add(forceDelItem);
    }

    private void configurePopupMenuActions() {
        if (myTree.getSelectionPath() == null && myTable.getSelectedRow() == -1) {
            refreshMenuItem.setEnabled(false);
            forceDelItem.setEnabled(false);
            copyPathItem.setEnabled(false);
        } else {
            refreshMenuItem.setEnabled(true);
            forceDelItem.setEnabled(true);
            copyPathItem.setEnabled(true);
        }

        if (myTable.getSelectedRow() == -1) {
            delFileItem.setEnabled(false);
            openMenuItem.setEnabled(false);
        } else {
            delFileItem.setEnabled(true);
            openMenuItem.setEnabled(true);
        }
    }

    private synchronized void displayPopupMenu(JComponent component, Point p) {
        if (popupMenu == null) {
            createPopupMenu();
        }
        configurePopupMenuActions();
        popupMenu.show(component, p.x, p.y);
    }

    public void setFs(SftpFileSystem fs) {
        this.fs = fs;
    }

    public SftpFileSystem getFs() {
        return fs;
    }

    public SftpClient getSftpClient() {
        return sftpClient;
    }

    public void setSftpClient(SftpClient sftpClient) {
        this.sftpClient = sftpClient;
    }

    private void initPane() {
        waitBar = new JProgressBar();
        waitBar.setIndeterminate(true);
        waitBar.setVisible(false);
        waitBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 为了异常情况下隐藏等待进度条
                if (e.getClickCount() == 2) {
                    int count = waitCount.decrementAndGet();
                    if (count <= 0) {
                        waitBar.setVisible(false);
                    }
                }
            }
        });

        //
        JSplitPane splitPane = new JSplitPane();
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


         sftpTabbedPanel= new SftpTabbedPanel();

        JSplitPane vSplitPane = new JSplitPane();
        vSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        vSplitPane.setBorder(null);
        vSplitPane.setDividerLocation(600);

        vSplitPane.setTopComponent(splitPane);
        vSplitPane.setBottomComponent(sftpTabbedPanel);
        this.add(vSplitPane, BorderLayout.CENTER);

        // 工具栏菜单
        // 快速跳转工具栏
        JTextField pathField = new JTextField();
        pathField.setColumns(20);
        pathField.putClientProperty("JTextField.placeholderText", "/home/g3g4x5x6/Document");
        pathField.registerKeyboardAction(e -> {
                    String quickPath = pathField.getText();
                    // TODO 1. 检查路径是否存在
                    if (Files.exists(fs.getPath(quickPath))) {
                        if (!Files.isDirectory(fs.getPath(quickPath))) {
                            quickPath = fs.getPath(quickPath).getParent().toString();
                        }
                        DefaultMutableTreeNode parent = root;
                        // TODO 2. 添加快速跳转地址栏的树路径 （问题：如何解决节点重复添加的问题）
                        for (String child : quickPath.split("/")) {
                            if (child.strip().equals(""))
                                continue;
                            DefaultMutableTreeNode resultNode = findTreeNode(parent, child);
                            if (resultNode == null) {
                                DefaultMutableTreeNode temp = new DefaultMutableTreeNode(child);
                                treeModel.insertNodeInto(temp, parent, 0);
                                parent = temp;
                            } else {
                                parent = resultNode;
                            }
                        }

                        // TODO 3. 设置地址栏路径为选中和展开状态
                        TreePath tempPath = new TreePath(treeModel.getPathToRoot(parent));
                        myTree.setSelectionPath(tempPath);
                        myTree.scrollPathToVisible(tempPath);
                        myTree.expandPath(tempPath);
                        log.debug("执行了个寂寞");
                    } else {
                        JOptionPane.showMessageDialog(this, "远程路径不存在：\n" + quickPath, "警告", JOptionPane.WARNING_MESSAGE);
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);

        // fileTransfer.svg
        transferPopMenu = new JPopupMenu();
        JButton fileTransfer = new JButton();
        fileTransfer.setIcon(new FlatSVGIcon("icons/fileTransfer.svg"));
        fileTransfer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                transferPopMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        // 添加工具栏菜单
        toolBar.add(pathField);
        toolBar.add(waitBar);
        toolBar.addSeparator();
        toolBar.add(fileTransfer);
        this.add(toolBar, BorderLayout.NORTH);
    }

    private TreePath findTreePath(DefaultMutableTreeNode root, String s) {
        @SuppressWarnings("unchecked")
        Enumeration<TreeNode> e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (node.toString().equalsIgnoreCase(s)) {
                return new TreePath(node.getPath());
            }
        }
        return null;
    }

    private DefaultMutableTreeNode findTreeNode(DefaultMutableTreeNode root, String s) {
        @SuppressWarnings("unchecked")
        Enumeration<TreeNode> e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (node.toString().equalsIgnoreCase(s)) {
                return node;
            }
        }
        return null;
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
    }

    private void initTree() {
        // TODO 设置叶子节点样式
        DefaultTreeCellRenderer render = new DefaultTreeCellRenderer();
        render.setLeafIcon(new FlatTreeClosedIcon());
        myTree.setCellRenderer(render);

        DefaultMutableTreeNode child;
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
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
        myTable.getColumn("权限").setCellRenderer(centerRenderer);
        myTable.getColumn("大小").setCellRenderer(rightRenderer);
        myTable.getColumn("类型").setCellRenderer(centerRenderer);
        myTable.getColumn("属组").setCellRenderer(centerRenderer);
        myTable.getColumn("修改时间").setCellRenderer(centerRenderer);
    }

    public SftpTabbedPanel getSftpTabbedPanel() {
        return sftpTabbedPanel;
    }

    public EditorFrame getEditorFrame() {
        return editorFrame;
    }

    public MyTree getMyTree() {
        return myTree;
    }

    public void setMyTree(MyTree myTree) {
        this.myTree = myTree;
    }

    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }

    public void setTreeModel(DefaultTreeModel treeModel) {
        this.treeModel = treeModel;
    }

    public MyTable getMyTable() {
        return myTable;
    }

    public void setMyTable(MyTable myTable) {
        this.myTable = myTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
    }

    private AbstractAction uploadsAction = new AbstractAction("上传(s)") {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("上传(s)");
            String path = "";
            if (myTree.isSelectionEmpty()) {
                try {
                    // TODO 默认上传至用户目录 getDefaultDir()
                    path = fs.getDefaultDir().toRealPath().toString();
                    log.debug("默认用户目录：" + path);
                    log.debug(path);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } else {
                TreePath dstPath = myTree.getSelectionPath();
                path = convertTreePathToString(dstPath);
            }
            // TODO 获取上传文件路径
            LinkedList<File> chooserFile = new LinkedList<>();
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setMultiSelectionEnabled(true);
            chooser.setDialogTitle("选择上传路径");
            int value = chooser.showOpenDialog(SftpBrowser.this);
            if (value == JFileChooser.APPROVE_OPTION) {
                File[] files = chooser.getSelectedFiles();
                LinkedList<File> fileList = new LinkedList<>();
                for (File file : files) {
                    chooserFile.add(file);
                    FileUtil.traverseFolder(file, fileList);
                }
                TaskProgressPanel taskPanel = new TaskProgressPanel("上传", 0, 100, "");
                transferPopMenu.add(taskPanel);
                String tmpPath = path;
                new Thread(() -> {
                    int fileCount = fileList.size();
                    for (File file : fileList) {
                        log.debug(file.getAbsolutePath());
                        taskPanel.setFileCount(fileCount);
                        taskPanel.setTaskLabel(file.getAbsolutePath());
                        taskPanel.setMin(0);
                        taskPanel.setMax((int) file.length());
                        try {
                            for (File f : chooserFile) {
                                if (file.getAbsolutePath().contains(f.getAbsolutePath())) {
                                    finalPath = Path.of(tmpPath, file.getAbsolutePath().substring(f.getParent().length())).toString();
                                }
                            }
                            if (Files.isDirectory(fs.getPath(finalPath))) {
                                Files.createDirectories(fs.getPath(finalPath));
                            } else {
                                Files.createDirectories(fs.getPath(finalPath).getParent());
                            }
                            FileInputStream fis = new FileInputStream(file);
                            OutputStream outputStream = Files.newOutputStream(fs.getPath(finalPath));
                            byte data[] = new byte[1024 * 8];   // 缓冲区
                            int len = 0;        // 创建长度
                            int sendLen = 0;    // 已发送长度
                            while ((len = fis.read(data)) != -1) {
                                outputStream.write(data, 0, len);
                                outputStream.flush();
                                sendLen += len;
                                taskPanel.setProgressBarValue(sendLen);
                            }
                            fileCount -= 1;
                        } catch (IOException fileNotFoundException) {
                            fileNotFoundException.printStackTrace();
                        }
                    }
                    transferPopMenu.remove(taskPanel);
                }).start();
            }
        }
    };

    private AbstractAction downloadsAction = new AbstractAction("下载(s)") {
        @SneakyThrows
        @Override
        public void actionPerformed(ActionEvent e) {
            log.debug("下载文件...");
            // 至少选中目录，才能开始下载
            if (myTree.isSelectionEmpty()) {
                JOptionPane.showMessageDialog(SftpBrowser.this, "请先选择文件目录", "警告", JOptionPane.WARNING_MESSAGE);
            } else {
                if (myTable.getSelectedRowCount() < 1) {
                    // TODO 批量下载（单目录、多目录下载）
                    int yesOrNo = JOptionPane.showConfirmDialog(SftpBrowser.this, "是否确认整目录下载？", "提示", JOptionPane.YES_NO_OPTION);
                    if (yesOrNo == 0) {
                        log.debug("================= 确认整目录下载 ================");
                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        chooser.setMultiSelectionEnabled(false);
                        chooser.setDialogTitle("选择保存路径");
                        int value = chooser.showOpenDialog(SftpBrowser.this);
                        if (value == JFileChooser.APPROVE_OPTION) {
                            File outputFile = chooser.getSelectedFile();
                            // TODO 获取下载文件路径
                            for (TreePath dstPath : myTree.getSelectionPaths()) {
                                String path = convertTreePathToString(dstPath);
                                downloadRemoteDir(path, outputFile);
                            }
                        }
                    }
                } else {
                    // TODO 文件下载（单文件，多文件下载）
                    log.debug("选中文件数：" + myTable.getSelectedRowCount());

                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.setMultiSelectionEnabled(false);
                    chooser.setDialogTitle("选择保存路径");
                    int value = chooser.showOpenDialog(SftpBrowser.this);
                    if (value == JFileChooser.APPROVE_OPTION) {
                        File outputFile = chooser.getSelectedFile();
                        log.debug(outputFile.getAbsolutePath());

                        TaskProgressPanel taskPanel = new TaskProgressPanel("下载", 0, 100, "");
                        transferPopMenu.add(taskPanel);
                        AtomicInteger fileCount = new AtomicInteger(myTable.getSelectedRows().length);
                        taskPanel.setFileCount(fileCount.get());
                        // TODO 下载, 进度, 下载5M分段
                        new Thread(() -> {
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
                                try {
                                    log.info("开始下载：" + path);
                                    BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destPath));
                                    InputStream inputStream = Files.newInputStream(downloadPath);
                                    log.debug("文件大小：" + Files.size(fs.getPath(path)));

                                    taskPanel.setTaskLabel(fs.getPath(path).toString());
                                    taskPanel.setMin(0);
                                    taskPanel.setMax((int) Files.size(fs.getPath(path)));
                                    byte[] buf = new byte[1024 * 8];
                                    int bytesRead;
                                    int sendLen = 0;
                                    while ((bytesRead = inputStream.read(buf)) != -1) {
                                        outputStream.write(buf, 0, bytesRead);
                                        sendLen += bytesRead;
                                        taskPanel.setProgressBarValue(sendLen);
                                    }
                                    outputStream.flush();   //
                                    outputStream.close();
                                    inputStream.close();
                                    int count = fileCount.addAndGet(-1);
                                    taskPanel.setFileCount(count);
                                    log.info("下载完成：" + destPath);
                                } catch (IOException fileNotFoundException) {
                                    fileNotFoundException.printStackTrace();
                                }
                            }
                            transferPopMenu.remove(taskPanel);
                        }).start();
                    }
                }
            }
        }
    };

    private void downloadRemoteDir(String path, File outputFile) {
        new Thread(() -> {
            TaskProgressPanel taskPanel = new TaskProgressPanel("下载", 0, 100, "");
            transferPopMenu.add(taskPanel);
            AtomicInteger fileCount = new AtomicInteger(0);
            taskPanel.setFileCount("下载完成：" + fileCount.get());
            try {
                Files.walkFileTree(fs.getPath(path), new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        String to = outputFile.getPath() + "/" + fs.getPath(path).getParent().relativize(file);
                        Path toPath = Path.of(to);
                        log.debug("visitFile: " + file.toString());
                        log.debug("toPath: " + toPath.toString());

                        if (Files.isDirectory(toPath)) {
                            Files.createDirectories(toPath);
                        } else {
                            Files.createDirectories(toPath.getParent());
                        }
                        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(toPath.toFile()));
                        InputStream inputStream = Files.newInputStream(file);
                        log.debug("文件大小：" + Files.size(fs.getPath(path)));

                        taskPanel.setTaskLabel(file.toString());
                        taskPanel.setMin(0);
                        taskPanel.setMax((int) Files.size(file));
                        byte[] buf = new byte[1024 * 8];
                        int bytesRead;
                        int sendLen = 0;
                        while ((bytesRead = inputStream.read(buf)) != -1) {
                            outputStream.write(buf, 0, bytesRead);
                            sendLen += bytesRead;
                            taskPanel.setProgressBarValue(sendLen);
                        }
                        outputStream.flush();   //
                        outputStream.close();
                        inputStream.close();
                        int count = fileCount.addAndGet(+1);
                        taskPanel.setFileCount("下载完成：" + count);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            transferPopMenu.remove(taskPanel);
        }).start();
    }

    private Iterable<SftpClient.DirEntry> getDirEntry(String path) {
        Iterable<SftpClient.DirEntry> entry = null;
        try {
            entry = this.sftpClient.readDir(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entry;
    }

    public String convertTreePathToString(TreePath treePath) {
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

        if (tempPath.toString().startsWith("//"))
            return tempPath.substring(1);
        return tempPath.toString();
    }

    private String[] convertFileLongNameToStringArray(SftpClient.DirEntry entry) {
        // 文件名, 权限, 大小, 类型, 属组, 修改时间
        String[] temp = new String[6];
        temp[0] = entry.getFilename();
        temp[1] = entry.getLongFilename().split("\\s+")[0];
        log.debug(Arrays.toString(entry.getLongFilename().split("\\s+")));

        // 大小单位转换
        String humanSize = "";
        long size = entry.getAttributes().getSize();
        if (size >= 1024 && size < 1024 * 1024) { // KB
            double d = size / 1024.0;
            humanSize = String.format("%.2f", d) + " KB";
        } else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {   // MB
            double d = size / 1024.0 / 1024.0;
            humanSize = String.format("%.2f", d) + " MB";
        } else if (size >= 1024 * 1024 * 1024) {  // GB
            double d = size / 1024.0 / 1024.0 / 1024.0;
            humanSize = String.format("%.2f", d) + " GB";
        }
        temp[2] = humanSize;

        /**
         *     public static final int SSH_FILEXFER_TYPE_REGULAR = 1;
         *     public static final int SSH_FILEXFER_TYPE_DIRECTORY = 2;
         *     public static final int SSH_FILEXFER_TYPE_SYMLINK = 3;
         *     public static final int SSH_FILEXFER_TYPE_SPECIAL = 4;
         *     public static final int SSH_FILEXFER_TYPE_UNKNOWN = 5;
         *     public static final int SSH_FILEXFER_TYPE_SOCKET = 6; // v5
         *     public static final int SSH_FILEXFER_TYPE_CHAR_DEVICE = 7; // v5
         *     public static final int SSH_FILEXFER_TYPE_BLOCK_DEVICE = 8; // v5
         *     public static final int SSH_FILEXFER_TYPE_FIFO = 9; // v5
         */
        switch (entry.getAttributes().getType()) {
            case SftpConstants.SSH_FILEXFER_TYPE_REGULAR:
                temp[3] = "Regular";
                break;
            case SftpConstants.SSH_FILEXFER_TYPE_DIRECTORY:
                temp[3] = "Directory";
                break;
            case SftpConstants.SSH_FILEXFER_TYPE_SYMLINK:
                temp[3] = "Symlink";
                break;
            case SftpConstants.SSH_FILEXFER_TYPE_SPECIAL:
                temp[3] = "Special";
                break;
            case SftpConstants.SSH_FILEXFER_TYPE_UNKNOWN:
                temp[3] = "Unknown";
                break;
            case SftpConstants.SSH_FILEXFER_TYPE_SOCKET:
                temp[3] = "Socket";
                break;
            case SftpConstants.SSH_FILEXFER_TYPE_CHAR_DEVICE:
                temp[3] = "Char_Device";
                break;
            case SftpConstants.SSH_FILEXFER_TYPE_BLOCK_DEVICE:
                temp[3] = "Block_Device";
                break;
            case SftpConstants.SSH_FILEXFER_TYPE_FIFO:
                temp[3] = "FIFO";
                break;
        }
        temp[4] = entry.getLongFilename().split("\\s+")[2] + "/" + entry.getLongFilename().split("\\s+")[3];
        temp[5] = entry.getAttributes().getModifyTime().toString();

        return temp;
    }

    public void freshTable() {
        // TODO 清空表中旧数据
        tableModel.setRowCount(0);

        // 获取被选中的相关节点
        TreePath path = myTree.getSelectionPath();

        DefaultMutableTreeNode currentTreeNode = (DefaultMutableTreeNode) myTree.getLastSelectedPathComponent();
        log.debug(path.toString());

        DefaultMutableTreeNode temp;
        try {
            log.debug(">>>>>>>>>>>>>>>>java.lang.NullPointerException>>>>>>>" + convertTreePathToString(path));
            for (SftpClient.DirEntry entry : getDirEntry(convertTreePathToString(path))) {
                log.debug(entry.getLongFilename());
                if (entry.getFilename().equals(".") || entry.getFilename().equals(".."))
                    continue;

                if (entry.getAttributes().isDirectory()) {
                    // TODO 当 fs 断开重连时， 判断选中节点下是否已存在相同子节点，应避免重复添加子节点
                    if (!currentTreeNode.isLeaf()) {
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
            JOptionPane.showMessageDialog(SftpBrowser.this, exception.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
        myTree.setSelectionPath(path);
        myTree.expandPath(myTree.getSelectionPath());
    }


    private static class MyTable extends JTable {

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
            this.addTreeSelectionListener(e -> {
                // 刷新文件列表
                if (waitCount.get() <= 0) {
                    new Thread(() -> {
                        addWaitProgressBar();
                        freshTable();
                        removeWaitProgressBar();
                    }).start();
                } else {
                    JOptionPane.showMessageDialog(SftpBrowser.this, "已有一个展开任务，请等待！", "警告", JOptionPane.WARNING_MESSAGE);
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
}
