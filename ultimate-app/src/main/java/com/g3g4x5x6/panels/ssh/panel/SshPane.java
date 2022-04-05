package com.g3g4x5x6.panels.ssh.panel;


import com.alibaba.fastjson.JSON;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatTreeClosedIcon;
import com.g3g4x5x6.formatter.IpAddressFormatter;
import com.g3g4x5x6.formatter.PortFormatter;
import com.g3g4x5x6.panels.ssh.SessionInfo;
import com.g3g4x5x6.utils.ConfigUtil;
import com.g3g4x5x6.utils.DialogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.config.hosts.HostConfigEntry;
import org.apache.sshd.client.session.ClientSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;


@Slf4j
public class SshPane extends JPanel {

    private BorderLayout borderLayout = new BorderLayout();
    private FlowLayout leftFlow = new FlowLayout();
    private FlowLayout rightFlow = new FlowLayout();
    private FlowLayout centerFlow = new FlowLayout();

    private JTabbedPane mainTabbedPane;

    protected JTabbedPane basicSettingTabbedPane = new JTabbedPane();
    protected String basicSettingPaneTitle;
    protected JPanel basicSettingPane = new JPanel();

    protected JTabbedPane advancedSettingTabbedPane = new JTabbedPane();
    protected String advancedSettingPaneTitle;
    protected JPanel advancedSettingPane = new JPanel();

    private JFormattedTextField hostField;
    private JFormattedTextField portField;
    private JFormattedTextField userField;
    private JPasswordField passField;
    private JTextField sessionName;
    private JTextArea commentText;
    private JLabel keyLabel;
    private JComboBox<String> categoryCombo;

    private String editPath;
    private String host;
    private int port;
    private String username;
    private String password;
    private String authType = "password";

    public SshPane(JTabbedPane tabbedPane) {
        this.setLayout(borderLayout);
        this.mainTabbedPane = tabbedPane;

        basicSettingPaneTitle = "Basic SSH Settings";
        advancedSettingPaneTitle = "Advanced SSH Settings";

        leftFlow.setAlignment(FlowLayout.LEFT);
        rightFlow.setAlignment(FlowLayout.RIGHT);
        centerFlow.setAlignment(FlowLayout.CENTER);
        initSettingPane();

        createBasicComponent();
        createAdvancedComponent();
    }

    /**
     * 初始化面板
     */
    protected void initSettingPane() {
        basicSettingPane.setLayout(leftFlow);
        basicSettingTabbedPane.addTab(basicSettingPaneTitle, basicSettingPane);
        advancedSettingTabbedPane.addTab(advancedSettingPaneTitle, advancedSettingPane);

        JPanel btnPane = new JPanel();
        btnPane.setLayout(centerFlow);
        JButton saveBtn = new JButton("保存会话");
        saveBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("保存会话");
                saveSession();
            }
        });

        // TODO Save and open session
        JButton openButton = new JButton("快速连接");
        openButton.setToolTipText("默认自动保存会话");
        JButton testButton = new JButton("测试通信");

        openButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                log.debug("快速连接");

                // TODO 测试连接
                if (testConnection() == 1) {
                    int preIndex = mainTabbedPane.getSelectedIndex();

                    String defaultTitle = sessionName.getText().equals("") ? "未命名" : sessionName.getText();
                    SessionInfo sessionInfo = new SessionInfo();
                    sessionInfo.setSessionName(sessionName.getText());
                    sessionInfo.setSessionAddress(hostField.getText());
                    sessionInfo.setSessionPort(portField.getText());
                    sessionInfo.setSessionUser(userField.getText());
                    sessionInfo.setSessionPass(String.valueOf(passField.getPassword()));
                    sessionInfo.setSessionKeyPath(keyLabel.getText());
                    sessionInfo.setSessionLoginType(authType);
                    sessionInfo.setSessionComment(commentText.getText());

                    // 鸠占鹊巢
                    mainTabbedPane.insertTab(defaultTitle,
                            new FlatSVGIcon("icons/OpenTerminal_13x13.svg"),
                            new SshTabbedPane(sessionInfo), "奥里给", preIndex);
                    mainTabbedPane.removeTabAt(preIndex + 1);
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

        btnPane.add(openButton);
        btnPane.add(testButton);
        btnPane.add(saveBtn);
        this.add(basicSettingTabbedPane, BorderLayout.NORTH);
        this.add(advancedSettingTabbedPane, BorderLayout.CENTER);
        this.add(btnPane, BorderLayout.SOUTH);
    }

    private void saveSession() {
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

        String path = ConfigUtil.getWorkPath() + "/sessions/ssh/" + Objects.requireNonNull(categoryCombo.getSelectedItem());
        String fileName = path + "/ssh_" + hostField.getText() + "_" + portField.getText() + "_" + userField.getText() + "_" + authType + ".json";
        if (editPath != null && !Path.of(fileName).toString().equalsIgnoreCase(editPath)) {
            try {
                Files.delete(Path.of(editPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("会话保存路径：" + fileName);
        try {
            File dirFile = new File(path);
            if (!dirFile.exists())
                dirFile.mkdirs();

            Files.write(Paths.get(fileName), JSON.toJSONString(session).getBytes(StandardCharsets.UTF_8));
            DialogUtil.info("会话保存成功");
        } catch (IOException e) {
            e.printStackTrace();
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
        hostField.setColumns(15);
        hostPane.add(hostLabel);
        hostPane.add(hostField);

        // TODO port
        JPanel portPane = new JPanel();
        JLabel portLabel = new JLabel("Port*");
        portField = new JFormattedTextField(new PortFormatter());
        portField.setColumns(5);
        portField.setText("22");
        portPane.add(portLabel);
        portPane.add(portField);

        // TODO user name
        JPanel userPane = new JPanel();
        JLabel userLabel = new JLabel("Username");
        userField = new JFormattedTextField();
        userField.setColumns(12);
        userPane.add(userLabel);
        userPane.add(userField);

        // TODO password
        JPanel passPane = new JPanel();
        JLabel passLabel = new JLabel("Password");
        passField = new JPasswordField();
        passField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        passField.setColumns(12);
        passPane.add(passLabel);
        passPane.add(passField);

        basicSettingPane.add(hostPane);
        basicSettingPane.add(portPane);
        basicSettingPane.add(userPane);
        basicSettingPane.add(passPane);
    }

    /**
     * 高级设置面板
     */
    private void createAdvancedComponent() {
        advancedSettingPane.setLayout(new BorderLayout());

        initAdvancedSettingPaneCenter();

        // 会话描述
        JPanel describePane = new JPanel(new BorderLayout());
        commentText = new JTextArea();
        commentText.setRows(5);
        JScrollPane scrollPane = new JScrollPane(commentText);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        describePane.add(new JLabel("  备注描述:"), BorderLayout.NORTH);
        describePane.add(scrollPane, BorderLayout.CENTER);
        advancedSettingPane.add(describePane, BorderLayout.SOUTH);
    }

    private void initAdvancedSettingPaneCenter() {
        // 会话名称
        JPanel north1 = new JPanel();
        sessionName = new JTextField();
        sessionName.setColumns(12);
        sessionName.putClientProperty("JTextField.placeholderText", "这么懒的吗？");
        north1.add(new JLabel("会话名称:"));
        north1.add(sessionName);

        // 会话分类
        JPanel categoryPane = new JPanel();
        categoryCombo = new JComboBox<>();
        categoryCombo.setEditable(true);
        categoryCombo.setSize(new Dimension(200, 25));
        categoryCombo.setPreferredSize(new Dimension(200, 25));
        categoryCombo.addItem("");
        ArrayList<String> list = new ArrayList<>();
        String sshPath = Path.of(ConfigUtil.getWorkPath(), "sessions", "ssh").toString();
        recursiveListDirectory(new File(sshPath), list);
        for (String category : list) {
            log.debug("sshPath: " + sshPath);
            log.debug("category: " + category);
            String item = category.substring(sshPath.length() + 1);
            categoryCombo.addItem(item);
        }
        categoryPane.add(new JLabel("会话分类:"));
        categoryPane.add(categoryCombo);

        // 公钥登录
        JPanel north2 = new JPanel();
        JButton keyBtn = new JButton();
        keyBtn.setIcon(new FlatTreeClosedIcon());
        keyBtn.setToolTipText("点击按钮选择私钥");
        keyLabel = new JLabel();
        keyBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                // 设置默认显示的文件夹为当前文件夹
                fileChooser.setCurrentDirectory(new File("."));
                // 设置文件选择的模式（只选文件、只选文件夹、文件和文件均可选）
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
                int result = fileChooser.showOpenDialog(SshPane.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    // 如果点击了"确定", 则获取选择的文件路径
                    File file = fileChooser.getSelectedFile();
                    keyLabel.setText(file.getAbsolutePath());
                    authType = "public";
                }
            }
        });
        north2.add(new JLabel("公钥登录:"));
        north2.add(keyBtn);
        north2.add(keyLabel);

        // TODO 细节布局在此设置
        JPanel center = new JPanel();
        leftFlow.setAlignment(FlowLayout.LEADING);
        center.setLayout(leftFlow);
        center.add(north1);
        center.add(categoryPane);
        center.add(north2);
        advancedSettingPane.add(center, BorderLayout.CENTER);
    }

    /**
     * 输出指定目录下面的文件名称，包括子目录
     */
    public void recursiveListDirectory(File directory, ArrayList<String> categoryList) {
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
                        log.debug("目录绝对路径：" + f.getAbsolutePath());
                        categoryList.add(f.getPath());
                        recursiveListDirectory(f, categoryList);
                    } else {
                        // 不是目录，判断是否是文件？
                        if (f.isFile()) {
                            log.debug("文件绝对路径：" + f.getAbsolutePath());
                        }
                    }
                }
            }
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

    public void setHostField(String host) {
        hostField.setText(host);
    }

    public void setPortField(String port) {
        portField.setText(port);
    }

    public void setUserField(String user) {
        userField.setText(user);
    }

    public void setPassField(String pass) {
        passField.setText(pass);
    }

    public void setSessionName(String session) {
        sessionName.setText(session);
    }

    public void setCategory(String category) {
        categoryCombo.setSelectedItem(category);
    }

    public void setKeyLabel(String key) {
        keyLabel.setText(key);
    }

    public void setCommentText(String text) {
        commentText.setText(text);
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public void setEditPath(String editPath) {
        this.editPath = editPath;
    }
}
