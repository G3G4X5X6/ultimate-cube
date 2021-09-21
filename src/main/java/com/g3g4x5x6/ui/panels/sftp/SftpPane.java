package com.g3g4x5x6.ui.panels.sftp;


import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.formatter.IpAddressFormatter;
import com.g3g4x5x6.ui.formatter.PortFormatter;
import com.g3g4x5x6.utils.DialogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.client.fs.SftpFileSystem;
import org.apache.sshd.sftp.client.fs.SftpFileSystemProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class SftpPane extends JPanel {

    private BorderLayout borderLayout = new BorderLayout();
    private JTabbedPane mainTabbedPane;

    private SshClient client;
    private SftpFileSystemProvider provider;
    private URI uri;
    private SftpFileSystem fs;

    private JTabbedPane basicSettingTabbedPane;
    private String basicSettingPaneTitle;
    private JPanel basicSettingPane;

    private JTabbedPane advancedSettingTabbedPane;
    private String advancedSettingPaneTitle;
    private JPanel advancedSettingPane;

    private JFormattedTextField hostField;
    private JFormattedTextField portField;
    private JTextField userField;
    private JPasswordField passField;

    public SftpPane(JTabbedPane tabbedPane) {
        this.setLayout(borderLayout);
        this.mainTabbedPane = tabbedPane;

        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);

        basicSettingTabbedPane = new JTabbedPane();
        basicSettingPane = new JPanel();
        basicSettingPane.setLayout(flowLayout);
        basicSettingPaneTitle = "Basic Sftp Settings";

        advancedSettingTabbedPane = new JTabbedPane();
        advancedSettingPane = new JPanel();
        advancedSettingPane.setLayout(flowLayout);
        advancedSettingPaneTitle = "Advanced Sftp Settings";

        initBasicPane();
        initAdvancePane();

        this.add(basicSettingTabbedPane, BorderLayout.NORTH);
        this.add(advancedSettingTabbedPane, BorderLayout.CENTER);

        initBasicPane();
        initAdvancePane();
    }

    private void initBasicPane() {
        basicSettingTabbedPane.addTab(basicSettingPaneTitle, basicSettingPane);
        // Host
        JPanel hostPane = new JPanel();
        JLabel hostLabel = new JLabel("Remote Host*");
        hostField = new JFormattedTextField(new IpAddressFormatter());
        hostField.setColumns(10);
        hostField.setText("172.17.200.104");
        hostPane.add(hostLabel);
        hostPane.add(hostField);

        // Port
        JPanel portPane = new JPanel();
        JLabel portLabel = new JLabel("Port*");
        portField = new JFormattedTextField(new PortFormatter());
        portField.setColumns(4);
        portField.setText("22");
        portPane.add(portLabel);
        portPane.add(portField);

        // User
        JPanel userPane = new JPanel();
        JLabel userLabel = new JLabel("Username");
        userField = new JTextField();
        userField.setColumns(8);
        userField.setText("security");
        userPane.add(userLabel);
        userPane.add(userField);

        // Pass
        JPanel passPane = new JPanel();
        JLabel passLabel = new JLabel("Password");
        passField = new JPasswordField();
        passField.setColumns(8);
        passField.setText("12345678");
        passPane.add(passLabel);
        passPane.add(passField);

        // 按钮
        JPanel btnPane = new JPanel();
        JButton saveBtn = new JButton("快速连接");
        saveBtn.setToolTipText("自动保存会话");
        saveBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Telnet 快速连接");
                if (testConnection()) {
                    // TODO 保存会话

                    // 打开会话
                    mainTabbedPane.insertTab("SFTP-" + hostField.getText(), new FlatSVGIcon("com/g3g4x5x6/ui/icons/folders.svg"),
                            new SftpBrowser(hostField.getText(), Integer.parseInt(portField.getText()), userField.getText(), String.valueOf(passField.getPassword())),
                            "SFTP-" + hostField.getText(),
                            mainTabbedPane.getSelectedIndex());
                    mainTabbedPane.removeTabAt(mainTabbedPane.getSelectedIndex());
                } else {
                    DialogUtil.warn("SFTP is not Open!");
                }
            }
        });

        JButton testBtn = new JButton("测试通信");
        testBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("SFTP 测试通信");
                log.debug(hostField.getText() + " / " + portField.getText() + " / " + userField.getText() + " / " + passField.getText());
                log.debug(hostField.getText());
//                if (testConnection()) {
//                    DialogUtil.info("SFTP 连接测试成功!");
//                } else {
//                    DialogUtil.warn("SFTP 连接测试失败!");
//                }
            }
        });
        btnPane.add(saveBtn);
        btnPane.add(testBtn);

        basicSettingPane.add(hostPane);
        basicSettingPane.add(Box.createHorizontalGlue());
        basicSettingPane.add(portPane);
        basicSettingPane.add(Box.createHorizontalGlue());
        basicSettingPane.add(userPane);
        basicSettingPane.add(Box.createHorizontalGlue());
        basicSettingPane.add(passPane);
        basicSettingPane.add(Box.createHorizontalGlue());
        basicSettingPane.add(btnPane);
    }

    private void initAdvancePane() {
        advancedSettingTabbedPane.addTab(advancedSettingPaneTitle, advancedSettingPane);

    }

    public SftpFileSystem createFileSystem() {
        log.debug(hostField.getText() + " / " + Integer.parseInt(portField.getText()) + " / " + userField.getText() + " / " + String.valueOf(passField.getPassword()));
        client = SshClient.setUpDefaultClient();
        // TODO 配置 SshClient
        // override any default configuration...
//        client.setSomeConfiguration(...);
//        client.setOtherConfiguration(...);
        client.start();

        provider = new SftpFileSystemProvider(client);
        uri = SftpFileSystemProvider.createFileSystemURI(hostField.getText(), Integer.parseInt(portField.getText()), userField.getText(), String.valueOf(passField.getPassword()));
        try {
            // TODO 配置 SftpFileSystem
            Map<String, Object> params = new HashMap<>();
//            params.put("param1", value1);
//            params.put("param2", value2);
            fs = provider.newFileSystem(uri, params);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return fs;
    }

    private Boolean testConnection() {
        boolean flag = false;

        SshClient client = SshClient.setUpDefaultClient();
        client.start();
        try {
            ClientSession session = client.connect(userField.getText(), hostField.getText(), Integer.parseInt(portField.getText()))
                    .verify(5000)
                    .getSession();
            session.addPasswordIdentity(String.valueOf(passField.getPassword())); // for password-based authentication
            flag = true;
            session.close();
            client.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return flag;
    }
}
