package com.g3g4x5x6.remote.ssh.panel;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import com.g3g4x5x6.editor.EditorFrame;
import com.g3g4x5x6.editor.EditorPanel;
import com.g3g4x5x6.exception.UserStopException;
import com.g3g4x5x6.remote.utils.FileUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.fs.SftpFileSystem;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.*;
import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import static com.g3g4x5x6.remote.utils.TableUtil.convertFileLongNameToStringArray;


@Slf4j
public class FilesBrowser extends JPanel implements MouseListener {

    private SftpFileSystem fs;

    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private final JTextField searchField;
    private JToolBar toolBar;
    private JPopupMenu popupMenu;

    private int dirCount = 0;

    private final JTextField pathField = new JTextField();
    private String currentPath = "";

    private LinkedList<String> forward = new LinkedList<>();
    private LinkedList<String> back = new LinkedList<>();

    private JButton backBtn;
    private JButton forwardBtn;

    private DropTargetListenerBrowserImpl dropTargetListener;

    private EditorFrame editorFrame;

    public FilesBrowser(SftpFileSystem fileSystem) {
        this.setLayout(new BorderLayout());
        this.fs = fileSystem;

        initTable();
        // 表格滚动面板
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        pathField.putClientProperty("JTextField.placeholderText", "/home/g3g4x5x6/Document");
        pathField.registerKeyboardAction(e -> {
            String quickPath = pathField.getText();
            // 1. 检查路径是否存在
            if (Files.exists(fs.getPath(quickPath))) {
                if (Files.isDirectory(fs.getPath(quickPath)))
                    gotoDir(quickPath);
                else
                    gotoDir(fs.getPath(quickPath).getParent().toString());
            } else {
                JOptionPane.showMessageDialog(FilesBrowser.this, "路径不存在", "警告", JOptionPane.WARNING_MESSAGE);
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_FOCUSED);

        // 初始化工具栏菜单
        initToolBar();

        // 初始化右键菜单
        initPopupMenu();

        searchField = new JTextField();
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search, Enter");
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON, new FlatSearchIcon());
        searchField.registerKeyboardAction(e -> {
                    String searchKeyWord = searchField.getText().strip();
                    sorter.setRowFilter(RowFilter.regexFilter(searchKeyWord));
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);

        this.add(toolBar, BorderLayout.WEST);
        this.add(pathField, BorderLayout.NORTH);
        this.add(searchField, BorderLayout.SOUTH);
        this.add(scrollPane, BorderLayout.CENTER);

    }

    private void initTable() {
        this.table = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
//        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // 文件名, 权限, 大小, 类型, 属组, 修改时间
        String[] columns = new String[]{"文件名", "权限", "大小", "类型", "属组", "修改时间"};
        tableModel = new DefaultTableModel();
        // 设置表头
        tableModel.setColumnIdentifiers(columns);
        table.setModel(tableModel);
        // 搜索功能
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        table.addMouseListener(this);

        // 居左
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JTextField.LEFT);

        // 居中
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JTextField.CENTER);

        // 居右
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JTextField.RIGHT);

//        table.getColumn("文件名").setCellRenderer(leftRenderer);
        table.getColumn("权限").setCellRenderer(centerRenderer);
        table.getColumn("大小").setCellRenderer(rightRenderer);
        table.getColumn("类型").setCellRenderer(centerRenderer);
        table.getColumn("属组").setCellRenderer(centerRenderer);
        table.getColumn("修改时间").setCellRenderer(centerRenderer);

        initData();
//        fitTableColumns(table);

        // 注册拖拽监听
        registerDropTarget();
    }

    private void registerDropTarget() {
        // 创建拖拽目标监听器
        dropTargetListener = new DropTargetListenerBrowserImpl(fs, currentPath);
        // 在 JTable 上注册拖拽目标监听器
        DropTarget dropTarget = new DropTarget(table, DnDConstants.ACTION_COPY_OR_MOVE, dropTargetListener, true);
    }

    @SneakyThrows
    private void initData() {
        String currentDirPath = fs.getDefaultDir().toString();
        currentPath = currentDirPath;
        pathField.setText(currentDirPath);
        for (SftpClient.DirEntry entry : fs.getClient().readDir(currentDirPath)) {
            if (entry.getFilename().equals(".") || entry.getFilename().equals(".."))
                continue;
            // 添加文件至表格
            if (entry.getAttributes().isDirectory()) {
                tableModel.insertRow(0, convertFileLongNameToStringArray(entry, true));
                dirCount++;
            } else {
                tableModel.addRow(convertFileLongNameToStringArray(entry, true));
            }
        }
        // 文件图标
        FilesBrowserTableCellRenderer fileRenderer = new FilesBrowserTableCellRenderer(dirCount);
        table.getColumn("文件名").setCellRenderer(fileRenderer);
    }

    @SneakyThrows
    private void gotoDir(String path) {
        log.debug("展开路径：" + path);
        currentPath = path;
        pathField.setText(currentPath);
        tableModel.setRowCount(0);
        dirCount = 0;
        for (SftpClient.DirEntry entry : fs.getClient().readDir(path)) {
            if (entry.getFilename().equals(".") || entry.getFilename().equals(".."))
                continue;
            // 添加文件至表格
            if (entry.getAttributes().isDirectory()) {
                tableModel.insertRow(0, convertFileLongNameToStringArray(entry, true));
                dirCount++;
            } else {
                tableModel.addRow(convertFileLongNameToStringArray(entry, true));
            }
        }
        // 文件图标
        FilesBrowserTableCellRenderer fileRenderer = new FilesBrowserTableCellRenderer(dirCount);
        table.getColumn("文件名").setCellRenderer(fileRenderer);
    }

    private void initToolBar() {
        toolBar = new JToolBar(SwingConstants.VERTICAL);
        toolBar.setAutoscrolls(true);

        JButton revertBtn = new JButton(new FlatSVGIcon("icons/reset.svg"));
        revertBtn.setToolTipText("返回上级目录");
        revertBtn.addActionListener(revertAction);

        backBtn = new JButton(new FlatSVGIcon("icons/back.svg"));
        backBtn.setToolTipText("后退");
        backBtn.addActionListener(backAction);
        backBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    configPopupMenu("back", popupMenu);
                    popupMenu.show(backBtn, e.getX(), e.getY());
                }
            }
        });

        forwardBtn = new JButton(new FlatSVGIcon("icons/forward.svg"));
        forwardBtn.setToolTipText("前进");
        forwardBtn.addActionListener(forwardAction);
        forwardBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    configPopupMenu("forward", popupMenu);
                    popupMenu.show(backBtn, e.getX(), e.getY());
                }
            }
        });

        JButton uploadBtn = new JButton(new FlatSVGIcon("icons/upload.svg"));
        uploadBtn.setToolTipText("上传");
        uploadBtn.addActionListener(uploadAction);

        JButton downloadBtn = new JButton(new FlatSVGIcon("icons/download.svg"));
        downloadBtn.setToolTipText("下载");
        downloadBtn.addActionListener(downloadAction);

        JButton refreshBtn = new JButton(new FlatSVGIcon("icons/refresh.svg"));
        refreshBtn.setToolTipText("刷新当前目录");
        refreshBtn.addActionListener(refreshAction);

        JButton addFileBtn = new JButton(new FlatSVGIcon("icons/addFile.svg"));
        addFileBtn.setToolTipText("新建文件");
        addFileBtn.addActionListener(newFileAction);

        JButton newFolderBtn = new JButton(new FlatSVGIcon("icons/newFolder.svg"));
        newFolderBtn.setToolTipText("新建目录");
        newFolderBtn.addActionListener(newDirAction);

        JButton deleteBtn = new JButton(new FlatSVGIcon("icons/deleteHovered.svg"));
        deleteBtn.setToolTipText("删除选中");
        deleteBtn.addActionListener(deleteAction);

        JButton transferTaskBtn = new JButton(new FlatSVGIcon("icons/fileTransfer.svg"));
        transferTaskBtn.setToolTipText("任务列表");
        transferTaskBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SshTabbedPane.taskPopupMenu.show(transferTaskBtn, e.getX(), e.getY());
            }
        });

        toolBar.add(revertBtn);
        toolBar.addSeparator();
        toolBar.add(backBtn);
        toolBar.add(forwardBtn);
        toolBar.addSeparator();
        toolBar.add(uploadBtn);
        toolBar.add(downloadBtn);
        toolBar.addSeparator();
        toolBar.add(refreshBtn);
        toolBar.addSeparator();
        toolBar.add(addFileBtn);
        toolBar.add(newFolderBtn);
        toolBar.addSeparator();
        toolBar.add(deleteBtn);
        toolBar.add(Box.createGlue());
        toolBar.add(transferTaskBtn);
    }

    private void initPopupMenu() {
        popupMenu = new JPopupMenu();

        JMenuItem openItem = new JMenuItem("打开文件");
        openItem.setToolTipText("打开文件");
        openItem.addActionListener(openFileAction);

        JMenuItem refreshItem = new JMenuItem("刷新");
        refreshItem.addActionListener(refreshAction);

        JMenuItem rmItem = new JMenuItem("强制删除（rm -rf）");
        rmItem.setToolTipText("慎重！");
        rmItem.addActionListener(rmAction);

        popupMenu.add(openItem);
        popupMenu.addSeparator();
        popupMenu.add(refreshItem);
        popupMenu.addSeparator();
        popupMenu.add(rmItem);
    }

    /**
     * https://www.cnblogs.com/gwq369/p/5364708.html
     */
    private void fitTableColumns(JTable myTable) {
        JTableHeader header = myTable.getTableHeader();
        int rowCount = myTable.getRowCount();

        Enumeration<TableColumn> columns = myTable.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn column = columns.nextElement();
            int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
            int width = (int) myTable.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(myTable, column.getIdentifier(), false, false, -1, col).getPreferredSize().getWidth();
            for (int row = 0; row < rowCount; row++) {
                int preferedWidth = (int) myTable.getCellRenderer(row, col).getTableCellRendererComponent(myTable, myTable.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
                width = Math.max(width, preferedWidth);
            }
            header.setResizingColumn(column); // 此行很重要
            column.setWidth(width + myTable.getIntercellSpacing().width);
        }
    }

    public void updateFs(SftpFileSystem fileSystem) {
        fs = fileSystem;
        dropTargetListener.updateFs(fs);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // 右键
        if (e.getButton() == 3) {
            popupMenu.show(FilesBrowser.this, e.getX(), e.getY() + popupMenu.getHeight() / 2);
        }
        // 双击
        if (e.getClickCount() == 2) {
            String fileName = ((String) table.getValueAt(table.getSelectedRow(), 0)).replaceFirst("DIR:", "");
            String quickPath;
            if (currentPath.endsWith("/"))
                quickPath = currentPath + fileName;
            else
                quickPath = currentPath + "/" + fileName;

            back.push(currentPath);
            // 1. 检查路径是否存在
            if (Files.exists(fs.getPath(quickPath))) {
                if (Files.isDirectory(fs.getPath(quickPath)))
                    gotoDir(quickPath);
                else
                    gotoDir(fs.getPath(quickPath).getParent().toString());
            } else {
                JOptionPane.showMessageDialog(FilesBrowser.this, "路径不存在或者连接已断开", "警告", JOptionPane.WARNING_MESSAGE);
            }
            log.debug(pathField.getText() + "/" + fileName);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private void runCommand(String command) throws IOException {
        String output = fs.getSession().executeRemoteCommand(command + "\r");
        log.debug("runCommand: " + output);
    }

    private void configPopupMenu(String flag, JPopupMenu popupMenu) {
        if (flag.equals("back")) {
            for (String item : back) {
                popupMenu.add(new AbstractAction(item) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        forward.push(currentPath);
                        back.remove(e.getActionCommand());

                        // 目录调整
                        pathField.setText(e.getActionCommand());
                        currentPath = e.getActionCommand();
                        gotoDir(currentPath);
                    }
                });
            }
        } else {
            for (String item : forward) {
                popupMenu.add(new AbstractAction(item) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        back.push(currentPath);
                        forward.remove(e.getActionCommand());

                        // 目录调整
                        pathField.setText(e.getActionCommand());
                        currentPath = e.getActionCommand();
                        gotoDir(currentPath);
                    }
                });
            }
        }
    }

    AbstractAction revertAction = new AbstractAction("返回上级目录") {
        @Override
        public void actionPerformed(ActionEvent e) {
            back.push(currentPath);
            if (!currentPath.equals("/")) {
                currentPath = fs.getPath(currentPath).getParent().toString();
                pathField.setText(currentPath);
                gotoDir(currentPath);
            } else {
                gotoDir(currentPath);
            }
        }
    };

    AbstractAction backAction = new AbstractAction("后退") {
        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO
            // 判断列表是否为空
            if (back.size() != 0) {
                log.debug("后退操作");
                // 栈操作
                String path = back.pop();
                forward.push(currentPath);

                // 目录调整
                pathField.setText(path);
                currentPath = path;
                gotoDir(path);
            }

        }
    };

    AbstractAction forwardAction = new AbstractAction("前进") {
        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO
            // 判断列表是否为空
            if (!forward.isEmpty()) {
                log.debug("前进操作");
                // 栈操作
                String path = forward.pop();
                back.push(currentPath);

                // 目录调整
                pathField.setText(path);
                currentPath = path;
                gotoDir(path);
            }
        }
    };

    AbstractAction uploadAction = new AbstractAction("上传") {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 获取上传文件路径
            LinkedList<File> chooserFile = new LinkedList<>();
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setMultiSelectionEnabled(true);
            chooser.setDialogTitle("选择上传路径");
            int value = chooser.showOpenDialog(FilesBrowser.this);
            if (value == JFileChooser.APPROVE_OPTION) {
                File[] files = chooser.getSelectedFiles();
                LinkedList<File> fileList = new LinkedList<>();
                for (File file : files) {
                    chooserFile.add(file);  // 为了对比生成远程路径
                    FileUtil.traverseFolder(file, fileList);
                }
                TaskProgressPanel taskPanel = new TaskProgressPanel("上传", 0, 100, "");
                SshTabbedPane.taskPopupMenu.add(taskPanel);
                new Thread(() -> {
                    int fileCount = fileList.size();
                    for (File file : fileList) {
                        log.debug(file.getAbsolutePath());
                        taskPanel.setFileCount(--fileCount);
                        taskPanel.setTaskLabel(file.getAbsolutePath());
                        taskPanel.setMin(0);
                        taskPanel.setMax((int) file.length());
                        try {
                            String finalPath = "";
                            // 对比生成远程路径
                            for (File f : chooserFile) {
                                if (file.getAbsolutePath().contains(f.getAbsolutePath())) {
                                    finalPath = Path.of(currentPath, file.getAbsolutePath().substring(f.getParent().length())).toString();
                                }
                            }
                            if (Files.isDirectory(fs.getPath(finalPath))) {
                                Files.createDirectories(fs.getPath(finalPath));
                            } else {
                                Files.createDirectories(fs.getPath(finalPath).getParent());
                            }
                            // 开始上传文件
                            FileInputStream fis = new FileInputStream(file);
                            OutputStream outputStream = Files.newOutputStream(fs.getPath(finalPath));
                            byte[] data = new byte[1024 * 8];   // 缓冲区
                            int len;        // 创建长度
                            int sendLen = 0;    // 已发送长度
                            while ((len = fis.read(data)) != -1) {
                                outputStream.write(data, 0, len);
                                outputStream.flush();
                                sendLen += len;
                                taskPanel.setProgressBarValue(sendLen);
                            }
                        } catch (IOException fileNotFoundException) {
                            fileNotFoundException.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    };

    AbstractAction downloadAction = new AbstractAction("下载") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (table.getSelectedRow() != -1) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setMultiSelectionEnabled(false);
                chooser.setDialogTitle("选择保存路径");
                int value = chooser.showOpenDialog(FilesBrowser.this);
                if (value == JFileChooser.APPROVE_OPTION) {
                    File outputFile = chooser.getSelectedFile();

                    // 获取待下载路径
                    LinkedList<String> downPath = new LinkedList<>();
                    int[] indexes = table.getSelectedRows();
                    for (int index : indexes) {
                        downPath.add(currentPath + "/" + tableModel.getValueAt(index, 0).toString().replaceFirst("DIR:", ""));
                    }
                    // 创建任务面板
                    TaskProgressPanel taskPanel = new TaskProgressPanel("下载", 0, 100, "");
                    SshTabbedPane.taskPopupMenu.add(taskPanel);
                    AtomicInteger fileCount = new AtomicInteger(0);
                    taskPanel.setFileCount("下载完成：" + fileCount.get());
                    new Thread(() -> {
                        // 开始下载
                        for (String path : downPath) {
                            if (Files.isDirectory(fs.getPath(path))) {
                                // 目录下载
                                try {
                                    Files.walkFileTree(fs.getPath(path), new SimpleFileVisitor<>() {
                                        @Override
                                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                            String to = outputFile.getPath() + "/" + fs.getPath(path).getParent().relativize(file);
                                            Path toPath = Path.of(to);
                                            log.debug("visitFile: " + file.toString());
                                            log.debug("toPath: " + toPath);

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
                                            if (Files.size(file) == 0) {
                                                taskPanel.setMax(1);
                                            }

                                            log.debug("setMax: " + Files.size(file));
                                            byte[] buf = new byte[1024 * 8];
                                            int bytesRead;
                                            int sendLen = 0;
                                            while ((bytesRead = inputStream.read(buf)) != -1) {
                                                outputStream.write(buf, 0, bytesRead);
                                                sendLen += bytesRead;
                                                taskPanel.setProgressBarValue(sendLen);
                                                if (taskPanel.isTerminate())
                                                    try {
                                                        throw new UserStopException("用户终止任务");
                                                    } catch (UserStopException ex) {
                                                        throw new RuntimeException(ex);
                                                    }
                                            }
                                            if (Files.size(file) == 0) {
                                                taskPanel.setProgressBarValue(1);
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
                                    log.debug(ioException.getMessage());
                                }
                            } else {
                                // 文件下载
                                try {
                                    log.info("开始下载：" + path);
                                    String to = outputFile.getPath() + "/" + fs.getPath(path).getParent().relativize(fs.getPath(path));
                                    log.debug("TO: " + to);
                                    BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(to));
                                    InputStream inputStream = Files.newInputStream(fs.getPath(path));
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

                                        if (taskPanel.isTerminate())
                                            throw new UserStopException("用户终止任务");
                                    }
                                    outputStream.flush();   //
                                    outputStream.close();
                                    inputStream.close();
                                    int count = fileCount.addAndGet(+1);
                                    taskPanel.setFileCount("下载完成：" + count);
                                    log.info("下载完成：" + path);
                                } catch (IOException | UserStopException ex) {
                                    log.debug(ex.getMessage());
                                }
                            }
                        }
                    }).start();

                }
            } else {
                JOptionPane.showMessageDialog(FilesBrowser.this, "请选择下载路径", "警告", JOptionPane.WARNING_MESSAGE);
            }
        }
    };

    AbstractAction refreshAction = new AbstractAction("刷新当前目录") {
        @Override
        public void actionPerformed(ActionEvent e) {
            gotoDir(currentPath);
        }
    };

    AbstractAction newFileAction = new AbstractAction("新建文件") {
        @Override
        public void actionPerformed(ActionEvent e) {
            String file = JOptionPane.showInputDialog(FilesBrowser.this, "请输入文件名：", "新建文件", JOptionPane.QUESTION_MESSAGE);
            try {
                log.debug("New file: " + currentPath + "/" + file);
                if (file != null && !file.equals(""))
                    Files.createFile(fs.getPath(currentPath + "/" + file));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            gotoDir(currentPath);
        }
    };

    AbstractAction newDirAction = new AbstractAction("新建目录") {
        @Override
        public void actionPerformed(ActionEvent e) {
            String dir = JOptionPane.showInputDialog(FilesBrowser.this, "请输入目录名：", "新建目录", JOptionPane.QUESTION_MESSAGE);
            try {
                if (dir != null && !dir.equals(""))
                    Files.createDirectory(fs.getPath(currentPath + "/" + dir));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            gotoDir(currentPath);
        }
    };

    AbstractAction deleteAction = new AbstractAction("删除") {
        @Override
        public void actionPerformed(ActionEvent e) {
            int[] indexes = table.getSelectedRows();
            LinkedList<String> delPath = new LinkedList<>();
            for (int i : indexes) {
                String fileName = table.getValueAt(i, 0).toString();
                if (fileName.startsWith("DIR:")) {
                    fileName = fileName.replaceFirst("DIR:", "");
                }
                delPath.add(Path.of(currentPath, fileName).toString());
            }
            try {
                for (String path : delPath) {
                    int option = JOptionPane.showConfirmDialog(FilesBrowser.this, "请确认删除：\n" + path.replace("\\", "/"), "提示", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        Files.delete(fs.getPath(path.replace("\\", "/")));
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                gotoDir(currentPath);
            }
        }
    };

    AbstractAction rmAction = new AbstractAction("强制删除") {
        @Override
        public void actionPerformed(ActionEvent e) {
            int[] indexes = table.getSelectedRows();
            LinkedList<String> delPath = new LinkedList<>();
            for (int i : indexes) {
                String fileName = table.getValueAt(i, 0).toString();
                if (fileName.startsWith("DIR:")) {
                    fileName = fileName.replaceFirst("DIR:", "");
                }
                delPath.add(Path.of(currentPath, fileName).toString());
            }
            try {
                for (String path : delPath) {
                    int option = JOptionPane.showConfirmDialog(FilesBrowser.this, "请确认强制删除：\n" + path.replace("\\", "/"), "提示", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        runCommand("rm -rf " + path.replace("\\", "/"));
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                gotoDir(currentPath);
            }
        }
    };

    AbstractAction openFileAction = new AbstractAction("打开文件") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (editorFrame == null) {
                editorFrame = EditorFrame.getInstance();
            }

            String openFileName = tableModel.getValueAt(sorter.convertRowIndexToModel(table.getSelectedRow()), 0).toString();
            String savePath = currentPath + "/" + openFileName;
            StringBuilder text = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(fs.getClient().read(savePath)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    text.append(line).append("\n");
                }
            } catch (IOException ioException) {
                log.debug(ioException.getMessage());
                JOptionPane.showMessageDialog(FilesBrowser.this, ioException.getMessage(), "警告", JOptionPane.WARNING_MESSAGE);
            }

            EditorPanel editorPanel = new EditorPanel(openFileName, savePath);
            editorPanel.setFs(fs);
            editorPanel.setSavePath(savePath);
            editorPanel.setTextArea(text.toString());
            editorFrame.addAndSelectPanel(editorPanel);
            editorFrame.setVisible(true);
        }
    };

}
