package com.g3g4x5x6.remote.ssh.panel;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatTreeClosedIcon;
import com.g3g4x5x6.AppConfig;
import com.g3g4x5x6.panel.session.SessionFileUtil;
import com.g3g4x5x6.remote.ssh.SessionInfo;
import com.g3g4x5x6.remote.utils.VaultUtil;
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
import java.util.*;


@Slf4j
public class NewSshPane extends JPanel {

    private final FlowLayout leftFlow = new FlowLayout();
    private FlowLayout rightFlow = new FlowLayout();
    private FlowLayout centerFlow = new FlowLayout();

    private final JTabbedPane mainTabbedPane;

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
    private String sessionCategory;
    private String sessionPukKey;

    public NewSshPane(JTabbedPane tabbedPane) {
        BorderLayout borderLayout = new BorderLayout();
        this.setLayout(borderLayout);
        this.mainTabbedPane = tabbedPane;

        basicSettingPaneTitle = "基本设置";
        advancedSettingPaneTitle = "高级设置";

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
        saveBtn.setIcon(new FlatSVGIcon("icons/menu-saveall.svg"));
        saveBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("保存会话");
                saveSession();
            }
        });

        // open session
        JButton openButton = new JButton("快速连接");
        openButton.setToolTipText("默认不保存会话");
        openButton.setIcon(new FlatSVGIcon("icons/rerun.svg"));
        openButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                log.debug("快速连接");
                openButton.setIcon(new FlatSVGIcon("icons/suspend.svg"));

                new Thread(() -> {
                    // TODO 测试连接
                    if (testConnection() == 1) {
                        openSession();
                    } else {
                        JOptionPane.showMessageDialog(NewSshPane.this, "连接失败", "警告", JOptionPane.WARNING_MESSAGE);
                        openButton.setIcon(new FlatSVGIcon("icons/rerun.svg"));
                    }
                }).start();
            }
        });


        JButton testButton = new JButton("测试通信");
        testButton.setIcon(new FlatSVGIcon("icons/lightning.svg"));
        testButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                log.debug("测试连接");
                testButton.setIcon(new FlatSVGIcon("icons/suspend.svg"));

                new Thread(() -> {
                    switch (testConnection()) {
                        case 0:
                            JOptionPane.showMessageDialog(NewSshPane.this, "通信失败", "警告", JOptionPane.WARNING_MESSAGE);
                            break;
                        case 1:
                            JOptionPane.showMessageDialog(NewSshPane.this, "通信成功", "信息", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        case 2:
                            JOptionPane.showMessageDialog(NewSshPane.this, "请输入主机地址！！！", "信息", JOptionPane.INFORMATION_MESSAGE);
                            break;
                    }
                    testButton.setIcon(new FlatSVGIcon("icons/lightning.svg"));
                }).start();
            }
        });

        JButton openAndSaveBtn = new JButton("保存并连接");
        openAndSaveBtn.setIcon(new FlatSVGIcon("icons/connectionStatus.svg"));
        openAndSaveBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("保存并连接");

                // 保存
                saveSession();

                // 连接
                openAndSaveBtn.setIcon(new FlatSVGIcon("icons/suspend.svg"));
                new Thread(() -> {
                    // 测试连接
                    if (testConnection() == 1) {
                        openSession();
                        openAndSaveBtn.setIcon(new FlatSVGIcon("icons/connectionStatus.svg"));
                    } else {
                        JOptionPane.showMessageDialog(NewSshPane.this, "连接失败", "警告", JOptionPane.WARNING_MESSAGE);
                        openAndSaveBtn.setIcon(new FlatSVGIcon("icons/connectionStatus.svg"));
                    }
                }).start();
            }
        });

        btnPane.add(openButton);
        btnPane.add(testButton);
        btnPane.add(saveBtn);
        btnPane.add(openAndSaveBtn);
        this.add(basicSettingTabbedPane, BorderLayout.NORTH);
        this.add(advancedSettingTabbedPane, BorderLayout.CENTER);
        this.add(btnPane, BorderLayout.SOUTH);
    }

    private void saveSession() {
        LinkedHashMap<String, String> session = new LinkedHashMap<>();
        if (sessionName.getText().isBlank()) {
            // 默认会话名称：Host_Port_User_LoginType
            session.put("sessionName", hostField.getText() + "_" + portField.getText() + "_" + userField.getText() + "_" + authType);
        } else {
            session.put("sessionName", sessionName.getText());
        }
        session.put("sessionCategory", Objects.requireNonNull(categoryCombo.getSelectedItem()).toString());
        session.put("sessionProtocol", "SSH");
        session.put("sessionAddress", hostField.getText());
        session.put("sessionPort", portField.getText());
        session.put("sessionUser", userField.getText());
        session.put("sessionPass", VaultUtil.encryptPasswd(String.valueOf(passField.getPassword())));
        if (sessionPukKey != null && !sessionPukKey.isBlank()) {
            session.put("sessionPukKey", sessionPukKey);
        } else {
            session.put("sessionPukKey", getKeyContentFromPath(keyLabel.getText()));
        }
        session.put("sessionLoginType", authType);
        session.put("sessionComment", commentText.getText());


        if (editPath == null) {
            // 保存会话路径
            Path sessionPath = Paths.get(AppConfig.getSessionPath(), "SSH");
            Path sessionFile = sessionPath.resolve(UUID.randomUUID() + ".json");
            editPath = sessionFile.toString();
        }
        log.info("会话保存路径：{}", editPath);
        try {
            Files.write(Path.of(editPath), JSON.toJSONString(session).getBytes(StandardCharsets.UTF_8));
            JOptionPane.showMessageDialog(NewSshPane.this, "会话保存成功", "信息", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            log.error(e.getMessage());
            JOptionPane.showMessageDialog(NewSshPane.this, "会话保存失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getKeyContentFromPath(String path) {
        log.debug(path);
        Path keyPath = Path.of(path);
        if (path.isEmpty() || !Files.exists(keyPath)) {
            return "";
        }
        try {
            return Files.readString(keyPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void openSession() {
        int preIndex = mainTabbedPane.getSelectedIndex();

        String defaultTitle = sessionName.getText().equals("") ? "未命名" : sessionName.getText();
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setSessionName(sessionName.getText());
        sessionInfo.setSessionAddress(hostField.getText());
        sessionInfo.setSessionPort(portField.getText());
        sessionInfo.setSessionUser(userField.getText());
        sessionInfo.setSessionPass(String.valueOf(passField.getPassword()));
        sessionInfo.setSessionPukKey(sessionPukKey);
        sessionInfo.setSessionLoginType(authType);
        sessionInfo.setSessionComment(commentText.getText());

        // 鸠占鹊巢
        mainTabbedPane.insertTab(defaultTitle, new FlatSVGIcon("icons/consoleRun.svg"), new SshTabbedPane(sessionInfo), "奥里给", preIndex);
        mainTabbedPane.removeTabAt(preIndex + 1);
        mainTabbedPane.setSelectedIndex(preIndex);
    }

    /**
     * 基本设置面板
     */
    private void createBasicComponent() {
        // TODO host address
        JPanel hostPane = new JPanel();
        JLabel hostLabel = new JLabel("Remote Host*");
        hostField = new JFormattedTextField();
        hostField.setColumns(15);
        hostPane.add(hostLabel);
        hostPane.add(hostField);

        // TODO port
        JPanel portPane = new JPanel();
        JLabel portLabel = new JLabel("Port*");
        portField = new JFormattedTextField();
        portField.setColumns(5);
        portField.setText("22");
        portPane.add(portLabel);
        portPane.add(portField);

        // TODO user name
        JPanel userPane = new JPanel();
        JLabel userLabel = new JLabel("Username");
        userField = new JFormattedTextField();
        userField.setColumns(10);
        userPane.add(userLabel);
        userPane.add(userField);

        // TODO password
        JPanel passPane = new JPanel();
        JLabel passLabel = new JLabel("Password");
        passField = new JPasswordField();
        passField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        passField.setColumns(20);
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
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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
        sessionName.setColumns(35);
        sessionName.putClientProperty("JTextField.placeholderText", "Default: Host_Port_User_LoginType");
        north1.add(new JLabel("会话名称:"));
        north1.add(sessionName);

        // 会话分类
        JPanel categoryPane = new JPanel();
        categoryCombo = new JComboBox<>();
        categoryCombo.setEditable(true);
        categoryCombo.setMinimumSize(new Dimension(250, 25));
        categoryCombo.setSize(new Dimension(250, 25));
        categoryCombo.setPreferredSize(new Dimension(250, 25));

        HashMap<String, ArrayList<JSONObject>> categoriesMap = SessionFileUtil.getCategoriesMap();
        ArrayList<String> categoryList = new ArrayList<>(categoriesMap.keySet());
        for (String category : categoryList) {
            categoryCombo.addItem(category);
        }
        if (categoryCombo.getItemCount() <= 0) {
            categoryCombo.addItem("");
        }
        categoryCombo.setSelectedItem(Objects.requireNonNullElse(sessionCategory, ""));

        categoryPane.add(new JLabel("会话分类:"));
        categoryPane.add(categoryCombo);

        // 密钥登陆
        JPanel north2 = new JPanel();
        JButton keyBtn = new JButton();
        keyBtn.setIcon(new FlatTreeClosedIcon());
        if (sessionPukKey != null && !sessionPukKey.isBlank()) {
            keyBtn.setToolTipText(sessionPukKey);
        }
        keyBtn.setToolTipText("点击按钮选择私钥");
        keyLabel = new JLabel();
        keyBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                // 设置默认显示的文件夹为当前文件夹
                fileChooser.setCurrentDirectory(new File(AppConfig.getWorkPath()));
                // 设置文件选择的模式（只选文件、只选文件夹、文件和文件均可选）
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
                int result = fileChooser.showOpenDialog(NewSshPane.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    // 如果点击了"确定", 则获取选择的文件路径
                    File file = fileChooser.getSelectedFile();
                    keyLabel.setText(file.getAbsolutePath());
                    authType = "public";

                    try {
                        sessionPukKey = Files.readString(file.toPath());
                        keyLabel.setToolTipText(sessionPukKey);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        north2.add(new JLabel("密钥登陆:"));
        north2.add(keyBtn);
        north2.add(keyLabel);

        // TODO 细节布局在此设置
        JPanel center = new JPanel();
        leftFlow.setAlignment(FlowLayout.LEADING);
        center.setLayout(leftFlow);
        center.add(north1);
        center.add(categoryPane);
        center.add(Box.createGlue());
        center.add(north2);
        advancedSettingPane.add(center, BorderLayout.CENTER);
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
        sessionCategory = category;
    }

    public void setPukKey(String key) {
        sessionPukKey = key;
        keyLabel.setText("在此查看密钥信息");
        keyLabel.setToolTipText(sessionPukKey);
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
