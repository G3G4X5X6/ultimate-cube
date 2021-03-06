package com.g3g4x5x6.ssh.panel;


import com.alibaba.fastjson.JSON;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatTreeClosedIcon;
import com.g3g4x5x6.ssh.SessionInfo;
import com.g3g4x5x6.utils.DialogUtil;
import com.g3g4x5x6.utils.ShellConfig;
import com.g3g4x5x6.utils.VaultUtil;
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

    public NewSshPane(JTabbedPane tabbedPane) {
        BorderLayout borderLayout = new BorderLayout();
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
     * ???????????????
     */
    protected void initSettingPane() {
        basicSettingPane.setLayout(leftFlow);
        basicSettingTabbedPane.addTab(basicSettingPaneTitle, basicSettingPane);
        advancedSettingTabbedPane.addTab(advancedSettingPaneTitle, advancedSettingPane);

        JPanel btnPane = new JPanel();
        btnPane.setLayout(centerFlow);
        JButton saveBtn = new JButton("????????????");
        saveBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("????????????");
                saveSession();
            }
        });

        // TODO Save and open session
        JButton openButton = new JButton("????????????");
        openButton.setToolTipText("????????????????????????");
        JButton testButton = new JButton("????????????");

        openButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                log.debug("????????????");

                // TODO ????????????
                if (testConnection() == 1) {
                    int preIndex = mainTabbedPane.getSelectedIndex();

                    String defaultTitle = sessionName.getText().equals("") ? "?????????" : sessionName.getText();
                    SessionInfo sessionInfo = new SessionInfo();
                    sessionInfo.setSessionName(sessionName.getText());
                    sessionInfo.setSessionAddress(hostField.getText());
                    sessionInfo.setSessionPort(portField.getText());
                    sessionInfo.setSessionUser(userField.getText());
                    sessionInfo.setSessionPass(VaultUtil.decryptPasswd(String.valueOf(passField.getPassword())));
                    sessionInfo.setSessionKeyPath(keyLabel.getText());
                    sessionInfo.setSessionLoginType(authType);
                    sessionInfo.setSessionComment(commentText.getText());

                    // ????????????
                    mainTabbedPane.insertTab(defaultTitle,
                            new FlatSVGIcon("icons/OpenTerminal_13x13.svg"),
                            new SshTabbedPane(sessionInfo), "?????????", preIndex);
                    mainTabbedPane.removeTabAt(preIndex + 1);
                    mainTabbedPane.setSelectedIndex(preIndex);
                } else {
                    JOptionPane.showMessageDialog(NewSshPane.this, "????????????", "??????", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        testButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                log.debug("????????????");
                switch (testConnection()) {
                    case 0:
                        DialogUtil.warn(NewSshPane.this, "????????????");
                        break;
                    case 1:
                        DialogUtil.info(NewSshPane.this, "????????????");
                        break;
                    case 2:
                        DialogUtil.info(NewSshPane.this, "??????????????????????????????");
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
        session.put("sessionPass", VaultUtil.encryptPasswd(String.valueOf(passField.getPassword())));
        session.put("sessionKeyPath", keyLabel.getText());
        session.put("sessionLoginType", authType);
        session.put("sessionComment", commentText.getText());
        log.debug("Comment: " + commentText.getText());

        String path = ShellConfig.getWorkPath() + "/sessions/ssh/" + Objects.requireNonNull(categoryCombo.getSelectedItem());
        String fileName = path + "/ssh_" + hostField.getText() + "_" + portField.getText() + "_" + userField.getText() + "_" + authType + ".json";
        if (editPath != null && !Path.of(fileName).toString().equalsIgnoreCase(editPath)) {
            try {
                Files.delete(Path.of(editPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("?????????????????????" + fileName);
        try {
            File dirFile = new File(path);
            if (!dirFile.exists())
                dirFile.mkdirs();

            Files.write(Paths.get(fileName), JSON.toJSONString(session).getBytes(StandardCharsets.UTF_8));
            JOptionPane.showMessageDialog(NewSshPane.this, "??????????????????", "??????", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(NewSshPane.this, "??????????????????", "??????", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * ??????????????????
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
     * ??????????????????
     */
    private void createAdvancedComponent() {
        advancedSettingPane.setLayout(new BorderLayout());

        initAdvancedSettingPaneCenter();

        // ????????????
        JPanel describePane = new JPanel(new BorderLayout());
        commentText = new JTextArea();
        commentText.setRows(5);
        JScrollPane scrollPane = new JScrollPane(commentText);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        describePane.add(new JLabel("  ????????????:"), BorderLayout.NORTH);
        describePane.add(scrollPane, BorderLayout.CENTER);
        advancedSettingPane.add(describePane, BorderLayout.SOUTH);
    }

    private void initAdvancedSettingPaneCenter() {
        // ????????????
        JPanel north1 = new JPanel();
        sessionName = new JTextField();
        sessionName.setColumns(12);
        sessionName.putClientProperty("JTextField.placeholderText", "??????????????????");
        north1.add(new JLabel("????????????:"));
        north1.add(sessionName);

        // ????????????
        JPanel categoryPane = new JPanel();
        categoryCombo = new JComboBox<>();
        categoryCombo.setEditable(true);
        categoryCombo.setSize(new Dimension(200, 25));
        categoryCombo.setPreferredSize(new Dimension(200, 25));
        categoryCombo.addItem("");
        ArrayList<String> list = new ArrayList<>();
        String sshPath = Path.of(ShellConfig.getWorkPath(), "sessions", "ssh").toString();
        recursiveListDirectory(new File(sshPath), list);
        for (String category : list) {
            log.debug("sshPath: " + sshPath);
            log.debug("category: " + category);
            String item = category.substring(sshPath.length() + 1);
            categoryCombo.addItem(item);
        }
        categoryPane.add(new JLabel("????????????:"));
        categoryPane.add(categoryCombo);

        // ????????????
        JPanel north2 = new JPanel();
        JButton keyBtn = new JButton();
        keyBtn.setIcon(new FlatTreeClosedIcon());
        keyBtn.setToolTipText("????????????????????????");
        keyLabel = new JLabel();
        keyBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                // ????????????????????????????????????????????????
                fileChooser.setCurrentDirectory(new File("."));
                // ??????????????????????????????????????????????????????????????????????????????????????????
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                // ??????????????????????????????????????????, ???????????????????????????
                int result = fileChooser.showOpenDialog(NewSshPane.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    // ???????????????"??????", ??????????????????????????????
                    File file = fileChooser.getSelectedFile();
                    keyLabel.setText(file.getAbsolutePath());
                    authType = "public";
                }
            }
        });
        north2.add(new JLabel("????????????:"));
        north2.add(keyBtn);
        north2.add(keyLabel);

        // TODO ????????????????????????
        JPanel center = new JPanel();
        leftFlow.setAlignment(FlowLayout.LEADING);
        center.setLayout(leftFlow);
        center.add(north1);
        center.add(categoryPane);
        center.add(north2);
        advancedSettingPane.add(center, BorderLayout.CENTER);
    }

    /**
     * ?????????????????????????????????????????????????????????
     */
    public void recursiveListDirectory(File directory, ArrayList<String> categoryList) {
        // 1?????????????????????????????????????????????
        if (!directory.exists()) {
            // ????????????????????????
            return;
        }
        // 2???????????????????????????
        if (!directory.isDirectory()) {
            // ???????????????????????????????????????
            if (directory.isFile()) {
                System.out.println("?????????????????????" + directory.getAbsolutePath());
            }
        } else {
            // ??????????????????????????????????????????????????????????????????
            File[] files = directory.listFiles();
            // ?????? files ???????????????
            if (null != files) {
                // ??????????????????
                for (File f : files) {
                    // ????????????????????????
                    if (f.isDirectory()) {
                        // ?????????
                        log.debug("?????????????????????" + f.getAbsolutePath());
                        categoryList.add(f.getPath());
                        recursiveListDirectory(f, categoryList);
                    } else {
                        // ???????????????????????????????????????
                        if (f.isFile()) {
                            log.debug("?????????????????????" + f.getAbsolutePath());
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
