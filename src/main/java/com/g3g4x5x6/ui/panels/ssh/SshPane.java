package com.g3g4x5x6.ui.panels.ssh;


import com.alibaba.fastjson.JSON;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatTreeClosedIcon;
import com.g3g4x5x6.ui.formatter.IpAddressFormatter;
import com.g3g4x5x6.ui.formatter.PortFormatter;
import com.g3g4x5x6.utils.ConfigUtil;
import com.g3g4x5x6.utils.DialogUtil;
import com.g3g4x5x6.utils.SessionUtil;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.JediTermWidget;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.config.hosts.HostConfigEntry;
import org.apache.sshd.client.session.ClientSession;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;


@Slf4j
public class SshPane extends JPanel {

    private BorderLayout borderLayout = new BorderLayout();
    private FlowLayout leftFlow = new FlowLayout();

    protected JTabbedPane basicSettingTabbedPane = new JTabbedPane();
    protected String basicSettingPaneTitle;
    protected JPanel basicSettingPane = new JPanel();

    protected JTabbedPane advancedSettingTabbedPane = new JTabbedPane();
    protected String advancedSettingPaneTitle;
    protected JPanel advancedSettingPane = new JPanel();

    private JTabbedPane mainTabbedPane;
    private JSplitPane splitPane;
    private JPanel rightPane;
    private JScrollPane treeScroll;
    private JTree sessionTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode root;

    private JFormattedTextField hostField;
    private JFormattedTextField portField;
    private JFormattedTextField userField;
    private JPasswordField passField;
    private JTextField sessionName;
    private JTextArea commentText;
    private JLabel keyLabel;

    private String host;
    private int port;
    private String username;
    private String password;
    private String authType = "password";

    public SshPane(JTabbedPane mainTabbedPane) {
        this.setLayout(borderLayout);
        this.mainTabbedPane = mainTabbedPane;

        basicSettingPaneTitle = "Basic SSH Settings";
        advancedSettingPaneTitle = "Advanced SSH Settings";

        leftFlow.setAlignment(FlowLayout.LEFT);
        initSettingPane();

        createBasicComponent();
        createAdvancedComponent();
    }

    /**
     * 初始化面板
     */
    protected void initSettingPane(){
        basicSettingPane.setLayout(leftFlow);
        basicSettingTabbedPane.addTab(basicSettingPaneTitle, basicSettingPane);
        advancedSettingTabbedPane.addTab(advancedSettingPaneTitle, advancedSettingPane);

        JPanel btnPane = new JPanel();
        btnPane.setLayout(leftFlow);
        JButton saveBtn = new JButton("保存会话");
        saveBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("保存会话");
                saveSession();
            }
        });
        btnPane.add(saveBtn);

        this.add(basicSettingTabbedPane, BorderLayout.NORTH);
        this.add(advancedSettingTabbedPane, BorderLayout.CENTER);
        this.add(btnPane, BorderLayout.SOUTH);
    }

    private void saveSession(){
        LinkedHashMap<String, String> session = new LinkedHashMap<>();
        session.put("sessionName", sessionName.getText());
        session.put("sessionProtocol", "SSH");
        session.put("sessionAddress", hostField.getText());
        session.put("sessionPort", portField.getText());
        session.put("sessionUser", userField.getText());
        session.put("sessionPass", String.valueOf(passField.getPassword()));
        session.put("sessionKeyPath", keyLabel.getText());
        session.put("sessionLoginType", authType);
        session.put("sessionComment", commentText.getText());
        log.debug("Comment: " + commentText.getText());

        TreePath treePath = sessionTree.getSelectionPath();
        String currentDir = SessionUtil.convertPathToTag(treePath);
        String path = ConfigUtil.getWorkPath() + "sessions/ssh";
        if (!currentDir.equals("选中以下节点以分类")){
            path = path + currentDir.substring(currentDir.indexOf("/"));
        }
        String fileName = path + "/ssh_" + hostField.getText() +"_" + portField.getText() + "_" + userField.getText() + ".json";
        try {
            Files.write(Paths.get(fileName), JSON.toJSONString(session).getBytes(StandardCharsets.UTF_8));
            DialogUtil.info("会话保存成功");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            DialogUtil.error("会话保存失败");
        } catch (IOException exception) {
            exception.printStackTrace();
            DialogUtil.error("会话保存失败");
        }
    }

    /**
     * 基本设置面板
     */
    private void createBasicComponent() {
        // TODO host address
        JPanel hostPane = new JPanel();
        JLabel hostLabel = new JLabel("Remote Host*");
        hostField = new JFormattedTextField(new IpAddressFormatter());
        hostField.setColumns(10);
        hostField.setText("172.17.200.104");    // For testing
        hostPane.add(hostLabel);
        hostPane.add(hostField);

        // TODO port
        JPanel portPane = new JPanel();
        JLabel portLabel = new JLabel("Port*");
        portField = new JFormattedTextField(new PortFormatter());
        portField.setColumns(4);
        portField.setText("22");
        portPane.add(portLabel);
        portPane.add(portField);

        // TODO user name
        JPanel userPane = new JPanel();
        JLabel userLabel = new JLabel("Username");
        userField = new JFormattedTextField();
        userField.setText("security");
        userField.setColumns(8);
        userPane.add(userLabel);
        userPane.add(userField);

        // TODO password
        JPanel passPane = new JPanel();
        JLabel passLabel = new JLabel("Password");
        passField = new JPasswordField();
        passField.setText("123456");
        passField.setColumns(8);
        passPane.add(passLabel);
        passPane.add(passField);

        // TODO Save and open session
        JPanel savePane = new JPanel();
        JButton openButton = new JButton("快速连接");
        openButton.setToolTipText("默认自动保存会话");
        JButton testButton = new JButton("测试通信");
        savePane.add(openButton);
        savePane.add(testButton);

        basicSettingPane.add(hostPane);
        basicSettingPane.add(portPane);
        basicSettingPane.add(userPane);
        basicSettingPane.add(passPane);
        basicSettingPane.add(savePane);

        openButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                log.debug("快速连接");

                // TODO 测试连接
                if (testConnection() == 1) {
                    host = hostField.getText();
                    port = Integer.parseInt(portField.getText());
                    username = userField.getText();
                    password = String.valueOf(passField.getPassword());
                    log.debug(password);

                    String defaultTitle = hostField.getText().equals("") ? "未命名" : hostField.getText();
                    int preIndex = mainTabbedPane.getSelectedIndex();
                    // 鸠占鹊巢
                    mainTabbedPane.insertTab(defaultTitle, new FlatSVGIcon("com/g3g4x5x6/ui/icons/OpenTerminal_13x13.svg"),
                            new SshTabbedPane(mainTabbedPane, createTerminalWidget(),
                                    hostField.getText(),
                                    portField.getText(),
                                    userField.getText(),
                                    String.valueOf(passField.getPassword())), "奥里给", preIndex);
                    mainTabbedPane.removeTabAt(preIndex+1);
                    mainTabbedPane.setSelectedIndex(preIndex);
                } else {
                    DialogUtil.warn("连接失败");
                }
            }
        });

        testButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                log.debug("测试连接");
                switch (testConnection()) {
                    case 0:
                        DialogUtil.warn("连接失败");
                        break;
                    case 1:
                        DialogUtil.info("连接成功");
                        break;
                    case 2:
                        DialogUtil.info("请输入主机地址！！！");
                }
            }
        });
    }

    /**
     * 高级设置面板
     */
    private void createAdvancedComponent() {
        splitPane = new JSplitPane();
        splitPane.setDividerLocation(200);
        advancedSettingPane.setLayout(new BorderLayout());
        advancedSettingPane.add(splitPane, BorderLayout.CENTER);
        initTreePane();
        initRightPane();
    }

    private void initTreePane(){
        root = new DefaultMutableTreeNode("选中以下节点以分类");
        treeModel = new DefaultTreeModel(root);

        sessionTree = new JTree();
        sessionTree.setModel(treeModel);
        sessionTree.setSelectionPath(new TreePath(root.getPath()));
        // 设置树节点可编辑
        sessionTree.setEditable(false);

        initTreeNode();

        sessionTree.expandPath(new TreePath(root.getPath()));
        sessionTree.setSelectionPath(new TreePath(root.getPath()));
        sessionTree.setExpandsSelectedPaths(true);

        treeScroll = new JScrollPane(sessionTree);
        treeScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        treeScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        DefaultTreeCellRenderer render = new DefaultTreeCellRenderer();
        render.setLeafIcon(new FlatTreeClosedIcon());
        sessionTree.setCellRenderer(render);
        splitPane.setLeftComponent(treeScroll);
    }

    private void initTreeNode(){
        String rootPath = ConfigUtil.getWorkPath() + "sessions/ssh/";
        File dir  = new File(rootPath);
        if (!dir.exists()){
            dir.mkdir();
        }
        recursiveListDirectory(dir, root);

    }

    private void initRightPane(){
        rightPane = new JPanel(new BorderLayout());

        JPanel north1 = new JPanel();
        sessionName = new JTextField();
        sessionName.setColumns(12);
        sessionName.putClientProperty("JTextField.placeholderText", "这么懒的吗？");
        north1.add(new JLabel("会话名称:"));
//        north1.add(new JLabel("<html><strong><font style='font-family:楷体;'>会话名称:</font></strong></html>"));
        north1.add(sessionName);

        JPanel north2 = new JPanel();
        JCheckBox checkBox = new JCheckBox("公钥登录");
        checkBox.setSelected(false);
        // TODO 启用私钥
        JButton keyBtn = new JButton();
        keyBtn.setIcon(new FlatTreeClosedIcon());
        keyBtn.setEnabled(false);
        keyLabel = new JLabel("点击按钮选择私钥");
        keyBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                // 设置默认显示的文件夹为当前文件夹
                fileChooser.setCurrentDirectory(new File("."));
                // 设置文件选择的模式（只选文件、只选文件夹、文件和文件均可选）
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                // 设置是否允许多选
//                    fileChooser.setMultiSelectionEnabled(false);
//                    // 添加可用的文件过滤器（FileNameExtensionFilter 的第一个参数是描述, 后面是需要过滤的文件扩展名 可变参数）
//                    fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("zip(*.zip, *.rar)", "zip", "rar"));
//                    // 设置默认使用的文件过滤器
//                    fileChooser.setFileFilter(new FileNameExtensionFilter("image(*.jpg, *.png, *.gif)", "jpg", "png", "gif"));
                // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
                int result = fileChooser.showOpenDialog(SshPane.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    // 如果点击了"确定", 则获取选择的文件路径
                    File file = fileChooser.getSelectedFile();
                    keyLabel.setText(file.getAbsolutePath());
                }
            }
        });
        checkBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // 获取事件源（即复选框本身）
                JCheckBox checkBox = (JCheckBox) e.getSource();
                log.debug(checkBox.getText() + " 是否选中: " + checkBox.isSelected());

                if (checkBox.isSelected()) {
                    authType = "secret";
                    keyBtn.setEnabled(true);
                } else {
                    authType = "password";
                    keyBtn.setEnabled(false);
                }
            }
        });
        north2.add(checkBox);
        north2.add(keyBtn);
        north2.add(keyLabel);
        Box vBox = Box.createVerticalBox();
        vBox.setAlignmentX(Box.LEFT_ALIGNMENT);
        JPanel north = new JPanel(leftFlow);
        north.setBorder(null);
        vBox.add(north1);
        vBox.add(north2);
        north.add(vBox);

        // TODO
        JPanel centerPane = new JPanel();


        JPanel southPane = new JPanel(new BorderLayout());
        commentText = new JTextArea();
        commentText.setRows(5);
        JScrollPane scrollPane = new JScrollPane(commentText);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        southPane.add(new JLabel("   备注描述:"), BorderLayout.NORTH);
        southPane.add(scrollPane, BorderLayout.CENTER);

        rightPane.add(north, BorderLayout.NORTH);
        rightPane.add(centerPane, BorderLayout.CENTER);
        rightPane.add(southPane, BorderLayout.SOUTH);
        splitPane.setRightComponent(rightPane);
    }

    /**
     * 输出指定目录下面的文件名称，包括子目录
     */
    public void recursiveListDirectory(File directory, DefaultMutableTreeNode rootNode) {
        // 1、判断映射的目录文件是否存在？
        if (!directory.exists()) {
            // 不存在则直接返回
            return;
        }
        // 2、判断是否是目录？
        if (!directory.isDirectory()) {
            // 不是目录，判断是否是文件？
            if (directory.isFile()) {
                System.out.println("文件绝对路径：" + directory.getAbsolutePath());
            }
        } else {
            // 是目录，获取该目录下面的所有文件（包括目录）
            File[] files = directory.listFiles();
            // 判断 files 是否为空？
            if (null != files) {
                // 遍历文件数组
                for (File f : files) {
                    // 判断是否是目录？
                    if (f.isDirectory()) {
                        // 是目录
                        System.out.println("目录绝对路径：" + f.getAbsolutePath());
                        DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(f.getName());
                        rootNode.add(tempNode);
                        recursiveListDirectory(f, tempNode);
                    } else {
                        // 不是目录，判断是否是文件？
                        if (f.isFile()) {
                            System.out.println("文件绝对路径：" + f.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }
    private @NotNull JediTermWidget createTerminalWidget() {
        SshSettingsProvider sshSettingsProvider = new SshSettingsProvider();
        JediTermWidget widget = new JediTermWidget(sshSettingsProvider);
        widget.setTtyConnector(createTtyConnector());
        widget.start();
        return widget;
    }

    // TODO 创建 sFTP channel
    private @NotNull TtyConnector createTtyConnector() {
        try {
            if (username.equals("")) {
                return new MyJSchShellTtyConnector(host, port);
            }
            if (password.equals("")) {
                return new MyJSchShellTtyConnector(host, port, username);
            }
            return new MyJSchShellTtyConnector(host, port, username, password);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private int testConnection() {
        host = hostField.getText();
        port = Integer.parseInt(portField.getText());
        log.debug(host);
        log.debug(String.valueOf(port));

        HostConfigEntry hostConfigEntry = new HostConfigEntry();
        hostConfigEntry.setHostName(host);
        hostConfigEntry.setHost(host);
        hostConfigEntry.setPort(port);

        SshClient client = SshClient.setUpDefaultClient();
        client.start();

        if (host.equals("")) {
            try {
                client.close();
            } catch (IOException e) {
                log.debug(e.getMessage());
            }

            return 2;
        }
        try {
            ClientSession session = client.connect(hostConfigEntry).verify(3000).getClientSession();
            session.close();
            client.close();
            return 1;
        } catch (IOException ioException) {
            try {
                client.close();
            } catch (IOException e) {
                log.debug(e.getMessage());
            }
            log.debug(ioException.getMessage());
        }
        return 0;
    }

    // TODO 获取 sFTP channel

}
